package com.myclass.common.watermark.generator;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.java.tuple.Tuple3;

/**
 * 自定义水印生成器-不断生成
 */
public class MyTuple3PunctuatedWatermarkGenerator implements WatermarkGenerator<Tuple3<String, Long, Integer>> {

    /**
     * 保留当前最大时间戳
     */
    private long currentMaxTimestamp = Long.MIN_VALUE;

    /**
     * 每有一条数据调用此方法时就将当前最大的时间戳当作水印发送
     */
    @Override
    public void onEvent(Tuple3<String, Long, Integer> event, long eventTimestamp, WatermarkOutput output) {
        currentMaxTimestamp = Math.max(eventTimestamp, currentMaxTimestamp);
        output.emitWatermark(new Watermark(currentMaxTimestamp));
    }

    /**
     * 不断生成水印无需关注周期发送方法
     */
    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
    }
}
