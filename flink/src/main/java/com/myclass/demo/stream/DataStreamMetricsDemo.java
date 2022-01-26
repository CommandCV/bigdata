package com.myclass.demo.stream;

import com.myclass.FlinkApplication;
import com.myclass.common.entry.Student;
import com.myclass.common.metric.counter.MyCounter;
import com.myclass.common.metric.gauge.MyGauge;
import com.myclass.common.metric.histogram.MyHistogram;
import com.myclass.common.metric.meter.MyMeter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.MeterView;
import org.apache.flink.streaming.api.datastream.DataStreamSource;

public class DataStreamMetricsDemo extends FlinkApplication {


    /**
     * 计数器指标-统计元素个数
     * nc -lk 7777
     * jack,男,30,北京
     * rose,女,32,上海
     * jj,男,29,广州
     * ta,女,28,深圳
     * wei,男,27,杭州
     * wei,女,30,杭州
     * wei,男,27,杭州
     */
    public static void mapCounter() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.map(new RichMapFunction<String, Student>() {
            private transient Counter counter;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                this.counter = getRuntimeContext().getMetricGroup().counter("myMapCounter", new MyCounter());
                System.out.println("RichMap function open...");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("RichMap function close...");
            }

            @Override
            public Student map(String value) {
                this.counter.inc();
                System.out.println("当前计数为:" + this.counter.getCount());
                String[] data = value.split(",");
                return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
            }
        }).startNewChain().print();
    }

    /**
     * 瞬时值指标-统计延迟
     * nc -lk 7777
     * flink,1637918524960
     * testa,1637918524961
     * flink,1637918550000
     */
    public static void mapGauge() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.map(new RichMapFunction<String, Tuple2<String, Long>>() {
            private transient MyGauge gauge;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                this.gauge = getRuntimeContext().getMetricGroup().gauge("myMapGauge", new MyGauge());
                System.out.println("RichMap function open...");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("RichMap function close...");
            }

            @Override
            public Tuple2<String, Long> map(String value) {
                String[] data = value.split(",");
                String word = data[0];
                long eventTime = Long.parseLong(data[1]);
                long latencyTime = System.currentTimeMillis() - eventTime;
                System.out.println("数据延迟:" + latencyTime + "ms");
                gauge.setValue(latencyTime);
                return new Tuple2<>(word, eventTime);
            }
        }).print();
    }

    /**
     * 平均值指标-统计map数据量平均值
     * nc -lk 7777
     * jack,男,30,北京
     * rose,女,32,上海
     * jj,男,29,广州
     * ta,女,28,深圳
     * wei,男,27,杭州
     * wei,女,30,杭州
     * wei,男,27,杭州
     */
    public static void mapMeter() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.map(new RichMapFunction<String, Student>() {
            private transient MyMeter meter;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                this.meter = getRuntimeContext().getMetricGroup().meter("myMapMeter", new MyMeter(10));
                System.out.println("RichMap function open...");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("RichMap function close...");
            }

            @Override
            public Student map(String value) {
                this.meter.markEvent();
                String[] data = value.split(",");
                return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
            }
        }).print();
    }

    public static void mapMeter2() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.map(new RichMapFunction<String, Student>() {
            private transient Meter meter;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                this.meter = getRuntimeContext().getMetricGroup().meter("myMapMeter", new DropwizardMeterWrapper(new com.codahale.metrics.Meter()));
                System.out.println("RichMap function open...");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("RichMap function close...");
            }

            @Override
            public Student map(String value) {
                this.meter.markEvent();
                String[] data = value.split(",");
                return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
            }
        }).print();
    }

    public static void mapMeter3() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.map(new RichMapFunction<String, Student>() {
            private transient Meter meter;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                this.meter = getRuntimeContext().getMetricGroup().meter("myMapMeter", new MeterView(60));
                System.out.println("RichMap function open...");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("RichMap function close...");
            }

            @Override
            public Student map(String value) {
                this.meter.markEvent();
                String[] data = value.split(",");
                return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
            }
        }).print();
    }

    /**
     * 直方图指标-统计map数据量
     * nc -lk 7777
     * jack,男,30,北京
     * rose,女,32,上海
     * jj,男,29,广州
     * ta,女,28,深圳
     * wei,男,27,杭州
     * wei,女,30,杭州
     * wei,男,27,杭州
     */
    public static void mapHistogram() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.map(new RichMapFunction<String, Student>() {
            private transient MyHistogram histogram;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                this.histogram = getRuntimeContext().getMetricGroup().histogram("myMapHistogram", new MyHistogram());
                System.out.println("RichMap function open...");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("RichMap function close...");
            }

            @Override
            public Student map(String value) {
                histogram.update(Long.parseLong(value.split(",")[2]));
                String[] data = value.split(",");
                return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
            }
        }).print();
    }


    public static void main(String[] args) throws Exception {
        sEnv.setParallelism(1);

        mapCounter();
        mapGauge();
        mapMeter();
        mapMeter2();
        mapMeter3();
        mapHistogram();

        sEnv.execute("metrics demo");

    }

}
