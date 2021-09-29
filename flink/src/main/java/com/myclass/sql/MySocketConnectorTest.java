package com.myclass.sql;

import com.myclass.TableFlinkApplication;

public class MySocketConnectorTest extends TableFlinkApplication {

    /**
     * nc -lk 7777
     * csv:
     * 2021-09-29 16:00:00	100001|boy|18|male|朝阳区
     * 2021-09-29 16:00:00	100002|girl|18|female|海淀区
     *
     * json:
     * 2021-09-29 16:00:00	{"id": 1, "name": "boy", "gender": "male","age": 18,"address": "a1"}
     * 2021-09-29 16:00:00	{"id": 2, "name": "girl", "gender": "female","age": 18,"address": "b1"}
     *
     */
    public static void main(String[] args) throws Exception {
        final String createTableSql =
                "CREATE TABLE `test` (" +
                    "`timestamp` BIGINT, " +
                    "`id` BIGINT, " +
                    "`name` STRING," +
                    "`gender` STRING," +
                    "`age` INT," +
                    "`address` STRING" +
                    ")" +
                "WITH (" +
                    "'connector' = 'my-socket'," +
                    "'host' = '127.0.0.1'," +
                    "'port' = '7777'," +
                    "'format' = 'csv'," +
                    "'delimiter' = '\t'" +
                ")";
        final String selectSql = "SELECT * FROM `test`";
        executeSql(createTableSql);
        sqlQuery(selectSql).print();
        execute();
    }

}
