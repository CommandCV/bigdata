package com.myproject.sql;

import com.myproject.sql.config.Configuration;
import com.myproject.sql.enums.StorageTypeEnum;
import com.myproject.sql.server.DatabaseServer;

import java.util.List;

public class SqlTest {

    public static void main(String[] args) {
        Configuration configuration = new Configuration().storageType(StorageTypeEnum.IN_MEMORY);
        DatabaseServer databaseServer = new DatabaseServer(configuration);

        String createDatabase = "create database default";
        String createTable = "create table default.test(id int, name varchar(255))";
        String alterTable = "alter table default.test add column age int";
        String insert = "insert into default.test values (1, 'aaa', 18), (2, 'bbb', 21)";
        String query1 = "select * from default.test";
        String query2 = "select * from default.test where id = 1";
        String query3 = "select * from default.test where id > 0 order by id desc";
        databaseServer.execute(createDatabase);
        databaseServer.execute(createTable);
        databaseServer.execute(alterTable);
        databaseServer.execute(insert);
        List<List<Object>> result1 = databaseServer.executeQuery(query1);
        System.out.println(result1);
        List<List<Object>> result2 = databaseServer.executeQuery(query2);
        System.out.println(result2);
        List<List<Object>> result3 = databaseServer.executeQuery(query3);
        System.out.println(result3);

        String dropTable = "drop table default.test";
        databaseServer.execute(dropTable);
        String dropDatabase = "drop database default";
        databaseServer.execute(dropDatabase);
    }
}
