package com.myclass.common.metric.meter;

import org.apache.flink.metrics.Meter;

/**
 * 均值指标
 * 求出一定间隔内某指标的平均值
 */
public class MyMeter implements Meter {

    private int interval;
    private long value;
    private long lastTimestamp;

    public MyMeter(int interval) {
        this.interval = interval;
        this.value = 0;
        this.lastTimestamp = System.currentTimeMillis();
    }

    @Override
    public void markEvent() {
        clear();
        this.value++;
    }

    @Override
    public void markEvent(long value) {
        clear();
        this.value += value;
    }

    @Override
    public double getRate() {
        return ((double) this.getCount()) / ((double) interval);
    }

    @Override
    public long getCount() {
        return value;
    }

    private void clear() {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - lastTimestamp >= interval * 1000) {
            System.out.println(currentTimestamp + ", count:" + this.getCount() + ", rate:" + this.getRate());
            lastTimestamp = currentTimestamp;
            this.value = 0;
        }
    }
}
