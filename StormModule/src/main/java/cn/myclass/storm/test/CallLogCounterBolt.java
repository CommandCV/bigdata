package cn.myclass.storm.test;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;
/**
 * @author Yang
 */
public class CallLogCounterBolt implements IRichBolt {
    private  Map<String,Integer> countMap;
    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        this.countMap = new HashMap<String, Integer>();
    }

    @Override
    public void execute(Tuple input) {
        String call = input.getString(0);
        //input.getInteger(1);
        if(!countMap.containsKey(call)){
            countMap.put(call, 1);
        }else{
            Integer i = countMap.get(call) + 1;
            countMap.put(call, i);
        }
        //消费元组
        collector.ack(input);
    }

    @Override
    public void cleanup() {
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            System.out.println(entry.getKey()+"???"+entry.getValue());
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("call"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
