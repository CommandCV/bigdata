package com.myclass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

public class TableFlinkApplication extends FlinkApplication {

    /**
     * 流式表执行环境
     */
    protected static StreamTableEnvironment tEnv = StreamTableEnvironment.create(sEnv, settings);;

    /**
     * table的增强实现
     */
    private static AdvanceTable advanceTable = new AdvanceTable();

    /**
     * 执行程序
     * 此方法使用的是streamEnv，适用情况为程序没有action操作又想要触发执行的情况
     */
    protected static void execute() throws Exception {
        sEnv.execute("flink job");
    }

    /**
     * 执行程序
     * @param jobName 任务名称
     */
    protected static void execute(String jobName) throws Exception {
        tEnv.execute(jobName);
    }

    /**
     * 执行sql（一般是DDL语句）
     * @param sql 执行的sql
     */
    protected static void executeSql(String sql) {
        tEnv.executeSql(sql);
    }

    /**
     * 执行sql查询
     * @param sql 查询的sql
     * @return 返回查询的表
     */
    protected static Table executeSqlQuery(String sql) {
        return tEnv.sqlQuery(sql);
    }

    /**
     * 通过内部增强实现的表查询sql
     * @param sql 查询sql
     * @return 增强实现类
     */
    protected static AdvanceTable sqlQuery(String sql) {
        advanceTable.setTable(tEnv.sqlQuery(sql));
        return advanceTable;
    }

    /**
     * 打印表中的数据
     * @param table 需要打印的表
     */
    protected static void print(Table table) {
        tEnv.toAppendStream(table, Row.class).print();
    }


    /**
     * 内部增强实现类
     * 提供对table的链式调用
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    protected static class AdvanceTable {
        private Table table;

        /**
         * 打印表中的数据
         */
        public void print() {
            tEnv.toAppendStream(table, Row.class).print();
        }

    }

}

