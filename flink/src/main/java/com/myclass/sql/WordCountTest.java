package com.myclass.sql;

import com.myclass.TableFlinkApplication;

import static org.apache.flink.table.api.Expressions.$;

public class WordCountTest extends TableFlinkApplication {

    public static void main(String[] args) {
        tEnv.createTemporaryView("test", sEnv.fromElements("java", "flink", "scala", "spark", "flink"), $("word"));
        tEnv.executeSql("SELECT `word`, COUNT(word) AS `count` FROM `test` GROUP BY `word`").print();
    }
}
