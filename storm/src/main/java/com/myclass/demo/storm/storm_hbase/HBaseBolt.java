package com.myclass.demo.storm.storm_hbase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

/**
 * 集成Hbase的bolt，将传送过来的数据 进行计数并写入hbase中
 *
 * @author Yang
 */
public class HBaseBolt implements IRichBolt {

    private Table table;
    private Map<String, Integer> map;

    /**
     * 打开Hbase连接，配置Hbase信息
     *
     * @param map             配置文件集合
     * @param topologyContext 上下文环境
     * @param outputCollector 收集器
     */
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.map = new HashMap<>(16);
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "master,node1,node2,node3");
        try {
            Connection conn = ConnectionFactory.createConnection(configuration);
            // 创建表对象
            table = conn.getTable(TableName.valueOf("storm_wordcount"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理业务逻辑，将数据输出,并将单词 以及出现的次数存入HBase
     *
     * @param tuple 从Spout传来的数据
     */
    @Override
    public void execute(Tuple tuple) {
        // 获得数据
        String word = tuple.getString(0);
        //将数据放入集合中，以边最后观察
        if (!map.containsKey(word)) {
            map.put(word, 1);
        } else {
            map.put(word, map.get(word) + 1);
        }

        // 设置行键列族和列，使用hbase的计数器进行计数
        // 行键
        byte[] rowKey = Bytes.toBytes(word);
        // 列族
        byte[] columnFamily = Bytes.toBytes("info");
        // 列
        byte[] column = Bytes.toBytes("count");
        try {
            //  行键      列族       列  增加的值
            table.incrementColumnValue(rowKey, columnFamily, column, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 输出map中的数据，以边查看结果
        System.out.println("------------------------------");
        System.out.println(map);

    }

    @Override
    public void cleanup() {

    }

    /**
     * 不设置
     *
     * @param outputFieldsDeclarer 字段声明
     */
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
