package cn.myclass.batch;

import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.aggregation.Aggregations;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

import java.util.Iterator;

/**
 * DataSet变换-分组和连接
 * @author Yang
 */
public class DataSetTransformationsGroupAndJoin {
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
     * groupBy方法，按照元组索引、Javabean字段、选择器分组
     * 将单词切分后按照单词分组，统计每个单词出现的次数
     */
    public static void groupBy() throws Exception {
        // 方法一，按照元组中的第一个索引列分组，即按照元组中的单词分组
        word.flatMap(new MyFlatMapFunction()).groupBy(0)
                .reduce(new ReduceFunction<Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> reduce(Tuple2<String, Integer> tuple1, Tuple2<String, Integer> tuple2) throws Exception {
                        // 将单词出现的次数累加
                        return new Tuple2<>(tuple1.f0, tuple1.f1 + tuple1.f1);
                    }
                }).print();
        // 方法二、如果元素为javabean的话可以按照字段名分组，例如groupBy("word") 或者 groupBy("count")
        // 方法三、按照keySelector自定义分组,这里仍然以WordCount为例，以单词分组
        /*new KeySelector<Tuple2<String,Integer>, String>(){
            @Override
            public String getKey(Tuple2<String,Integer> tuple) throws Exception {
                return tuple.f0;
            }
        };*/
    }

    /**
     * reduceGroup方法，对每个分组中的元组进行归并，可以返回一个或多个元素
     * 将相同分组的单词出现的次数进行累加
     */
    public static void groupReduce() throws Exception {
        word.flatMap(new MyFlatMapFunction()).groupBy(0)
                .reduceGroup(new GroupReduceFunction<Tuple2<String, Integer>, Tuple2<String, Integer>>() {
                    @Override
                    public void reduce(Iterable<Tuple2<String, Integer>> iterable, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        int count = 0;
                        String word = null;
                        for(Tuple2<String, Integer> tuple :iterable){
                            word = tuple.f0;
                            count += tuple.f1;
                        }
                        collector.collect(new Tuple2<>(word, count));
                    }
                }).print();
    }

    /**
     * sortGroup方法，将分组按照某个字段或者元组的某个字段排序
     * 将单词元组按照出现次数分组后按照单词排序，最后取出前5个单词。
     * 这里由于单词初始次数为1，所以按照次数分组只有一个组，相当于对一个区内的单词排序，取出前5个元素
     */
    public static void sortGroup() throws Exception {
        word.flatMap(new MyFlatMapFunction()).groupBy(1)
                .sortGroup(0, Order.ASCENDING)
                // 每个组调用一次reduce方法
                .reduceGroup(new GroupReduceFunction<Tuple2<String, Integer>, Tuple2<String, Integer>>() {
                    @Override
                    public void reduce(Iterable<Tuple2<String, Integer>> iterable, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        Iterator<Tuple2<String, Integer>> iterator = iterable.iterator();
                        int size =0 ;
                        // 迭代器中的元素为一个组中的元素
                        while (iterator.hasNext()) {
                            collector.collect(iterator.next());
                            if (++size == 5){
                                break;
                            }
                        }
                    }
                }).setParallelism(1).print();
    }

    /**
     * aggregate方法，将数据集按照某个字段进行聚合，可以求和、最小值、最大值
     * 按照单词分组后统计单词出现次数的和，最小值，最大值
     */
    public static void aggregate() throws Exception {
        word.flatMap(new MyFlatMapFunction()).groupBy(0)
                .aggregate(Aggregations.SUM,1).setParallelism(1).print();
        System.out.println("--------------");
        word.flatMap(new MyFlatMapFunction()).groupBy(0)
                .aggregate(Aggregations.MIN,1).setParallelism(1).print();
        System.out.println("--------------");
        word.flatMap(new MyFlatMapFunction()).groupBy(0)
                .aggregate(Aggregations.MAX,1).setParallelism(1).print();
    }

    /**
     * distinct方法，去除重复元素。默认去除数据集中的重复元素，可以根据元组(javabean)的字段、
     * 表达式、选择器 选择符合条件的元素去除
     * 将每一行的单词切分后去重
     */
    public static void distinct() throws Exception {
        word.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                for (String str:s.split("\t")){
                    collector.collect(str);
                }
            }
        }).distinct().print();
    }

    /**
     * join方法，将两个元组(javabean)进行连接，类似于数据库中的join连接。
     * join后的where(0),equalTo(0)方法分别表示按照第一个元组的第一列和第二个元组的第一列连接，
     * 默认是按照两个字段的值等值连接，可以通过with()方法自定义连接方法，自定义方法分为两种，
     * 一种是JoinFunction，另一种是FlatJoinFunction，区别类似于map和flatMap，即前者是返回相同
     * 个元素，后者可以返回任何个元素
     *
     * 这里相当于以下两个文本按照单词进行连接，返回单词和单词出现的总数。
     * 注意：左侧的每个元素都会与右侧符合条件的元素进行连接
     * (how,1)    连接      (and,2)
     * (are,1)              (app,1)
     * (you,1)              (are,1)
     * (app,1)              (hello,1)
     * (storm,1)            (how,1)
     * (storm,1)            (jack,1)
     * (what,1)             (spark,2)
     * (jack,1)             (storm,2)
     * (and,1)              (that,1)
     * (spark,1)            (what,1)
     * (spark,1)            (world,2)
     * (hello,1)            (you,1)
     * (world,1)
     * (world,1)
     * (and,1)
     * (that,1)
     */
    public static void join() throws Exception {
        word.flatMap(new MyFlatMapFunction())
                .join(word.flatMap(new MyFlatMapFunction()).groupBy(0).aggregate(Aggregations.SUM,1))
                .where(0)
                .equalTo(0)
                .with(new JoinFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> join(Tuple2<String, Integer> tuple1, Tuple2<String, Integer> tuple2) throws Exception {

                        return new Tuple2<>(tuple1.f0, tuple1.f1+tuple2.f1);
                    }
                }).print();
    }

    /**
     * OuterJoin方法，外连接，类似于数据库中的外连接，分为左外连接，右外连接，全外连接。
     * 与内连接不同的是外连接会保留主表的所有数据以及连接表中符合条件的数据。
     *
     * 这里是右外连接
     * (how,1)   右外连接   (and,2)
     * (are,1)              (app,1)
     * (you,1)              (are,1)
     * (app,1)              (hello,1)
     * (storm,1)            (how,1)
     * (storm,1)            (jack,1)
     * (what,1)             (spark,2)
     * (jack,1)             (storm,2)
     * (and,1)              (that,1)
     * (spark,1)            (what,1)
     * (spark,1)            (world,2)
     * (hello,1)            (you,1)
     * (world,1)
     * (world,1)
     * (and,1)
     * (that,1)
     */
    public static void outerJoin() throws Exception {
        word.flatMap(new MyFlatMapFunction())
                .rightOuterJoin(word.flatMap(new MyFlatMapFunction()).groupBy(0).aggregate(Aggregations.SUM,1))
                .where(0)
                .equalTo(0)
                .with(new JoinFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> join(Tuple2<String, Integer> tuple1, Tuple2<String, Integer> tuple2) throws Exception {

                        return new Tuple2<>(tuple1.f0, tuple1.f1+tuple2.f1);
                    }
                }).print();
    }

    /**
     * cross方法，求两个数据集的笛卡儿积，可以配合with方法使用，自定义连接方法
     * 将text文本与自身进行笛卡儿积
     */
    public static void cross() throws Exception {
        text.cross(text).print();
    }

    /**
     * union方法，将两个数据集连接到一起形成新的数据集
     */
    public static void union() throws Exception {
        text.union(text).print();
    }

    /**
     * coGroup方法，协分组，将两个数据集分组后将相同分组进行连接执行一系列操作
     * 将单词数据集与自己协分组然后统计出现的次数
     */
    public static void coGroup() throws Exception {
        word.flatMap(new MyFlatMapFunction())
                .coGroup(word.flatMap(new MyFlatMapFunction()))
                .where(0)
                .equalTo(0)
                .with(new CoGroupFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, Tuple2<String, Integer>>() {
                    @Override
                    public void coGroup(Iterable<Tuple2<String, Integer>> iterable, Iterable<Tuple2<String, Integer>> iterable1, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        String word = null;
                        int count = 0;
                        for(Tuple2<String,Integer> t:iterable){
                            word = t.f0;
                            count += t.f1;
                        }
                        for(Tuple2<String,Integer> t:iterable1){
                            count += t.f1;
                        }
                        collector.collect(new Tuple2<>(word,count));
                    }
                }).print();
    }

    public static void main(String[] args) throws Exception {
        groupBy();
        groupReduce();
        sortGroup();
        aggregate();
        distinct();
        join();
        outerJoin();
        cross();
        union();
        coGroup();
    }
}
