package com.myclass.demo.stream;

import com.myclass.common.sink.MysqlDataSink;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Flink流处理常用数据沉槽
 * @author Yang
 */
public class DataStreamSinkDemo {

    /**
     * 初始化流处理执行环境
     */
    private static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    /**
     * 将结果写入到文本文件中
     */
    public static void writeAsText() throws Exception {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = env.readTextFile("flink/src/main/resources/common/word");
        // 设置并行度为1，将结果写入到一个文件中
        env.setParallelism(1);
        // 将结果写入到hdfs中
        //dataStream.writeAsText("hdfs://master:9000/words.txt");
        // 将结果写到本地文本文件中
        dataStream.writeAsText("flink/src/main/resources/stream/words.txt");
        // 执行任务
        env.execute();
    }

    /**
     * 将结果写入到csv文件中
     * 注意：将结果写入到csv只支持元组类型的数据
     */
    public static void writeAsCsv() throws Exception {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = env.readTextFile("flink/src/main/resources/common/word");
        // 设置并行度
        env.setParallelism(1);
        // 将单词转化成元组
        dataStream.flatMap((s, collector) -> {
            String[] words = s.split("\t");
            for(String word: words){
                collector.collect(new Tuple2<>(word, 1));
            }
        }).writeAsCsv("flink/src/main/resources/stream/words.csv");
        // 执行任务
        env.execute();
    }

    /**
     * 将结果写入到socket套接字中
     */
    public static void writeToSocket() throws Exception {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = env.readTextFile("flink/src/main/resources/common/word");
        // 将结果写入到socket套接字中，以简单字符串类型发送
        dataStream.writeToSocket("localhost", 9999, new SimpleStringSchema());
        // 执行任务
        env.execute();
    }

    /**
     * 将结果写入自定义数据沉槽
     */
    public static void addSink() throws Exception {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = env.readTextFile("flink/src/main/resources/common/word");
        // 将单词形成元组并初始化次数为1
        // 写入MySql中
        dataStream.flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (s, collector) -> {
            String[] words = s.split("\t");
            for(String word: words){
                collector.collect(new Tuple2<>(word, 1));
            }
        }).addSink(new MysqlDataSink());
        // 执行任务
        env.execute();
    }


    public static void main(String[] args) throws Exception {
        writeAsText();
        writeAsCsv();
        writeToSocket();
        addSink();
    }
}
