package com.myclass.common.watermark.assigner;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.java.tuple.Tuple3;

/**
 * 自定义时间戳分配器
 */
public class MyTuple3TimestampAssigner implements SerializableTimestampAssigner<Tuple3<String, Long, Integer>> {

    @Override
    public long extractTimestamp(Tuple3<String, Long, Integer> element, long recordTimestamp) {
        return element.f1;
    }
}
