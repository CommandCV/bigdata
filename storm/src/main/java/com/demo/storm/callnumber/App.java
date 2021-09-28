package com.demo.storm.callnumber;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * App
 */
public class App {
    public static void main(String[] args) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        //设置Spout类
        builder.setSpout("spout", new CallLogSpout());
        //设置creator-Bolt类
        builder.setBolt("creator-bolt", new CallLogCreatorBolt()).shuffleGrouping("spout");
        //设置counter-Bolt类
        builder.setBolt("counter-bolt", new CallLogCounterBolt()).fieldsGrouping("creator-bolt", new Fields("call"));

        Config conf = new Config();
        conf.setDebug(true);

        /*
         * 本地模式storm
         */
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("LogAnalyserStorm", conf, builder.createTopology());
        System.out.println("start!");
        Thread.sleep(10000);
        cluster.shutdown();
        //在集群上运行storm
        //StormSubmitter.submitTopology("mytop", conf, builder.createTopology());
    }
}
