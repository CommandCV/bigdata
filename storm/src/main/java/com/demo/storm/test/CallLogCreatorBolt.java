package com.demo.storm.test;

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
public class CallLogCreatorBolt implements IRichBolt {
    private OutputCollector collector;
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        //拿到主叫
        String from = tuple.getString(0);
        //拿到被叫
        String to = tuple.getString(1);
        //获得通话时长
        Integer duration = tuple.getInteger(2);
        //生成新的元组
        collector.emit(new Values(from+"-"+to, duration));
    }

    @Override
    public void cleanup() {

    }
    /**
     * 声明输出的字段含义
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("call","duration"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
