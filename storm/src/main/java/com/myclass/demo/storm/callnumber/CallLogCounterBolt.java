package com.myclass.demo.storm.callnumber;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * 从上游bolt中获得数据
 * 将传过来的通话记录进行计数
 * @author Yang
 */
public class CallLogCounterBolt implements IRichBolt{

    private Map<String, Integer> counterMap;

    private OutputCollector collector;

    /**
     *  初始化方法，只执行一次
     * @param stormConf 配置文件
     * @param context 上下文，一般不用
     * @param collector 收集器
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.counterMap = new HashMap<>();
        this.collector = collector;
    }

    /**
     *  处理业务逻辑，将通话记录进行计数
     * @param tuple 从上游获得的一个元组数据
     */
    @Override
    public void execute(Tuple tuple) {
        String call = tuple.getString(0);

        if (!counterMap.containsKey(call)) {
            counterMap.put(call, 1);
        } else {
            Integer c = counterMap.get(call) + 1;
            counterMap.put(call, c);
        }
        collector.ack(tuple);
    }

    /**
     * 停掉storm之后运行的方法
     */
    @Override
    public void cleanup() {
        for (Map.Entry<String, Integer> entry : counterMap.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    /**
     * 描述输出数据的字段名
     * 如果不发送数据则可以不实现
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("call"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
