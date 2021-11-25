package com.myclass.common.watermark.generator;

import org.apache.flink.api.common.eventtime.Watermark;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkOutput;
import org.apache.flink.api.java.tuple.Tuple3;

/**
 * 自定义水印生成器-周期生成
 */
public class MyTuple3PeriodicWatermarkGenerator implements WatermarkGenerator<Tuple3<String, Long, Integer>> {

    /**
     * 保留当前最大时间戳
     */
    private long currentMaxTimestamp = Long.MIN_VALUE;

    /**
     * 周期生成水印时在event事件中不需要发送水印
     */
    @Override
    public void onEvent(Tuple3<String, Long, Integer> event, long eventTimestamp, WatermarkOutput output) {
        currentMaxTimestamp = Math.max(eventTimestamp, currentMaxTimestamp);
    }

    /**
     * 周期生成水印，每次将次周期内最大的时间戳当作水印发送
     */
    @Override
    public void onPeriodicEmit(WatermarkOutput output) {
        output.emitWatermark(new Watermark(currentMaxTimestamp));
    }

}
