package com.demo.stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

/**
 * Java实现WordCount
 * @author Yang
 */
public class DataStreamWordCountJava {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Tuple2<String, Integer>> dataStream = env
                // 读取文本文件中的单词
                .readTextFile("flink/src/main/resources/stream/word")
                // 切分单词并形成元组
                .flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
                    @Override
                    public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        // 切分数据
                        String[] arr = s.split("\t");
                        // 形成元组
                        for(String string:arr){
                            collector.collect(new Tuple2<>(string, 1));
                        }
                    }
                })
                // 把元组中索引为0的字段当键
                .keyBy(0)
                // 按照元组中的索引为1的字段进行累加
                .sum(1);
        // 输出
        dataStream.print();
        // 执行任务
        env.execute("Window WordCount");
    }

}
