package com.myclass.demo.storm.storm_kafka;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.topology.TopologyBuilder;

/**
 * storm集成Kafka，将Kafka的主题当作水龙头
 * 即数据源，之后通过storm的bolt处理业务逻辑
 * @author Yang
 */
public class StormKafkaMain {
    public static void main(String[] args) throws Exception {
        // 创建拓扑
        TopologyBuilder builder = new TopologyBuilder();

        KafkaSpoutConfig<String, String> kafkaSpoutConfig = KafkaSpoutConfig.builder("node1:2181", "test")
                .build();
        // 通过配置创建一个kafka数据源
        KafkaSpout<String, String> kafkaSpout = new KafkaSpout<>(kafkaSpoutConfig);

        // 设置数据源,开启两个数据源，每个数据源一个任务
        builder.setSpout("kafka-spout", kafkaSpout).setNumTasks(2) ;
        // 设置转接头，开启两个bolt，每个bolt处理一个任务，并按照kafka-spout随机分组
        builder.setBolt("Kafka-Bolt", new KafkaBolt(), 2).shuffleGrouping("kafka-spout").setNumTasks(2) ;

        // 设置集群配置文件，开启两个任务即两个节点运行此程序
        Config config = new Config();
        config.setNumWorkers(2);
        config.setDebug(true);

        // 本地集群模式，提交作业
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("kafka-storm", config, builder.createTopology());

    }
}
