package com.myclass.demo.stream;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;


/**
 * 数据流窗口操作
 * @author Yang
 */
public class DataStreamWindowFunction {
    /**
     * 获得数据流执行环境
     */
    private static final StreamExecutionEnvironment ENV = StreamExecutionEnvironment.getExecutionEnvironment();

    /**
     * 从socket中获取数据
     */
    private static DataStream<String> data = ENV.socketTextStream("localhost",9999);

    /**
     * 自定义FlatMap方法，将流中的数据按照空格切分打散，初始化出现次数为1并以元组返回
     */
    private static class MyFlatMapFunction implements FlatMapFunction<String, Tuple2<String,Integer>>{

        @Override
        public void flatMap(String s, Collector<Tuple2<String,Integer>> collector) {
            for(String str:s.split(" ")){
                collector.collect(new Tuple2<>(str,1));
            }
        }
    }

    /**
     * 自定义聚合函数，统计单词出现的次数
     * 每个组分别有一个累加器，接口中的三个泛型分别为输入类型，累加器类型，输出类型
     */
    private static class MyAggregateFunction implements AggregateFunction<Tuple2<String, Integer>,Tuple2<String, Integer>,Tuple2<String, Integer>>{
        /**
         * 创建累加器，初始化单词为null，次数为1
         */
        @Override
        public Tuple2<String, Integer> createAccumulator() {
            return new Tuple2<>(null,0);
        }

        /**
         * 添加元素，设置累加器的单词，将元素的次数与累加器的值相加
         * @param tuple1 新的元素
         * @param tuple2 累加器的值
         */
        @Override
        public Tuple2<String, Integer> add(Tuple2<String, Integer> tuple1, Tuple2<String, Integer> tuple2) {
            return new Tuple2<>(tuple1.f0, tuple1.f1 + tuple2.f1);
        }
        
        /**
         * 计算结果，获得单词和出现的次数
         * @param tuple2 累加器
         */
        @Override
        public Tuple2<String, Integer> getResult(Tuple2<String, Integer> tuple2) {
            return tuple2;
        }

        /**
         * 合并累加器，将两个累加器的值合并
         * @param tuple2 上一个累加器
         * @param acc1 新的累加器
         */
        @Override
        public Tuple2<String, Integer> merge(Tuple2<String, Integer> tuple2, Tuple2<String, Integer> acc1) {
            return new Tuple2<>(tuple2.f0, tuple2.f1 + acc1.f1);
        }
    }

    /**
     * 自定义窗口加工方法，将集合中的元素收集起来
     */
    private static class MyProcessWindowFunction extends ProcessWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, String, TimeWindow>{
        /**
         * 数据加工方法
         * @param key 该组的key(keyBy指定的key)
         * @param context 下文对象，其中包含着窗口对象以及窗口状态等
         * @param iterable 可迭代集合，存放着改组的所有元素
         * @param collector 收集器,用来将处理过后的元素收集起来，形成最终的结果
         */
        @Override
        public void process(String key,
                            Context context,
                            Iterable<Tuple2<String, Integer>> iterable,
                            Collector<Tuple2<String, Integer>> collector) {
            for(Tuple2<String, Integer> tuple:iterable){
                collector.collect(tuple);
            }
        }
    }

    /**
     * 窗口归纳方法
     * 使用时间滑动窗口统计单词出现的次数
     */
    private static void reduceFunction() throws Exception {
        data.flatMap(new MyFlatMapFunction())
                .keyBy(tuple -> tuple.f0)
                // 设置时间窗口的大小为3秒，窗口滑动间隔为1秒即每秒统计一下前三秒的单词
                .window(SlidingEventTimeWindows.of(Time.seconds(3), Time.seconds(1)))
                // 统计单词出现的次数
                .reduce((tuple1, tuple2) -> new Tuple2<>(tuple1.f0, tuple1.f1 + tuple2.f1))
                .print();
        ENV.execute("Reduce Function");
    }

    /**
     * 窗口聚合方法
     * 使用自定义聚合方法统计单词出现的次数
     */
    private static void aggregateFunction() throws Exception {
        data.flatMap(new MyFlatMapFunction())
                .keyBy(tuple -> tuple.f0)
                // 设置时间窗口的大小为3秒
                .window(TumblingEventTimeWindows.of(Time.seconds(3)))
                // 使用自定义聚合方法计算单词出现的次数
                .aggregate(new MyAggregateFunction())
                .print();
                
        ENV.execute("Aggregate Function");
    }
    
