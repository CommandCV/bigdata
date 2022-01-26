package com.myclass.demo.sql;

import com.myclass.TableFlinkApplication;
import com.myclass.common.entry.Student;
import com.myclass.common.operator.map.MyStudentMapFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.types.Row;

public class DataStreamSqlTableDemo extends TableFlinkApplication {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class SqlQueryEntry {
        public String tableName;
        public Class<?> classType = Row.class;

        public SqlQueryEntry(String tableName) {
            this.tableName = tableName;
        }
    }

    /**
     * 基于mysql创建student表
     */
    public static void createStudentTableFromMysql() {
        String createTableSql =
                "CREATE TABLE student_mysql (" +
                        "  `id` BIGINT," +
                        "  `name` STRING," +
                        "  `gender` STRING," +
                        "  `age` INT," +
                        "  `address` STRING" +
                        ") WITH (" +
                        "  'connector' = 'jdbc'," +
                        "  'url' = 'jdbc:mysql://127.0.0.1:3306/test?useSSL=false&useUnicode=true&characterEncoding=utf8'," +
                        "  'table-name' = 'student'," +
                        "  'driver' = 'com.mysql.jdbc.Driver'," +
                        "  'username' = 'root'," +
                        "  'password' = 'root'" +
                        ")";
        tEnv.executeSql(createTableSql);
    }

    /**
     * 基于socket创建student表
     */
    public static void createStudentTableFromSocket() {
        String createTableSql =
                "CREATE TABLE student_socket (" +
                        "  `timestamp` BIGINT, " +
                        "  `id` BIGINT," +
                        "  `name` STRING," +
                        "  `gender` STRING," +
                        "  `age` INT," +
                        "  `address` STRING," +
                        "  `addition` STRING" +
                        ") WITH (" +
                        "  'connector' = 'my-socket'," +
                        "  'host' = 'localhost'," +
                        "  'port' = '7777'," +
                        "  'format' = 'csv'," +
                        "  'delimiter' = '\\t'" +
                        ")";
        tEnv.executeSql(createTableSql);
    }

    /**
     * 创建声明event_time和watermark的创表语句
     */
    public static void createWordTableWithWatermarkFromMysql() {
        String createTableSql =
                "CREATE TABLE word_mysql (" +
                        "  `id` BIGINT," +
                        "  `word` STRING," +
                        "  `log_time` BIGINT," +
                        // 声明id为主键并且不强制传id字段
                        "  PRIMARY KEY (`id`) NOT ENFORCED," +
                        // PROCTIME()为process time
                        "  processing_time AS PROCTIME()," +
                        // 声明log_time字段为event time
                        "  `event_time` AS TO_TIMESTAMP(FROM_UNIXTIME(`log_time` / 1000))," +
                        // 使用event_time字段中提取水印，并允许有n秒延迟（0即为无延迟）
                        "  WATERMARK FOR `event_time` AS `event_time` - INTERVAL '0' SECOND" +
                        ") WITH (" +
                        "  'connector' = 'jdbc'," +
                        "  'url' = 'jdbc:mysql://127.0.0.1:3306/test?useSSL=false&useUnicode=true&characterEncoding=utf8'," +
                        "  'table-name' = 'word'," +
                        "  'driver' = 'com.mysql.jdbc.Driver'," +
                        "  'username' = 'root'," +
                        "  'password' = 'root'" +
                        ")";
        executeSql(createTableSql);
    }

    /**
     * 创建声明event_time和watermark的创表语句
     */
    public static void createWordTableWithWatermarkFromSocket() {
        String createTableSql =
                "CREATE TABLE word_socket (" +
                        "  `timestamp` BIGINT, " +
                        "  `id` BIGINT," +
                        "  `word` STRING," +
                        "  `processing_time` AS PROCTIME()," +
                        "  `event_time` AS TO_TIMESTAMP(FROM_UNIXTIME(`timestamp` / 1000))," +
                        "  WATERMARK FOR `event_time` AS `event_time` - INTERVAL '0' SECOND" +
                        ") WITH (" +
                        "  'connector' = 'my-socket'," +
                        "  'host' = 'localhost'," +
                        "  'port' = '7777'," +
                        "  'format' = 'csv'," +
                        "  'delimiter' = '\\t'" +
                        ")";
        executeSql(createTableSql);
    }

    /**
     * 将DataStream数据流注册成临时视图表
     */
    public static void registerStudentTemporaryViewFromFile() {
        SingleOutputStreamOperator<Student> stream1 = sEnv.readTextFile("flink/src/main/resources/common/student")
                .map(new MyStudentMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Student>forMonotonousTimestamps()
                                .withTimestampAssigner((student, timestamp) -> System.currentTimeMillis())
                );
        tEnv.createTemporaryView("student_file", tEnv.fromDataStream(stream1));
    }

}
