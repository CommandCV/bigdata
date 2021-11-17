package com.myclass.demo.stream;

import com.myclass.common.source.MysqlDataSource;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;

/**
 * Flink流处理常用数据源及数据沉槽
 * @author Yang
 */
public class DataStreamSourceDemo {

    /**
     * 初始化流处理执行环境
     */
    private static StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    /**
     * 从socket套接字中获取数据作为数据源
     */
    public static void socketTextStream() throws Exception {
        // 从本地socket套接字中读取数据
        DataStreamSource<String> dataStream = env.socketTextStream("localhost", 9999);
        // 打印输入的内容
        dataStream.print();
        // 执行任务
        env.execute();
    }

    /**
     * 从文本文件中读取数据作为数据源
     * 文本文件可以是本地文件，也可以是hdfs中的文件，只需要指定路径即可
     */
    public static void readTextFile() throws Exception {
        // 从本地文本文件中读取数据
        DataStreamSource<String> dataStream = env.readTextFile("flink/src/main/resources/common/word");
        // 从hdfs文件系统中读取数据
        //DataStreamSource<String> dataStream = env.readTextFile("hdfs://master:9000/word");

        // 将文本中每行单词切分成单个单词并收集
        dataStream.flatMap((s, collector) -> {
            String[] words = s.split("\t");
            for(String word: words){
                collector.collect(word);
            }
        }).print();
        // 执行任务
        env.execute();
    }

    /**
     * 从生成序列中读取数据作为数据源
     */
    public static void generateSequence() throws Exception {
        // 设置并行度，默认为CPU核心数，这里设置为1可以防止输出乱序
        env.setParallelism(1);
        // 生成1-10的序列并输出
        env.fromSequence(1, 10).print();
        // 执行任务
        env.execute();
    }

    /**
     * 从Java.util.Collection集合中读取数据作为数据源
     */
    public static void fromCollection() throws Exception {
        ArrayList<String> list = new ArrayList<>(5);
        list.add("flink");
        list.add("scala");
        list.add("spark");
        list.add("hadoop");
        list.add("hive");
        env.fromCollection(list).print();
        // 执行任务
        env.execute();
    }

    /**
     * 从Java.util.Collection集合中读取数据作为数据源
     */
    public static void fromElements() throws Exception {
        env.fromElements("flink", "scala", "spark", "hadoop", "hive").print();
        // 执行任务
        env.execute();
    }

    /**
     * 从自定义数据源中读取数据
     */
    public static void addSource() throws Exception {
        // 添加自定义数据源并打印读取的数据
        env.addSource(new MysqlDataSource()).print();
        // 执行任务
        env.execute();
    }

    public static void main(String[] args) throws Exception {
        socketTextStream();
        readTextFile();
        generateSequence();
        fromCollection();
        fromElements();
        addSource();
    }
}
