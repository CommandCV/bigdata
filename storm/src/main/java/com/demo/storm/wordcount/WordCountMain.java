package com.demo.storm.wordcount;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

/**
 * @author Yang
 */
public class WordCountMain {
    public static void main(String[] args) {
        // 设置任务信息
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        // 数据源头，从文件中读取数据。                          设置executors为3即进程数为3，分配的任务数为3
        topologyBuilder.setSpout("word-spout",new WordSpout(),3).setNumTasks(3);
        // 接收从数据源头传来的数据。切分单词，将单词的发送给下一个bolt。 设置executors为4，任务数为4
        topologyBuilder.setBolt("create-bolt",new CreateWordBolt(),4).shuffleGrouping("word-spout").setNumTasks(4);
        // 接收从上游来的Bolt的数据。将单词进行计数并放入map集合，最后输出。   设置executors为5，任务数为5
        topologyBuilder.setBolt("word-count",new WordCountBolt(),5).shuffleGrouping("create-bolt").setNumTasks(5);

        // 创建配置文件
        Config config =new Config();
        config.setDebug(true);
        // 设置工作进程数，即运行的节点数
//        config.setNumWorkers(2);

        // 本地集群模式，在本地运行
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("WordCount",config,topologyBuilder.createTopology());

        // 集群模式
//        StormSubmitter.submitTopology("WordCount",config,topologyBuilder.createTopology());

    }
}
