package com.myclass.demo.stream;

import com.myclass.FlinkApplication;
import com.myclass.common.entry.Student;
import com.myclass.common.sink.MysqlDataSink;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.Collector;

/**
 * Flink流处理常用数据沉槽
 *
 * @author Yang
 */
public class DataStreamSinkDemo extends FlinkApplication {

    /**
     * 将结果写入到文本文件中
     */
    public static void writeAsText() {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = sEnv.readTextFile("flink/src/main/resources/common/student");
        // 将结果写入到hdfs中
        //dataStream.writeAsText("hdfs://127.0.0.1:9000/student.txt");
        // 将结果写到本地文本文件中
        dataStream.writeAsText("flink/src/main/resources/stream/student.txt");
    }

    /**
     * 将结果写入到csv文件中
     * 注意：将结果写入到csv只支持元组类型的数据
     */
    public static void writeAsCsv() {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = sEnv.readTextFile("flink/src/main/resources/common/word");
        // 将单词转化成元组
        dataStream.flatMap((String s, Collector<Tuple1<String>> collector) -> {
            String[] words = s.split("\t");
            for (String word : words) {
                collector.collect(new Tuple1<>(word));
            }
        }).returns(Types.TUPLE(Types.STRING)).writeAsCsv("flink/src/main/resources/stream/words.csv");
    }

    /**
     * 将结果写入到socket套接字中
     */
    public static void writeToSocket() {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = sEnv.readTextFile("flink/src/main/resources/common/student").setParallelism(1);
        // 将结果写入到socket套接字中，以简单字符串类型发送
        dataStream.writeToSocket("127.0.0.1", 7777, new SimpleStringSchema());
    }

    /**
     * 将结果写入Mysql
     */
    public static void addSinkWithMysql() {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = sEnv.readTextFile("flink/src/main/resources/common/student");
        // 写入MySql中
        dataStream.flatMap((FlatMapFunction<String, Student>) (String s, Collector<Student> collector) -> {
            String[] data = s.split(",");
            Student student = new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
            collector.collect(student);
        }).returns(TypeInformation.of(Student.class))
                .addSink(new MysqlDataSink());
    }

    /**
     * 将结果写入Kafka
     */
    public static void addSinkWithKafka() {
        // 从本地文本文件中读取数据
        DataStream<String> dataStream = sEnv.readTextFile("flink/src/main/resources/common/student");
        dataStream.addSink(new FlinkKafkaProducer<>("127.0.0.1:9092", "test", new SimpleStringSchema()));
    }


    public static void main(String[] args) throws Exception {
        // 设置并行度
        sEnv.setParallelism(1);

//        writeAsText();
        writeAsCsv();
//        writeToSocket();
//        addSinkWithMysql();
//        addSinkWithKafka();

        // 执行任务
        sEnv.execute();
    }
}
