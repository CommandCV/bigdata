package cn.myclass.storm.storm_hbase;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;

/**
 * 将数据源传过来的一行数据进行切分并初始化次数为1次
 * @author Yang
 */
public class SplitBolt implements IRichBolt {
    private OutputCollector collector;

    /**
     * @param map 配置文件集合
     * @param topologyContext 拓扑上下文
     * @param outputCollector 收集器
     */
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.collector = outputCollector;
    }

    /**
     * 将数据进行切分，次数初始化为1次
     * 最后发送给HBaseBolt进行处理
     * @param tuple 从上游传来的元组数据
     */
    @Override
    public void execute(Tuple tuple) {
        String[] line = tuple.getString(0).split("\t");
        for (String s: line) {
            collector.emit(new Values(s));
        }
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
        outputFieldsDeclarer.declare(new Fields("word"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
