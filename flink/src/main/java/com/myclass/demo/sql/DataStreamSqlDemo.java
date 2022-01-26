package com.myclass.demo.sql;

import com.myclass.common.entry.Student;
import org.apache.flink.types.Row;

public class DataStreamSqlDemo extends DataStreamSqlTableDemo {

    public static void sqlQuery(SqlQueryEntry entry) {
        switch (entry.tableName) {
            case "student_mysql":
                createStudentTableFromMysql();
                break;
            case "student_socket":
                createStudentTableFromSocket();
                break;
            case "word_mysql":
                createWordTableWithWatermarkFromMysql();
                break;
            case "word_socket":
                createWordTableWithWatermarkFromSocket();
                break;
            case "student_file":
                registerStudentTemporaryViewFromFile();
                break;
            default:
                throw new IllegalArgumentException("nonsupport table name:" + entry.tableName);
        }
        String querySql = String.format("SELECT * FROM `%s`", entry.tableName);
        sqlQuery(querySql, entry.classType).print();
    }

    public static void sqlQueryWithWatermark() {
        createWordTableWithWatermarkFromMysql();
        String querySql = "SELECT * FROM `word_mysql`";
        sqlQuery(querySql).print();
    }

    /**
     * 滚动窗口, 基于event time
     */
    public static void tumbleEventTimeWindow() {
        createWordTableWithWatermarkFromMysql();
        // TUMBLE_START为窗口开始时间、TUMBLE_END为窗口结束时间
        String querySql =
                "SELECT TUMBLE_START(`event_time`, INTERVAL '5' SECOND), TUMBLE_END(`event_time`, INTERVAL '5' SECOND), `word`, count(`word`) " +
                        "FROM `word_mysql` " +
                        // 按照窗口及单词进行分组，窗口大小为5s
                        "GROUP BY TUMBLE(`event_time`, INTERVAL '5' SECOND), `word`";
        sqlQuery(querySql).print();
    }

    /**
     * 滚动窗口, 基于processing time
     */
    public static void tumbleProcessingTimeWindow() {
        createWordTableWithWatermarkFromMysql();
        // 使用processing time进行开窗
        String querySql =
                "SELECT TUMBLE_START(`processing_time`, INTERVAL '1' SECOND), TUMBLE_END(`processing_time`, INTERVAL '1' SECOND), `word`, count(`word`) " +
                        "FROM `word_mysql` " +
                        "GROUP BY TUMBLE(`processing_time`, INTERVAL '1' SECOND), `word`";
        sqlQuery(querySql).print();
    }

    /**
     * 双流关联操作，将两条流按key关联起来后做一些操作，例如转化成新的结构或提取某些字段
     * 2022-01-20 00:00:00  1,未知,男,18,china,add1
     * 2022-01-20 00:00:00  2,未知,男,18,china,add2
     * 2022-01-20 00:00:00  3,未知,男,18,china,add3
     */
    public static void join() {
        createStudentTableFromMysql();
        createStudentTableFromSocket();
        String joinSql =
                "SELECT `student_mysql`.*, `student_socket`.`addition`" +
                        "FROM `student_mysql`" +
                        "INNER JOIN `student_socket`" +
                        "ON `student_mysql`.`id` = `student_socket`.`id`";
        sqlQuery(joinSql).print();
    }

    /**
     * 双流关联操作，将两条流按key关联起来后做一些操作，与join不同的是join是根据窗口进行划分的，而intervalJoin是根据上下界范围划分的.
     * interval join仅支持event time
     * mysql
     * 1	test	1641830400000
     * 2	test	1641830401000
     * 3	word	1641830402000
     * 4	student	1641830403000
     * 5	flink	1641830405000
     * 6	aaa	1641830408000
     * 7	word	1641830408000
     *
     * nc -lk 7777
     * 2022-01-11 00:00:00  1,interval_join1
     * 2022-01-11 00:05:00  2,interval_join2
     * 2022-01-11 00:10:00  3,interval_join3
     * 2022-01-11 00:15:00  4,interval_join4
     * 2022-01-11 01:00:00  5,interval_join5
     * 2022-01-11 01:00:08  6,interval_join8
     * 2022-01-11 01:00:09  6,interval_join9
     */
    public static void intervalJoin() {
        createWordTableWithWatermarkFromMysql();
        createWordTableWithWatermarkFromSocket();
        String joinSql =
                "SELECT `word_mysql`.`log_time`, `word_mysql`.`id`, `word_socket`.`word`" +
                        "FROM `word_mysql`" +
                        "INNER JOIN `word_socket`" +
                        "ON `word_mysql`.`id` = `word_socket`.`id`" +
                        "AND `word_mysql`.`event_time` BETWEEN `word_socket`.`event_time` - INTERVAL '1' HOUR AND `word_socket`.`event_time`";
        sqlQuery(joinSql).print();
    }

    public static void main(String[] args) throws Exception {
        sEnv.setParallelism(1);

        sqlQuery(new SqlQueryEntry("student_mysql", Student.class));
        sqlQuery(new SqlQueryEntry("student_socket", Row.class));
        sqlQuery(new SqlQueryEntry("word_mysql"));
        sqlQuery(new SqlQueryEntry("word_socket"));
        sqlQuery(new SqlQueryEntry("student_file"));
        sqlQueryWithWatermark();
        tumbleEventTimeWindow();
        tumbleProcessingTimeWindow();
        join();
        intervalJoin();

        sEnv.execute("sql demo");
    }


}
