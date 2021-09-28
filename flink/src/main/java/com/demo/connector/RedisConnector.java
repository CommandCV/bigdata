package com.demo.connector;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;


/**
 * flink连接redis
 * 将结果写入redis
 * @author Yang
 */
public class RedisConnector {

    /**
     * 自定义redis数据类型的映射
     */
    public static class MyRedisMapper implements RedisMapper<Tuple2<String, Integer>> {

        /**
         * 设置redis数据类型以及hash表键的名称。
         * 即将处理结果(元组Tuple2<String, Integer>)映射为redis里的hash表数据类型
         */
        @Override
        public RedisCommandDescription getCommandDescription() {
            return new RedisCommandDescription(RedisCommand.HSET, "flink");
        }

        /**
         * 设置键，从元组中获取到单词作为键
         * @param data 元组数据
         * @return java.lang.String 键
         */
        @Override
        public String getKeyFromData(Tuple2<String, Integer> data) {
            return data.f0;
        }

        /**
         * 设置键的值，从元组中获取到单词出现的次数设置为键的值
         * @param data 元组数据
         * @return java.lang.String 键对应的值
         */
        @Override
        public String getValueFromData(Tuple2<String, Integer> data) {
            return data.f1.toString();
        }
    }

    public static void main(String[] args) throws Exception {
        // 获得流执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 将元素变为元组并计算出现的次数
        DataStream<Tuple2<String, Integer>> stream = env.fromElements("hello","hello","hi").map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String s) throws Exception {
                return new Tuple2<>(s, 1);
            }
        }).keyBy(0).sum(1);

        // 单机连接redis
        FlinkJedisPoolConfig conf = new FlinkJedisPoolConfig.Builder().setHost("hadoop").build();
        // 将结果写入redis
        stream.addSink(new RedisSink<>(conf, new MyRedisMapper()));
        // 执行任务
        env.execute("redis sink");
    }
}
