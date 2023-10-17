package com.myclass.demo.storm.storm_hbase;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

/**
 * storm集成HBase，读取文件中的单词进行统计
 * 之后storm的bolt写入HBase
 * @author Yang
 */
public class StormHBaseMain {
    public static void main(String[] args) throws Exception {
        // 创建拓扑
        TopologyBuilder builder = new TopologyBuilder();

        // 设置数据源,开启两个数据源，每个数据源一个任务
        builder.setSpout("CreateWordSpout",new CreateWordSpout()).setNumTasks(2) ;
        // 设置数据切分转接头，开启两个bolt，每个bolt处理一个任务,随机分组
        builder.setBolt("SplitBolt",new SplitBolt(),2).shuffleGrouping("CreateWordSpout").setNumTasks(2);
        // 设置HBase转接头，开启两个bolt，每个bolt处理一个任务，随机分组
        builder.setBolt("HBaseBolt", new HBaseBolt(), 2).shuffleGrouping("SplitBolt").setNumTasks(2) ;

        // 设置集群配置文件，开启两个任务即两个节点运行此程序
        Config config = new Config();
        config.setNumWorkers(2);
        config.setDebug(true);

        // 本地集群模式，提交作业
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("hbase-storm", config, builder.createTopology());

    }
}
