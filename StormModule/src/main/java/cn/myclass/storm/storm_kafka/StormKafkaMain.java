package cn.myclass.storm.storm_kafka;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

import java.util.UUID;

/**
 * storm集成Kafka，将Kafka的主题当作水龙头
 * 即数据源，之后通过storm的bolt处理业务逻辑
 * @author Yang
 */
public class StormKafkaMain {
    public static void main(String[] args) {
        // 创建拓扑
        TopologyBuilder builder = new TopologyBuilder();

        // 配置zookeeper映射，即kafka主题所在的zookeeper
        ZkHosts zkHosts = new ZkHosts("node1:2181");
        // 创建一个数据源配置文件，参数为 zookeeper地址列表，主题，主题所在根路径，一个唯一的任务ID（用来保存读取状态，以便下次能够继续读取而不是从头消费）
        SpoutConfig spoutConfig = new SpoutConfig(zkHosts, "test" , "/test", UUID.randomUUID().toString());
        // 创建一个数据源配置方案，此参数的作用为声明输出数据的字段，对kafka中的数据进行反序列化将byte字节转化为tuple元组
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        // 通过配置创建一个kafka数据源
        KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);

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
