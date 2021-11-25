package com.myclass.demo.stream;

import com.myclass.FlinkApplication;
import com.myclass.common.source.MysqlDataSource;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.ArrayList;
import java.util.Properties;

/**
 * Flink流处理常用数据源及数据沉槽
 * @author Yang
 */
public class DataStreamSourceDemo extends FlinkApplication {

    /**
     * 从socket套接字中获取数据作为数据源
     * nc -lk 7777
     */
    public static void socketTextStream() {
        // 从本地socket套接字中读取数据
        DataStreamSource<String> dataStream = sEnv.socketTextStream("127.0.0.1", 7777);
        // 打印输入的内容
        dataStream.print();
    }

    /**
     * 从文本文件中读取数据作为数据源
     * 文本文件可以是本地文件，也可以是hdfs中的文件，只需要指定路径即可
     */
    public static void readTextFile() {
        // 从本地文本文件中读取数据
        DataStreamSource<String> dataStream = sEnv.readTextFile("flink/src/main/resources/common/word");
        // 从hdfs文件系统中读取数据
//        DataStreamSource<String> dataStream = sEnv.readTextFile("hdfs://127.0.0.1:9000/word.txt");
        // 将文本中每行单词切分成单个单词并收集
        dataStream.flatMap(new RichFlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) {
                String[] words = s.split("\t");
                for(String word: words){
                    collector.collect(word);
                }
            }
        }).print();
    }

    /**
     * 从生成序列中读取数据作为数据源
     */
    public static void fromSequence() {
        // 设置并行度，默认为CPU核心数，这里设置为1可以防止输出乱序
        sEnv.setParallelism(1);
        // 生成1-10的序列并输出
        sEnv.fromSequence(1, 10).print();
    }

    /**
     * 从Collection集合中读取数据作为数据源
     */
    public static void fromCollection() {
        ArrayList<String> list = new ArrayList<>(5);
        list.add("flink");
        list.add("flink");
        list.add("scala");
        list.add("spark");
        sEnv.fromCollection(list).print();
    }

    /**
     * 从Java.util.Collection集合中读取数据作为数据源
     */
    public static void fromElements() {
        sEnv.fromElements("flink", "scala", "spark", "hadoop", "hive").print();
    }

    /**
     * 从自定义数据源中读取数据
     * insert into student(name, gender, age, address) VALUE('demo2', 'man', 12, 'beijing')
     */
    public static void addSourceWithMysql() {
        // 添加自定义数据源并打印读取的数据
        sEnv.addSource(new MysqlDataSource()).print();
    }

    /**
     * 从kafka中读取数据
     * kafka-console-producer --broker-list 127.0.0.1:9092 --topic test
     */
    public static void addSourceWithKafka() {
        // 添加自定义数据源并打印读取的数据
        Properties prop = new Properties();
        prop.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        prop.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
        sEnv.addSource(new FlinkKafkaConsumer<>("test", new SimpleStringSchema(), prop)).print();
    }

    public static void main(String[] args) throws Exception {
        socketTextStream();
        readTextFile();
        fromSequence();
        fromCollection();
        fromElements();
        addSourceWithMysql();
        addSourceWithKafka();

        // 执行任务
        sEnv.execute();
    }
}
