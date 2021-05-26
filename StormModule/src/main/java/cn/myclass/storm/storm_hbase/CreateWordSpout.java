package cn.myclass.storm.storm_hbase;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 从文件中读取数据一行一行的发送给切分的bolt
 * @author Yang
 */
public class CreateWordSpout implements IRichSpout {

    private SpoutOutputCollector collector;
    private FileInputStream fileInputStream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;
    private String line;

    /**
     *  从word文件中读取单词，将单词和次数发送给bolt处理
     * @param map 配置文件的集合
     * @param topologyContext 拓扑上下文
     * @param spoutOutputCollector 收集器
     */
    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        try {
            //本地集群模式文件路径
            this.fileInputStream=new FileInputStream("target/classes/file/word");
            //集群模式文件路径
            //this.fileInputStream=new FileInputStream("/file/word");
            this.inputStreamReader=new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
            this.bufferedReader=new BufferedReader(inputStreamReader);
        } catch (Exception e) {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                fileInputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    /**
     * 关闭资源
     */
    @Override
    public void close() {
        try {
            bufferedReader.close();
            inputStreamReader.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    /**
     *  将一行单词发送给Bolt
     */
    @Override
    public void nextTuple() {
        try {
            while( (line = bufferedReader.readLine()) != null){
                collector.emit(new Values(line));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void ack(Object o) {

    }

    @Override
    public void fail(Object o) {

    }

    /**
     * @param outputFieldsDeclarer 声明字段名
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("line"));
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