    /**
     * 窗口加工方法
     * 统计单词个数。此方法单独使用无法增量聚合元素，只能将元素缓冲在内部(Iterable)，
     * 直到窗口被视为已准备好进行处理为止，即窗口结束时才能进入此方法，在此方法中聚合元素，同时归纳
     * 和加工元素，因此使用此方法实现简单的count功能效率低下
     */
    private static void processWindowFunction() throws Exception {
        data.flatMap(new MyFlatMapFunction())
                // 选取键，由于下方使用的是process function，这里用的是元组，所以必须通过KeySelector指定键
                .keyBy(t -> t.f0)
                // 设置时间窗口的大小为3秒
                .window(TumblingEventTimeWindows.of(Time.seconds(3)))
                // 窗口处理方法，方法第一个泛型为输入类型，第二个泛型为输出类型，第三个泛型为键的类型，
                // 第四个为窗口类型。
                // 注意：如果分组是使用元组索引或者字段指定的键则必须手动强制转化成正确大小的元组并提取字段
                // 即通过键选择器指定使用的键，这样才能够判断出返回值类型
                .process(new ProcessWindowFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, String, TimeWindow>() {
                    /**
                     * 数据加工方法
                     * @param key 该组的key(keyBy指定的key)
                     * @param context 下文对象，其中包含着窗口对象以及窗口状态等
                     * @param iterable 可迭代集合，存放着改组的所有元素
                     * @param collector 收集器,用来将处理过后的元素收集起来，形成最终的结果
                     */
                    @Override
                    public void process(String key, Context context, Iterable<Tuple2<String, Integer>> iterable, Collector<Tuple2<String, Integer>> collector) {
                        Integer count = 0;
                        // 遍历整个元素集合
                        for (Tuple2<String, Integer> t: iterable) {
                            count++;
                        }
                        // 窗口对象，包含此窗口的开始时间以及结束时间
                        // context.window()

                        collector.collect(new Tuple2<>(key, count));
                    }
                }).print();
        ENV.execute("Process Window Function");
    }
    
    /**
     * 具有增量聚合功能的窗口归纳方法
     * 与之前单独的窗口加工不同的是元素会先进入reduce方法进行归纳，等到此窗口结束时进入窗口加工方法，
     * 此时加工方法中包含着归纳后的元素而不是全部元素，即先将元素进行归纳，待窗口结束时将归纳结果进行
     * 加工，从而实现增量聚合。
     */
    private static void incrementalWindowAggregationWithReduceFunction() throws Exception {
        // 窗口结束时进入此方法，处理该分组归纳后的元素
        data.flatMap(new MyFlatMapFunction())
                // 选取键，由于下方使用的是process function，这里用的是元组，所以必须通过KeySelector指定键
                .keyBy(t -> t.f0)
                // 设置时间窗口的大小为5秒
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                // 先将该窗口的元素进行归纳，计算单词出现的次数
                .reduce((tuple1, tuple2) -> new Tuple2<>(tuple1.f0, tuple1.f1 + tuple2.f1), new MyProcessWindowFunction())
                .print();

        ENV.execute("Incremental Window Aggregation with ReduceFunction");
    }
    
    /**
     * 具有增量聚合功能的窗口聚合方法
     * 与增量聚合的窗口归纳方法类似，只是实现部分为聚合方法而非归纳方法
     */
    private static void incrementalWindowAggregationWithAggregateFunction() throws Exception {
        data.flatMap(new MyFlatMapFunction())
                // 选取键，由于下方使用的是process function，这里用的是元组，所以必须通过KeySelector指定键
                .keyBy(t -> t.f0)
                // 设置时间窗口的大小为5秒
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                // 先将该窗口的元素进行归纳，计算单词出现的次数
                .aggregate(new MyAggregateFunction(), new MyProcessWindowFunction()).print();
        
        ENV.execute("Incremental Window Aggregation with AggregateFunction");
    }

    public static void main(String[] args) throws Exception {
        reduceFunction();
        aggregateFunction();
        processWindowFunction();
        incrementalWindowAggregationWithReduceFunction();
        incrementalWindowAggregationWithAggregateFunction();
    }
}
