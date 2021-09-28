package com.demo.storm.wordcount;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * @author Yang
 */
public class CreateWordBolt implements IRichBolt {

    private OutputCollector collector;


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    /**
     *  处理从上游传过来的元组数据，将
     *  单词出现的次数进行统计
     * @param tuple 从上游传过来的元组
     */
    @Override
    public void execute(Tuple tuple) {
        String word = tuple.getStringByField("word");
        collector.emit(new Values(word,1));
    }


    @Override
    public void cleanup() {

    }

    /**
     * 描述字段信息
     * @param outputFieldsDeclarer 描述器
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word","count"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
