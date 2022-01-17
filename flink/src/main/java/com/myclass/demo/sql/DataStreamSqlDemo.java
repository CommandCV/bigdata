package com.myclass.demo.sql;

import com.myclass.TableFlinkApplication;
import com.myclass.common.entry.Student;

public class DataStreamSqlDemo extends TableFlinkApplication {

    public static void createTable() {
        String createTableSql =
                "CREATE TABLE student (" +
                        "  `id` BIGINT," +
                        "  `name` STRING," +
                        "  `gender` STRING," +
                        "  `age` INT," +
                        "  `address` STRING," +
                        "  PRIMARY KEY (`id`) NOT ENFORCED" +
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

    public static void sqlQuery() {
        createTable();
        String querySql = "SELECT * FROM `student`";
        sqlQuery(querySql, Student.class).print();
    }

    /**
     * 创建声明event_time和watermark的创表语句
     */
    public static void createTableWithWatermark() {
        String createTableSql =
                "CREATE TABLE word (" +
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

    public static void sqlQueryWithWatermark() {
        createTableWithWatermark();
        String querySql = "SELECT * FROM `word`";
        sqlQuery(querySql).print();
    }

    /**
     * 滚动窗口, 基于event time
     */
    public static void tumbleEventTimeWindow() {
        createTableWithWatermark();
        // TUMBLE_START为窗口开始时间、TUMBLE_END为窗口结束时间
        String querySql =
                "SELECT TUMBLE_START(`event_time`, INTERVAL '5' SECOND), TUMBLE_END(`event_time`, INTERVAL '5' SECOND), `word`, count(`word`) " +
                        "FROM `word` " +
                        // 按照窗口及单词进行分组，窗口大小为5s
                        "GROUP BY TUMBLE(`event_time`, INTERVAL '5' SECOND), `word`";
        sqlQuery(querySql).print();
    }

    /**
     * 滚动窗口, 基于processing time
     */
    public static void tumbleProcessingTimeWindow() {
        createTableWithWatermark();
        // 使用processing time进行开窗
        String querySql =
                "SELECT TUMBLE_START(`processing_time`, INTERVAL '1' SECOND), TUMBLE_END(`processing_time`, INTERVAL '1' SECOND), `word`, count(`word`) " +
                        "FROM `word` " +
                        "GROUP BY TUMBLE(`processing_time`, INTERVAL '1' SECOND), `word`";
        sqlQuery(querySql).print();
    }


    public static void main(String[] args) throws Exception {
        sEnv.setParallelism(1);

        sqlQuery();
        sqlQueryWithWatermark();
        tumbleEventTimeWindow();
        tumbleProcessingTimeWindow();

        sEnv.execute("sql demo");
    }


}
