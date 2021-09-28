package com.myclass.demo.table;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.util.Collector;

/**
 * FLink table sql&api
 * @author Yang
 */
public class TableSqlApi {


    /**
     * 环境配置
     */
    private static EnvironmentSettings settings = EnvironmentSettings.newInstance().build();

    /**
     * 获得流执行环境
     */
    private static ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    /**
     * 获得流式表的执行环境
     * @return org.apache.flink.table.api.TableEnvironment
     */
    private static TableEnvironment getStreamTableEnvironment(){
        return TableEnvironment.create(settings);
    }

    /**
     * 获得批式表的执行环境
     * @return org.apache.flink.table.api.java.BatchTableEnvironment
     */
    private static BatchTableEnvironment getBatchTableEnvironment(){
        return BatchTableEnvironment.create(env);
    }

    /**
     * 从文件中读取数据转化后注册为wordcount表
     * @param tableEnv 表执行环境
     */
    private static void registerWordCount(BatchTableEnvironment tableEnv){
        // 加载数据
        DataSet<String> text = env.readTextFile("flink/src/main/resources/common/word");
        // 将数据进行切分
        DataSet<String> word = text.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String s, Collector<String> collector) throws Exception {
                String[] arr = s.split("\t");
                // 将单词切分
                for(String word1 : arr){
                    collector.collect(word1);
                }
            }
        });
        // 将数据转化成元组
        DataSet<WordCount> tuple = word.map((MapFunction<String, WordCount>) s ->
                new WordCount(s, 1));
        // 按照单词字段分组后聚合累加次数
        DataSet<WordCount> count = tuple.groupBy("word").reduce((ReduceFunction<WordCount>) (wordCount, t1) ->
                new WordCount(wordCount.getWord(), wordCount.getCount() + t1.getCount()));
        // 注册表
        tableEnv.registerDataSet("wordcount",count);
    }

    /**
     * word count table api
     */
    private static void wordCountTableApi() throws Exception {
        // 获得表环境
        BatchTableEnvironment tableEnv = getBatchTableEnvironment();
        // 注册表
        registerWordCount(tableEnv);
        // 扫描表获得表对象并按照单词排序
        Table table = tableEnv.scan("wordcount").orderBy("word");
        // 转化成对应类型的数据集
        DataSet<WordCount> data = tableEnv.toDataSet(table,WordCount.class);
        // 输出
        data.print();

    }

    /**
     * word count table sql
     */
    private static void wordCountTableSql() throws Exception {
        // 获得表环境
        BatchTableEnvironment tableEnv = getBatchTableEnvironment();
        // 注册表
        registerWordCount(tableEnv);
        // 执行自定义sql，扫描表获得表对象并按照单词排序
        Table sqlTable = tableEnv.sqlQuery("select * from wordcount order by word");
        // 转化成对应类型的数据集
        DataSet<WordCount> sqlData = tableEnv.toDataSet(sqlTable, WordCount.class);
        // 输出
        sqlData.print();
    }

    public static void main(String[] args) throws Exception {
        //wordCountTableApi();
        wordCountTableSql();
    }
}
