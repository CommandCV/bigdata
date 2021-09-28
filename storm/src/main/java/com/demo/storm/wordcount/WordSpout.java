package com.demo.storm.wordcount;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * 产生单词的源头
 * @author Yang
 */
public class WordSpout  implements IRichSpout {

    private SpoutOutputCollector collector;

    private FileInputStream fileInputStream;

    private InputStreamReader inputStreamReader;

    private BufferedReader bufferedReader;

    private String string;

    /**
     *  打开连接，将数据传给bolt
     * @param map 传入的是storm集群的信息以及自定义配置，一般不用
     * @param topologyContext 上下文环境，一般不用
     * @param spoutOutputCollector 收集器
     */
    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        try {
            //本地路径
            //this.fileInputStream=new FileInputStream("target/classes/file/word");
            //集群路径
            this.fileInputStream=new FileInputStream("target/classes/file/word");
            this.inputStreamReader=new InputStreamReader(fileInputStream,"UTF-8");
            this.bufferedReader=new BufferedReader(inputStreamReader);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭时的方法
     */
    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    /**
    *  业务逻辑方法
    *  将一行的数据进行切割，然后将每个单词都当作一个元组，传给下游
    */
    @Override
    public void nextTuple() {
        try {
            while( (string=bufferedReader.readLine()) !=null){
                String[] words = string.split("\t");
                for(String word :words){
                    collector.emit(new Values(word));

                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * ack服务是用来确认信息发送是否成功的
     * 当一个数据发送成功时就会调用成功的方法
     * ack成功时调用的方法
     * @param o 传入的对象
     */
    @Override
    public void ack(Object o) {

    }

    /**
     * ack失败时调用的方法
     */
    @Override
    public void fail(Object o) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
