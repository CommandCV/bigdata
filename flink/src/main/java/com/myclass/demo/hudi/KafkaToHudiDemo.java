package com.myclass.demo.hudi;

import com.myclass.TableFlinkApplication;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.CheckpointConfig;

import java.util.concurrent.TimeUnit;

public class KafkaToHudiDemo extends TableFlinkApplication {

    private static void init() {
        CheckpointConfig checkpointConfig = sEnv.getCheckpointConfig();
        checkpointConfig.setCheckpointInterval(TimeUnit.SECONDS.toMillis(30));
        checkpointConfig.setMinPauseBetweenCheckpoints(TimeUnit.SECONDS.toMillis(30));
        checkpointConfig.setCheckpointTimeout(TimeUnit.SECONDS.toMillis(60));
        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        checkpointConfig.setTolerableCheckpointFailureNumber(5);
        checkpointConfig.setMaxConcurrentCheckpoints(1);
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
//        checkpointConfig.setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
    }


    public static void main(String[] args) throws Exception {
        init();
        final String createSourceTableSql = "" +
                " CREATE TABLE student_source(" +
                "   `record_time` BIGINT," +
                "   `id` BIGINT," +
                "   `name` STRING," +
                "   `address` STRING," +
                "   `proctime` as PROCTIME()," +
                "   `event_time` AS TO_TIMESTAMP(FROM_UNIXTIME(`record_time`/1000))," +
                "   WATERMARK FOR event_time AS `event_time` - INTERVAL '30' SECOND" +
                " )" +
                " WITH (" +
                " 'connector' = 'kafka'," +
                " 'topic' = 'student'," +
                " 'properties.bootstrap.servers' = 'kafka:9092'," +
                " 'properties.group.id' = 'test'," +
                " 'scan.startup.mode' = 'earliest-offset'," +
                " 'key.format' = 'csv'," +
                " 'key.csv.field-delimiter' = ','," +
                " 'key.csv.ignore-parse-errors' = 'true'," +
                " 'key.csv.allow-comments' = 'true'," +
                " 'value.format' = 'csv'," +
                " 'value.csv.field-delimiter' = ','," +
                " 'value.csv.ignore-parse-errors' = 'true'," +
                " 'value.csv.allow-comments' = 'true'," +
                " 'key.fields' = 'record_time'" +
                " )";
        final String createSinkTableSql = "" +
                " CREATE TABLE student_sink(" +
                "   `uuid` STRING," +
                "   `record_time` BIGINT," +
                "   `id` BIGINT," +
                "   `name` STRING," +
                "   `address` STRING," +
                "   `dt` INT" +
                " )" +
                " PARTITIONED BY (`dt`)" +
                " WITH (" +
                " 'connector' = 'hudi'," +
                " 'path' = 'hdfs://hadoop:8020/usr/hive/warehouse/hudi_test/student_mor'," +
                " 'table.type' = 'MERGE_ON_READ'," +
                " 'hoodie.datasource.write.recordkey.field'= 'uuid'," +
                " 'write.preDefaultExecutorServiceLoader.javacombine.field'= 'dt'," +
                " 'write.tasks' = '1'," +
                " 'write.rate.limit'= '1000'," +
                " 'compaction.tasks' = '1'," +
                " 'compaction.async.enabled' = 'true'," +
                " 'compaction.trigger.strategy' = 'num_commits'," +
                " 'compaction.delta_commits' = '1'," +
                " 'changelog.enabled'= 'true'," +
                " 'read.streaming.enabled'= 'true'," +
                " 'read.streaming.check-interval'= '30'," +
                " 'hive_sync.enable'= 'true'," +
                " 'hive_sync.mode'= 'hms'," +
                " 'hive_sync.metastore.uris'= 'thrift://bigdata:9083'," +
                " 'hive_sync.jdbc_url'= 'jdbc:hive2://bigdata:10000'," +
                " 'hive_sync.table'= 'student_mor'," +
                " 'hive_sync.db'= 'hudi_test'," +
                " 'hive_sync.username'= ''," +
                " 'hive_sync.password'= ''," +
                " 'hive_sync.support_timestamp'= 'true'" +
                " )";
        final String insertSql = "" +
                " INSERT INTO student_sink " +
                " SELECT " +
                "   uuid() AS `uuid`," +
                "   `record_time`," +
                "   `id`," +
                "   `name`," +
                "   `address`," +
                "   CAST(FROM_UNIXTIME(`record_time`, 'yyyyMMdd') AS INT) AS `dt`" +
                " FROM student_source";

        executeSql(createSourceTableSql);
        executeSql(createSinkTableSql);
        executeSql(insertSql);
        execute();
    }

}
