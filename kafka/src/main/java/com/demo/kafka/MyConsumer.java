package com.demo.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 消费者
 * @author Yang
 */
public class MyConsumer {
    private static void receiveData(){
        //创建配置文件
        Properties properties = new Properties();
        //配置zookeeper连接
        properties.put("zookeeper.connect", "node1:2181");
        //设置组id
        properties.put("group.id", "0");
        //设置zookeeper会话超时时间
        properties.put("zookeeper.session.timeout.ms", "500");
        //设置zookeeper的同步时间间隔
        properties.put("zookeeper.sync.time.ms", "250");
        //设置提交时间间隔
        properties.put("auto.commit.interval.ms", "1000");
        //设置自动重置偏移量
        properties.put("auto.offset.reset", "smallest");
        //创建消费者配置对象
        ConsumerConfig config = new ConsumerConfig(properties);
        //创建java消费者连接对象
        ConsumerConnector consumerConnector=Consumer.createJavaConsumerConnector(config);
        Map<String, Integer> map = new HashMap<String, Integer>(16);
        //一次从主题中获取n条数据
        map.put("test", 1);
        //获取消费者的信息流集合
        Map<String, List<KafkaStream<byte[], byte[]>>> msgs = consumerConnector.createMessageStreams(map);
        //从集合中获得主题为test的信息流集合
        List<KafkaStream<byte[], byte[]>> msgList = msgs.get("test");
        //遍历输出信息流中的数据
        for(KafkaStream<byte[],byte[]> stream : msgList){
            //获得信息流的迭代器
            ConsumerIterator<byte[],byte[]> it = stream.iterator();
            while(it.hasNext()){
                byte[] message = it.next().message();
                System.out.println(new String(message));
            }
        }
    }
    public static void main(String[] args) {
        receiveData();
    }
}
