package com.myclass.common.metric.gauge;

import org.apache.flink.metrics.Gauge;

/**
 * 瞬时值指标
 * 每次只返回一个值
 */
public class MyGauge implements Gauge<Long> {

    private long value;

    public MyGauge() {
        value = 0;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    @Override
    public Long getValue() {
        return value;
    }
}
