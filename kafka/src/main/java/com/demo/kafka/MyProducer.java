package com.demo.kafka;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;
import java.util.Scanner;

/**
 * 生产者
 * @author Yang
 */
public class MyProducer {

    private static void productData(){
        //创建配置文件
        Properties properties = new Properties();
        //设置broker列表
        properties.put("metadata.broker.list", "node1:9092");
        //串行化
        properties.put("serializer.class", "storm_kafka.serializer.StringEncoder");
        //设置确认收到的消息数，0为从不等待，1为只等待Leader,-1为等待所有
        properties.put("request.required.acks", "1");
        //创建生产者配置对象
        ProducerConfig config = new ProducerConfig(properties);
        //创建生产者
        Producer<String, String> producer = new Producer<>(config);
        //从控制台输入信息只要不为exit则发送至消费者
        Scanner scanner=new Scanner(System.in);
        String message;
        while(!(message=(scanner.nextLine())).equals("exit")){
            //设置消息，格式为 主题，Key, value   key的作用是用来分区
            KeyedMessage<String, String> msg = new KeyedMessage<>("test","100" ,message);
            //发送消息
            producer.send(msg);
        }
        System.out.println("send over!");
    }

    public static void main(String[] args) {
        productData();
    }
}
