package com.myclass.demo.batch;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * DataSet变换-常用Map和Reduce相关操作
 * @author Yang
 */
public class DataSetTransformationsBaseFunction {
    /**
     * 获得执行环境
     */
    private static ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    /**
     * 文本数据集
     */
    private static DataSet<String> text = env.readTextFile("flink/src/main/resources/batch/text");

    /**
     * 单词数据集
     */
    private static DataSet<String> word = env.readTextFile("flink/src/main/resources/common/word");

    /**
     * 以类的形式实现flatMap方法，提高复用性
     * 将一行字符串按照制表符切分并形成元组
     */
    public static class MyFlatMapFunction implements FlatMapFunction<String, Tuple2<String,Integer>> {

        @Override
        public void flatMap(String s, Collector<Tuple2<String,Integer>> collector) throws Exception {
            for(String str : s.split("\t")){
                collector.collect(new Tuple2<>(str, 1));
            }
        }
    }

    /**
     * map方法，匿名方法实现，获取一个元素进行处理最后返回一个元素
     * 输出此行文本
     */
    public static void map() throws Exception {
        // 其中第一个泛型为输入类型，第二个泛型为返回值类型
        text.map(new MapFunction<String, String>() {
            @Override
            public String map(String s) throws Exception {
                return "这一行的内容是:" + s;
            }
            // 调用print方法输出。如果只有数据集的转化，flink不会真正执行，
            // 只有触发需要shuffle的方法，例如reduce才会真正执行此次任务。
        }).print();
    }

    /**
     * map方法，Lambda表达式实现
     * 输出此行文本
     */
    public static void mapLambda() throws Exception {
        text.map((MapFunction<String, String>) s -> "这一行的内容是:" + s).print();
    }

    /**
     * flatMap方法，匿名方法实现，获取一个元素进行处理最后返回0个，一个或多个类型相同的元素
     * 将一行单词按照制表符切分并形成元组放入收集器中
     */
    public static void flatMap() throws Exception {
        word.flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                // 将一行的单词切分成单个单词并形成元组返回
                for(String str : s.split("\t")){
                    collector.collect(new Tuple2<>(str, 1));
                }
            }
        }).print();
    }

    /**
     * flatMap方法，Lambda表达式实现
     * 将一行单词按照制表符切分并形成元组放入收集器中
     */
    public static void flatMapLambda() throws Exception {
        word.flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (s, collector) -> {
            for(String str : s.split("\t")){
                collector.collect(new Tuple2<>(str,1));
            }
        }).returns(Types.TUPLE(Types.STRING,Types.INT)).print();
    }

    /**
     * flatMap方法，匿名方法实现，获取一个元素进行处理最后返回0个，一个或多个类型相同的元素
     * 将一行单词按照制表符切分并形成元组放入收集器中
     */
    public static void flatMapFunctionClass() throws Exception {
        word.flatMap(new MyFlatMapFunction()).print();
    }


    /**
     * mapPartition方法，分区进行map方法，分区个数取决于之前设置的并行度，默认为CPU核心数
     * 设置并行度并统计每个分区中的元素个数后输出。由于是并行计算所以输出时可能乱序
     */
    public static void mapPartition() throws Exception {
        word.mapPartition(new MapPartitionFunction<String, Long>() {
            @Override
            public void mapPartition(Iterable<String> iterable, Collector<Long> collector) throws Exception {
                long c = 0;
                for (String s : iterable) {
                    // 输出此行内容
                    System.out.println(s);
                    c++;

                }
                System.out.println("此分区有" + c + "个元素");
                collector.collect(c);
            }
            // 设置并行度为2，即有二个分区
        }).setParallelism(2).print();
    }

    /**
     * filter方法，按照条件过滤数据
     * 将字符串长度小于5的数据剔除
     */
    public static void filter() throws Exception {
        text.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String s) throws Exception {
                return s.length()>5;
            }
        }).print();
    }

    /**
     * project方法，投影元组中的字段，按照索引选取字段
     * 先将一行单词切分形成元组，再通过投影方法选取元组中的单词
     * Tuple2<String,Integer> --> Tuple<String>
     */
    public static void project() throws Exception {
        DataSet tuple = word.flatMap(new MyFlatMapFunction());
        tuple.project(0).print();
    }

    /**
     * reduce方法，将元素进行归并,一般配合分组使用
     * 将字符串拼接到一起
     */
    public static void reduce() throws Exception {
        text.reduce(new ReduceFunction<String>() {
            @Override
            public String reduce(String s1, String s2) throws Exception {
                // 将字符串拼接到一起
                return s1 + s2;
            }
            // 设置并发度为1
        }).setParallelism(1).print();
    }


    public static void main(String[] args) throws Exception {
        map();
        mapLambda();
        flatMap();
        flatMapLambda();
        flatMapFunctionClass();
        mapPartition();
        filter();
        project();
        reduce();
    }

}
