package cn.myclass.storm.test;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author Yang
 */
public class FakeCallLogReaderSpout implements IRichSpout {

    private SpoutOutputCollector collector;

    private boolean complated = false;

    private Random randomGenerator = new Random();

    private Integer index = 0;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;

    }

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
     *
     */
    @Override
    public void nextTuple() {
            if (this.index <= 1000){
                List<String> mobileNumbers = new ArrayList<String>(16);
                mobileNumbers.add("1234123401");
                mobileNumbers.add("1234123402");
                mobileNumbers.add("1234123403");
                mobileNumbers.add("1234123404");
                Integer localIndex = 0;
                while (localIndex++ < 100&&this.index++ <=1000){
                    String fromMobileNumber = mobileNumbers.get(randomGenerator.nextInt(4));
                    String toMobileNumber = mobileNumbers.get(randomGenerator.nextInt(4));
                    while(fromMobileNumber == toMobileNumber){
                        toMobileNumber = mobileNumbers.get(randomGenerator.nextInt(4));
                    }
                    Integer duration = randomGenerator.nextInt(60);
                    //转化为元组发出
                    this.collector.emit(new Values(fromMobileNumber, toMobileNumber, duration));
                }
            }
    }

    @Override
    public void ack(Object o) {

    }

    @Override
    public void fail(Object o) {

    }

    /**
     * 声明输出的字段含义
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("from","to","duration"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
