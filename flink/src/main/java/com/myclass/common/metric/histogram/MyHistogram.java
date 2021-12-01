package com.myclass.common.metric.histogram;

import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.HistogramStatistics;

import java.util.Stack;

/**
 * 直方图指标
 * 统计最近10个元素的最大值、最小值、平均值等
 */
public class MyHistogram implements Histogram {

    private static final int LIMIT_SIZE = 10;
    private long total;
    private Stack<Long> stack;

    public MyHistogram() {
        total = 0;
        stack = new Stack<>();
    }

    @Override
    public void update(long value) {
        total++;
        stack.push(value);
        while (stack.size() > LIMIT_SIZE) {
            stack.pop();
        }
    }

    @Override
    public long getCount() {
        return total;
    }

    @Override
    public HistogramStatistics getStatistics() {
        return new HistogramStatistics() {

            @Override
            public double getQuantile(double v) {
                return v;
            }

            @Override
            public long[] getValues() {
                return stack.stream().mapToLong(value -> value).toArray();
            }

            @Override
            public int size() {
                return stack.size();
            }

            @Override
            public double getMean() {
                return stack.stream().mapToLong(value -> value).sum() / (LIMIT_SIZE * 1.0);
            }

            @Override
            public double getStdDev() {
                return 0;
            }

            @Override
            public long getMax() {
                return stack.stream().mapToLong(value -> value).max().orElse(0);
            }

            @Override
            public long getMin() {
                return stack.stream().mapToLong(value -> value).min().orElse(0);
            }
        };
    }
}
