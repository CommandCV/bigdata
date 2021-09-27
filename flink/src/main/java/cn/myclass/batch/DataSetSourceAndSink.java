package cn.myclass.batch;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.io.TextOutputFormat;
import org.apache.flink.api.java.io.jdbc.JDBCInputFormat;
import org.apache.flink.api.java.io.jdbc.JDBCOutputFormat;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;
import org.apache.flink.types.StringValue;
import org.apache.flink.util.Collector;

import java.util.ArrayList;

/**
 * DataSet 常用数据源以及数据沉槽
 * @author Yang
 */
public class DataSetSourceAndSink {

    /**
     * 获得执行环境
     */
    private static ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    /**
     * 从文本文件中读取数据按照制表符切分并输出。
     */
    public static void readTextFile() throws Exception {
        DataSource<String> words = env.readTextFile("flink/src/main/resources/common/word");
        // 将单词按照制表符切分并收集
        words.flatMap(new FlatMapFunction<String, String>() {

            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String []arr = s.split("\t");
                for(String word:arr){
                    collector.collect(word);
                }
            }
        }).print();
    }

    /**
     * 从文本文件中读取数据并以StringValue类型返回，StringValue类型为可变字符串。
     * 此方法和readTextFile方法类似，只不过是制定了数据类型。
     */
    public static void readTextFileWithValue() throws Exception {
        DataSource<StringValue> words = env.readTextFileWithValue("flink/src/main/resources/common/word");
        words.flatMap(new FlatMapFunction<StringValue, String>() {

            @Override
            public void flatMap(StringValue s, Collector<String> collector) throws Exception {
                String []arr = s.getValue().split("\t");
                for(String word:arr){
                    collector.collect(word);
                }
            }
        }).print();
    }

    /**
     * 从Csv文件中读取数据。
     */
    public static void readCsvFile() throws Exception {
        env.readCsvFile("flink/src/main/resources/common/wordcount.csv")
                // 指定每个字段的类型
                .types(String.class, Integer.class)
                .print();
    }

    /**
     * 将多个元素作为数据，经过此方法后，元素转化成了DataSet类型的数据。
     */
    public static void fromElement() throws Exception {
        env.fromElements("flink", "scala", "spark", "hadoop", "hive")
                .print();
    }

    /**
     * 将生成序列作为数据。
     * 和fromElement方法类似，只是将序列数据转化成了DataSet类型的数据。
     */
    public static void generateSequence() throws Exception {
        env.setParallelism(1);
        env.generateSequence(1,10).print();
    }

    /**
     * 从Java.util.Collection类型的集合中获取数据。
     */
    public static void fromCollection() throws Exception {
        ArrayList<String> list = new ArrayList<>(5);
        list.add("flink");
        list.add("scala");
        list.add("spark");
        list.add("hadoop");
        list.add("hive");
        env.fromCollection(list).print();
    }

    /**
     * 从mysql中读取数据。
     */
    public static void JDBCInputFormat() throws Exception {
        // 创建自定义的输入类型
        env.createInput(
                // 使用JDBC输入格式
                JDBCInputFormat.buildJDBCInputFormat()
                        // 设置驱动、url、用户名、密码以及查询语句
                        .setDrivername("com.mysql.jdbc.Driver")
                        .setDBUrl("jdbc:mysql://localhost:3306/flink")
                        .setUsername("root")
                        .setPassword("root")
                        .setQuery("select word,count from word")
                        // 说明每条记录每个字段的数据类型
                        .setRowTypeInfo(new RowTypeInfo(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO))
                        .finish()).print();
    }

    /**
     * 将结果写入文本文件中。
     * 注意：由于flink的运行机制（懒加载），如果没有触发具体的action（触发shuffle的方法）的方法
     * 只是transformation变换的话flink是不会执行的，所以需要加上execute()方法执行。
     */
    public static void writeAsText() throws Exception {
        env.fromElements("flink", "scala", "spark", "hadoop", "hive")
                .writeAsText("flink/src/main/resources/common/text_result.txt");
        // 注意：由于懒加载机制所以需要执行execute方法
        env.execute();
    }

    /**
     * 将结果以自定义格式写入文本文件中，这里将单词和次数1之间加了符号','
     */
    public static void writeAsFormattedText() throws Exception {
        env.fromElements("flink", "scala", "spark", "hadoop", "hive")
                .writeAsFormattedText("flink/src/main/resources/common/text_formatted_result.txt", new TextOutputFormat.TextFormatter<String>() {
                    @Override
                    public String format(String s) {
                        return s + "," + 1;
                    }
                });
        env.execute();
    }

    /**
     * 将结果写入到csv文件中
     */
    public static void writeAsCsv() throws Exception {
        env.fromElements("flink", "scala", "spark", "hadoop", "hive")
                .map(new MapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map(String s) throws Exception {
                        return new Tuple2<>(s,1);
                    }
                })
                .writeAsCsv("flink/src/main/resources/common/csv_result.csv");
        env.execute();
    }

    /**
     * 将结果写入到mysql中。
     * 注意：写入mysql的数据格式必须为org.apache.flink.types.Row类型
     */
    public static void JDBCOutputFormat() throws Exception {
        // 将元素转化成DataSet
        DataSet<Row> dataSource = env.fromElements("flink", "scala", "spark", "hadoop", "hive")
                .map(new MapFunction<String, Row>() {
                    @Override
                    public Row map(String s) throws Exception {
                        // 创建存储两个字段的row对象
                        Row row = new Row(2);
                        // 单词
                        row.setField(0, s);
                        // 次数
                        row.setField(1, 1);
                        return row;
                    }
                });
        // 通过JDBCOutputFormat将结果写入到数据库
        dataSource.output(JDBCOutputFormat.buildJDBCOutputFormat()
                .setDrivername("com.mysql.jdbc.Driver")
                .setDBUrl("jdbc:mysql://localhost:3306/flink")
                .setUsername("root")
                .setPassword("root")
                .setQuery("insert into word(word,count) values(?,?)")
                .finish());
        env.execute();
    }

    public static void main(String[] args) throws Exception {
        readTextFile();
        readTextFileWithValue();
        readCsvFile();
        fromElement();
        generateSequence();
        fromCollection();
        JDBCInputFormat();
        writeAsText();
        writeAsFormattedText();
        writeAsCsv();
        JDBCOutputFormat();
    }
}
