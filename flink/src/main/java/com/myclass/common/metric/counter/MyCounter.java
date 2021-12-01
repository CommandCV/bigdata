package com.myclass.common.metric.counter;

import org.apache.flink.metrics.Counter;

/**
 * 计数器指标
 * 每来一条数据累加一次
 */
public class MyCounter implements Counter {

    private long value;

    public MyCounter() {
        this.value = 0;
    }

    @Override
    public void inc() {
        value++;
    }

    @Override
    public void inc(long num) {
        value += num;
    }

    @Override
    public void dec() {
        value--;
    }

    @Override
    public void dec(long num) {
        value -= num;
    }

    @Override
    public long getCount() {
        return value;
    }
}
