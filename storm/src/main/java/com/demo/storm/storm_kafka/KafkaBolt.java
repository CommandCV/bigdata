package com.demo.storm.storm_kafka;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

/**
 * 集成kafka的storm转接头bolt
 * 从kafka的topic中获得数据
 * @author Yang
 */
public class KafkaBolt implements IRichBolt {
    private OutputCollector collector;

    /**
     *
     * @param map 配置文件集合
     * @param topologyContext 上下文环境
     * @param outputCollector 收集器
     */
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector ;
    }

    /**
     * 处理业务逻辑，直接将数据输出
     * @param tuple 从kafka传来的数据
     */
    @Override
    public void execute(Tuple tuple) {
        String line = tuple.getString(0);
        System.out.println(line);
    }

    @Override
    public void cleanup() {

    }

    /**
     * 不设置
     * @param outputFieldsDeclarer 字段声明
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
