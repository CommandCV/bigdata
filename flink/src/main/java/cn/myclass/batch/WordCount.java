package cn.myclass.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

/**
 * 批处理WordCount
 * @author Yang
 */
public class WordCount {
    public static void main(String[] args) throws Exception {
        // 获得执行环境
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        // 从文件中读取数据
        DataSet<String> text = env.readTextFile("flink/src/main/resources/common/word");
        // 将单词切分并初始化次数为1
        DataSet<Tuple2<String, Integer>> wordCounts = text
                .flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
                    @Override
                    public void flatMap(String s, Collector<Tuple2<String,Integer>> collector) throws Exception {
                        for(String str: s.split("\t")){
                            collector.collect(new Tuple2<String,Integer>(str, 1));
                        }
                    }
                })
                // 按照单词分组
                .groupBy(0)
                // 求相同单词的次数和
                .sum(1);
        // 输出结果
        wordCounts.print();
    }
}
