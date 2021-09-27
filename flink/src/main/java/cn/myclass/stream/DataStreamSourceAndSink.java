package cn.myclass.stream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

/**
 * Flink流处理常用数据源及数据沉槽
 * @author Yang
 */
public class DataStreamSourceAndSink {

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
        dataStream.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] words = s.split("\t");
                for(String word: words){
                    collector.collect(word);
                }
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
        env.generateSequence(1, 10).print();
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
        dataStream.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                String[] words = s.split("\t");
                for(String word: words){
                    collector.collect(new Tuple2<>(word, 1));
                }
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
        dataStream.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        String[] words = s.split("\t");
                        for(String word: words){
                            collector.collect(new Tuple2<>(word, 1));
                        }
                    }
                    // 写入MySql中
                }).addSink(new MysqlDataSink());
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
        writeAsText();
        writeAsCsv();
        writeToSocket();
        addSink();
    }
}
