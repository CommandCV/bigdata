package com.myclass.demo.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试
 * @author Yang 
 */
public class Test {
    
    private final static Configuration CONFIGURATION = HBaseConfiguration.create();

    private final static String TABLE_NAME = "user_log";

    /**
     * 从hbase中获得表中的数据
     * @param rowKey  行键
     * @param page  页数
     * @throws IOException IO异常
     */
    public static void findDataByRowKey(String rowKey,int page) throws IOException {
        //设置zookeeper所在地址
        CONFIGURATION.set("hbase.zookeeper.quorum", "node1,node2,node3");
        //获得表对象，设置表名
        HTable table=new HTable(CONFIGURATION, TABLE_NAME);
        //创建get对象，设置行键
        Get get=new Get(Bytes.toBytes(rowKey));
        //分页查取
        Filter filter =new PageFilter(page * 20);
        get.setFilter(filter);
        //设置版本数
        get.setMaxVersions(1000);
        //获得结果集
        Result result=table.get(get);
        //遍历结果集
        int i = 0;
        List<Map<String,String>> list = new ArrayList<>();
        //获得记录的条数
        int sum =  result.size() / 10;
        //创建出sum个map
        for(int j = 0; j < sum; j++){
            Map<String,String> map = new HashMap<>();
            map.put("rowKey",rowKey);
            list.add(map);
        }
        for(KeyValue kv :result.list()){
            list.get(i).put(Bytes.toString(kv.getQualifier()),Bytes.toString(kv.getValue()));
            i++;
            if (i == sum){
                //重置i
                i = 0;  
            }
        }

        System.out.println(list);
        //关闭连接
        table.close();
    }


    /**
     * 扫描表数据
     */
    private static void scanTable(int page) throws IOException {
        //设置zookeeper所在地址
        CONFIGURATION.set("hbase.zookeeper.quorum", "node1,node2,node3");
        //获得表连接池，并设置版本数
        HTablePool pool=new HTablePool(CONFIGURATION, 1000);
        //从连接池获得表对象
        HTableInterface table=pool.getTable(TABLE_NAME);
        //获得scan对象
        Scan scan=new Scan();
        scan.addFamily(Bytes.toBytes("info"));
        //设置查询的条数以及缓存
        Filter filter = new PageFilter(page * 20);
        scan.setMaxVersions(3);
        scan.setFilter(filter);
        scan.setCaching(200);
        //获得结果集
        ResultScanner scanner=table.getScanner(scan);
        List<Map<String,String>> list = new ArrayList<>();
        int i = 0;
        //遍历输出结果集
        for(Result result:scanner){
            Map<String,String> map = new HashMap<>();
            if(i >= (page - 1) * 20){
                for(KeyValue kv : result.raw()){
                    //获得行键
                    String key = new String(kv.getRow());
                    if(!map.containsKey(key)){
                        map.put("rowKey",key);
                    }
                    map.put(new String(kv.getQualifier()),new String(kv.getValue()));
                }
                list.add(map);
            }
            i++;
        }
        System.out.println(list.size());
        System.out.println(list);
        //关闭连接
        pool.close();
    }

    public static void main(String[] args) throws IOException {
    //设置zookeeper所在地址
        CONFIGURATION.set("hbase.zookeeper.quorum", "node1,node2,node3");
        //scanTable(1);
        findDataByRowKey("100381",1);
    }
}
