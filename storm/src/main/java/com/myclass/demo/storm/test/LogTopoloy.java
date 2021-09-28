package com.myclass.demo.storm.test;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class LogTopoloy {
    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.setDebug(true);
       //设置Top构建器
        TopologyBuilder builder = new TopologyBuilder();
//            设置spout，id是唯一的String
        builder.setSpout("call spout", new FakeCallLogReaderSpout());
//        设置Bolt，shuffleGrouping 控制spout出来的tuple 数据怎样进入bolt
        builder.setBolt("call bolt create", new CallLogCreatorBolt()).shuffleGrouping("call spout");
//          设置Bolt，fieldsGrouping:按照指定的字段进行分组
        builder.setBolt("call bolt count", new CallLogCounterBolt()).fieldsGrouping("call bolt create", new Fields("call"));
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("LogAnalyserStorm",config , builder.createTopology());
        Thread.sleep(10000);
        cluster.shutdown();
    }

}
