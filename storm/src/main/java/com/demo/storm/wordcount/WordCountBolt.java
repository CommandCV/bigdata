package com.demo.storm.wordcount;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yang
 */
public class WordCountBolt implements IRichBolt {


    private HashMap<String, Integer> wordMap = new HashMap<>();

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    }

    /**
     *  将数据放入集合中进行统计并输出
     * @param tuple 元组数据
     */
    @Override
    public void execute(Tuple tuple) {
        String word = tuple.getStringByField("word");
        Integer count = tuple.getIntegerByField("count");
        if(!wordMap.containsKey(word)){
            wordMap.put(word,1);
        }else{
            wordMap.put(word,count+1);
        }
        System.out.println("-----------------------------------------");
        System.out.println(word);
        System.out.println(wordMap);
    }

    @Override
    public void cleanup() {

    }
    /**
     *  由于没有下一个Bolt,所以可以不写
     * @param outputFieldsDeclarer 描述器
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
