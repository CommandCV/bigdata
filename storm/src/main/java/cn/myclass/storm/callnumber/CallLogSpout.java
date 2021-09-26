package cn.myclass.storm.callnumber;

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
 * Spout类,负责产生数据流
 * 读取外部的文件，将一行一行的数据发送给下游的bolt
 * 类似于Hadoop的MR中的inputFormat
 *
 * @author Yang
 */
public class CallLogSpout implements IRichSpout {

    private SpoutOutputCollector collector;

    private Random randomGenerator = new Random();

    private Integer idx = 0;

    /**
     * 初始化方法，只执行一次，一般用于打开连接
     *
     * @param conf      传入的是storm集群的信息和用户自定义配置文件，一般不用
     * @param context   上下文，一般不用
     * @param collector 数据输出的收集器，spout类将数据发给collector,collector再发送给storm集群
     */
    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
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
     * 下一个元组,tuple是数据传送的基本单位
     * 后台有while循环不断调用，每调用一次就发送一个元组
     */
    @Override
    public void nextTuple() {
        if (this.idx <= 1000) {
            List<String> mobileNumbers = new ArrayList<String>();
            mobileNumbers.add("1234123401");
            mobileNumbers.add("1234123402");
            mobileNumbers.add("1234123403");
            mobileNumbers.add("1234123404");

            Integer localIdx = 0;
            while (localIdx++ < 100 && this.idx++ < 1000) {
                //取出主叫
                String caller = mobileNumbers.get(randomGenerator.nextInt(4));
                //取出被叫
                String callee = mobileNumbers.get(randomGenerator.nextInt(4));
                while (caller == callee) {
                    //重新取出被叫
                    callee = mobileNumbers.get(randomGenerator.nextInt(4));
                }
                //模拟通话时长
                Integer duration = randomGenerator.nextInt(60);

                //输出元组
                this.collector.emit(new Values(caller, callee, duration));
            }
        }
    }

    @Override
    public void ack(Object msgId) {
        System.out.println("spout : ack = " + msgId);
    }

    @Override
    public void fail(Object msgId) {

    }

    /**
     * 声明数据的字段
     * @param declarer 声明发出的数据是什么
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("from", "to", "duration"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
