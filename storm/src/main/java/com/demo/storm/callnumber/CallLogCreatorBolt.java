package com.demo.storm.callnumber;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * 接收从源头来的一行数据
 * 将数据进行拼接整理
 * @author Yang
 */
public class CallLogCreatorBolt implements IRichBolt {

    private OutputCollector collector;
    /**
     *  初始化方法，只运行一次
     * @param stormConf 配置文件
     * @param context 上下文，一般不用
     * @param collector 收集器
     */
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector ;
    }
    /**
     *  执行的业务逻辑
     * @param tuple 从上游获取的一个元组数据
     */
    @Override
    public void execute(Tuple tuple) {
        //处理通话记录
        String from = tuple.getString(0);
        String to = tuple.getString(1);
        Integer duration = tuple.getInteger(2);
        //产生新的tuple
        collector.emit(new Values(from + " - " + to, duration));
    }

    @Override
    public void cleanup() {

    }

    /**
     * 设置输出字段的名称
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("call", "duration"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
