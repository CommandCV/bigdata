package cn.myclass.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * DataSet变换-分区
 * @author Yang
 */
public class DataSetTransformationsPartitions {
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
     * rebalance方法，将数据集均匀的重新分配到每一个分区
     */
    public static void rebalance() throws Exception {
        text.rebalance().setParallelism(2).print();
    }

    /**
     * partitionByHash方法，将数据集按照某个字段的hash值分区
     */
    public static void hashPartition() throws Exception {
        word.flatMap(new MyFlatMapFunction()).partitionByHash(0).print();
    }

    /**
     * partitionByRange方法，将数据集按照一定范围分区
     */
    public static void rangePartition() throws Exception {
        word.flatMap(new MyFlatMapFunction()).partitionByRange(0).print();
    }

    /**
     * sortPartition方法，将数据集按照某个字段分区并排序
     */
    public static void sortPartition() throws Exception {
        word.flatMap(new MyFlatMapFunction())
                .sortPartition(0, Order.ASCENDING)
                .sortPartition(1, Order.DESCENDING).print();
    }

    public static void main(String[] args) throws Exception {
        rebalance();
        hashPartition();
        rangePartition();
        sortPartition();
    }

}
