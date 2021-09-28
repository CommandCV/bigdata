package com.myclass.demo.stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.util.Collector;

/**
 * 数据流处理
 * @author Yang
 */
public class DataStreamProcessFunction {

    /**
     * 获得流执行环境
     */
    private static final StreamExecutionEnvironment ENV = StreamExecutionEnvironment.getExecutionEnvironment();

    /**
     * 从socket中获得数据源
     */
    private static DataStream<String> data = ENV.socketTextStream("localhost", 9999);

    /**
     * 单词实体类。存放单词，单词的数量,以及最近的修改时间
     */
    public static class Word{
        String key;
        int count;
        long lastModified;

        public Word() {
        }

        public Word(String key, int count, long lastModified) {
            this.key = key;
            this.count = count;
            this.lastModified = lastModified;
        }

        @Override
        public String toString() {
            return "Word{" +
                    "key='" + key + '\'' +
                    ", count=" + count +
                    ", lastModified=" + lastModified +
                    '}';
        }
    }

    private static class MyKeyedProcessFunction
            extends KeyedProcessFunction<String, Word, Tuple2<String, Integer>>{

        /**
         * 存放键
         */
        private ValueState<Word> state;

        private long lastTimer = 0L;
        /**
         * 初始化方法，注册Word类的ValueState
         * @param parameters 参数
         */
        @Override
        public void open(Configuration parameters) {
            // 通过创建Word类型的状态
            state = getRuntimeContext()
                    .getState(new ValueStateDescriptor<>("myState", Word.class));
        }

        /**
         * 处理元素的方法
         * @param word 元素
         * @param context 上下文
         * @param collector 收集器
         */
        @Override
        public void processElement(Word word,
                                   Context context,
                                   Collector<Tuple2<String, Integer>> collector) throws Exception {
            // 从集合中获得上一次保存的元素
            Word lastWord = state.value();
            // 当集合为空时设置为当前元素
            if (lastWord == null) {
                lastWord = word;
            }else{
                // 增加单词的数量,更新最后一次修改时间
                lastWord.count++;
                lastWord.lastModified = word.lastModified;
            }

            // 如果两次更新时间间隔大于10秒或者只有一条元素则在下一秒触发定时器收集元素
            if(word.lastModified - lastTimer >= 10_000L){
                // 更新上一次的时间戳
                lastTimer = word.lastModified;
                // 从当前处理时间开始算起，创建一个新的计时器时间为下一秒
                context.timerService().registerProcessingTimeTimer(lastWord.lastModified + 1000);
            }
            // 更新集合中的元素
            state.update(lastWord);

        }

        /**
         * 触发定时器时的方法
         * @param timestamp 时间戳
         * @param context 上下文
         * @param collector 收集器
         */
        @Override
        public void onTimer(
                long timestamp,
                OnTimerContext context,
                Collector<Tuple2<String, Integer>> collector) throws Exception {
            System.out.println("定时器触发了......");
            // 从集合中获得结果
            Word word = state.value();
            // 通过收集器收集元素
            collector.collect(new Tuple2<>(word.key, word.count));
            // 清除状态
            //state.clear();
        }
    }

    /**
     * 加工方法
     */
    private static void processFunction() throws Exception {
        data.flatMap(new FlatMapFunction<String, Word>() {
            @Override
            public void flatMap(String string, Collector<Word> collector) throws Exception {
                for(String str:string.split(" ")){
                    // 将时间戳精确到秒
                    long timestamp = System.currentTimeMillis();
                    collector.collect(new Word(str, 1, timestamp));
                }
            }
        // 允许固定延迟的时间戳和水印生成器
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Word>() {
            @Override
            public long extractAscendingTimestamp(Word word) {
                return word.lastModified;
            }
        }).keyBy(x -> x.key).process(new MyKeyedProcessFunction()).print("process function");

        ENV.execute("Keyed Process Function");
    }

    public static void main(String[] args) throws Exception {
        processFunction();
    }
}
