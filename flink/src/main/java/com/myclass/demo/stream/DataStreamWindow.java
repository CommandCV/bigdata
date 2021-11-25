package com.myclass.demo.stream;

import com.myclass.FlinkApplication;
import com.myclass.common.entry.Student;
import com.myclass.common.operator.flatmap.MyWordSplitFlatMapFunction;
import com.myclass.common.operator.flatmap.MyWordSplitWithTimestampFlatMapFunction;
import com.myclass.common.operator.map.MyStudentMapFunction;
import com.myclass.common.watermark.assigner.MyTuple3TimestampAssigner;
import com.myclass.common.watermark.generator.MyTuple3PunctuatedWatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.LongSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.runtime.TupleSerializer;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.co.ProcessJoinFunction;
import org.apache.flink.streaming.api.functions.windowing.delta.DeltaFunction;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.evictors.DeltaEvictor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ContinuousEventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.triggers.DeltaTrigger;
import org.apache.flink.util.Collector;

import java.time.Duration;

/**
 * 流处理 窗口操作
 *
 * @author Yang
 */
public class DataStreamWindow extends FlinkApplication {

    // ------------------watermark------------------

    /**
     * 创建时间戳为单调递增的水印生成器--周期生成水印
     */
    public static void watermarkForMonotonousTimestampStrategy() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                // 认为数据中的时间戳都是单调递增的，窗口会在水印等于窗口触发时间时触发
                                .<Tuple3<String, Long, Integer>>forMonotonousTimestamps()
                                .withTimestampAssigner(new MyTuple3TimestampAssigner())
                );
    }

    /**
     * 创建允许时间戳有一定范围内乱序的水印生成器--周期生成水印
     */
    public static void watermarkForBoundedOutOfOrdernessStrategy() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                // 允许数据延迟1s到达，窗口会延迟1s触发计算
                                .<Tuple3<String, Long, Integer>>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner(new MyTuple3TimestampAssigner())
                );
    }

    /**
     * 创建自定义水印生成器
     */
    public static void watermarkForGeneratorStrategy() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        // 创建自定义周期水印生成器
//                        WatermarkStrategy.forGenerator((ctx) -> new MyTuple3PeriodicWatermarkGenerator())
                        // 创建自定义不断生成水印生成器
                        WatermarkStrategy.forGenerator((ctx) -> new MyTuple3PunctuatedWatermarkGenerator())
                );
    }


// --------------window--------------

    /**
     * 基于事件时间的滚动窗口 - forBoundedOutOfOrderness
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * testa,1637510403000
     * flink,1637510404000
     * flink,1637510405000
     * flink,1637510406000
     * testa,1637510403000
     * <p>
     * flink,1637510407000
     * flink,1637510408000
     * flink,1637510409000
     * flink,1637510410000
     * flink,1637510411000
     * <p>
     * 滚动窗口 - forMonotonousTimestamps
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * testa,1637510403000
     * flink,1637510404000
     * flink,1637510405000
     * testa,1637510403000
     * flink,1637510406000
     */
    public static void tumblingEventTimeWindow() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
//                                .<Tuple3<String, Long, Integer>>forMonotonousTimestamps()
                                .<Tuple3<String, Long, Integer>>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner(new MyTuple3TimestampAssigner()))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .sum(2)
                .print();
    }


    /**
     * 基于flink处理时间的滚动窗口
     * flink    test    word
     * flink    count   test
     * aaa  test    flink
     */
    public static void tumblingProcessTimeWindow() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitFlatMapFunction())
                .keyBy(tuple -> tuple.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .sum(1)
                .print();
    }


    /**
     * 滚动窗口
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * testa,1637510403000
     * flink,1637510404000
     * flink,1637510405000
     * flink,1637510406000
     * flink,1637510407000
     * flink,1637510408000
     * flink,1637510409000
     */
    public static void slidingEventTimeWindow() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple3<String, Long, Integer>>forMonotonousTimestamps()
                                .withTimestampAssigner((Tuple3<String, Long, Integer> element, long recordTimestamp) -> element.f1))
                .keyBy(tuple -> tuple.f0)
                .window(SlidingEventTimeWindows.of(Time.seconds(5), Time.seconds(1)))
                .sum(2)
                .print();
    }

    /**
     * 计数窗口，每5个元素触发一次计算
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * testa,1637510403000
     * flink,1637510404000
     * flink,1637510405000
     * flink,1637510406000
     * flink,1637510407000
     * flink,1637510408000
     * flink,1637510409000
     */
    public static void countWindow() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .keyBy(tuple -> tuple.f0)
                .countWindow(5)
                .sum(2)
                .print();
    }

    /**
     * 会话窗口，相邻会话相隔5秒触发计算
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * testa,1637510403000
     * flink,1637510404000
     * flink,1637510405000
     * flink,1637510406000
     * flink,1637510407000
     * flink,1637510408000
     * flink,1637510409000
     */
    public static void sessionWindow() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .keyBy(tuple -> tuple.f0)
                .window(ProcessingTimeSessionWindows.withGap(Time.seconds(5)))
                .sum(2)
                .print();
    }


    // ------------------join------------------


    /**
     * 双流关联操作，将两条流按key关联起来后做一些操作，例如转化成新的结构或提取某些字段
     * jack,12345678
     * rose,12344444
     * aaaa,456789011
     * bbbb,666668888
     */
    public static void join() {
        SingleOutputStreamOperator<Student> stream1 = sEnv.readTextFile("flink/src/main/resources/common/student")
                .map(new MyStudentMapFunction());
        SingleOutputStreamOperator<Tuple2<String, String>> stream2 = sEnv.socketTextStream("127.0.0.1", 7777)
                .map((MapFunction<String, Tuple2<String, String>>) (line) -> {
                    String[] message = line.split(",");
                    return new Tuple2<>(message[0], message[1]);
                }).returns(Types.TUPLE(Types.STRING, Types.STRING));

        stream1.join(stream2)
                .where(Student::getName)
                .equalTo(tuple2 -> tuple2.f0)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
                .apply(new JoinFunction<Student, Tuple2<String, String>, Tuple2<Student, String>>() {
                    @Override
                    public Tuple2<Student, String> join(Student student, Tuple2<String, String> tuple2) throws Exception {
                        return new Tuple2<>(student, tuple2.f1);
                    }
                }).print();
    }


    /**
     * 双流关联操作，将两条流按key关联起来后做一些操作，与join不同的是join是根据窗口进行划分的，而intervalJoin是根据上下界范围划分的.
     * interval join仅支持event time
     * jack,12345678
     * rose,12344444
     * aaaa,456789011
     * bbbb,666668888
     */
    public static void intervalJoin() {
        SingleOutputStreamOperator<Student> stream1 = sEnv.readTextFile("flink/src/main/resources/common/student")
                .map(new MyStudentMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Student>forMonotonousTimestamps()
                                .withTimestampAssigner((student, timestamp) -> System.currentTimeMillis())
                );
        SingleOutputStreamOperator<Tuple2<String, String>> stream2 = sEnv.socketTextStream("127.0.0.1", 7777)
                .map((MapFunction<String, Tuple2<String, String>>) (line) -> {
                    String[] message = line.split(",");
                    return new Tuple2<>(message[0], message[1]);
                }).returns(Types.TUPLE(Types.STRING, Types.STRING))
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple2<String, String>>forMonotonousTimestamps()
                                .withTimestampAssigner((tuple2, timestamp) -> System.currentTimeMillis())
                );
        stream1.keyBy(Student::getName)
                .intervalJoin(stream2.keyBy(tuple -> tuple.f0))
                // 与stream2中时间范围在当前时间前3s，当前时间之后5s内的数据，即取值范围为[-3, 5]
                .between(Time.seconds(-3), Time.seconds(5))
                .process(new ProcessJoinFunction<Student, Tuple2<String, String>, Tuple2<Student, String>>() {
                    @Override
                    public void processElement(Student left, Tuple2<String, String> right, Context ctx, Collector<Tuple2<Student, String>> out) throws Exception {
                        out.collect(new Tuple2<>(left, right.f1));
                    }
                }).print();
    }


    // ------------------trigger------------------

    /**
     * 基于事件时间固定时间间隔持续触发窗口，每当事件时间过去n个单位之后就触发计算（这里的时间间隔n并非左闭右开，而是真正过去n个单位，即n+1时才真正触发）
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * testa,1637510403000
     * flink,1637510404000
     * flink,1637510405000
     * flink,1637510406000
     * flink,1637510407000
     * flink,1637510408000
     * flink,1637510409000
     * flink,1637510410000
     */
    public static void continuousEventTimeTrigger() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple3<String, Long, Integer>>forMonotonousTimestamps()
                                .withTimestampAssigner((Tuple3<String, Long, Integer> element, long recordTimestamp) -> element.f1))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .trigger(ContinuousEventTimeTrigger.of(Time.seconds(2)))
                .sum(2)
                .print();
    }

    /**
     * 基于元素个数触发窗口计算，每当某个key的同一个窗口中有n个元素就触发计算
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * testa,1637510403000
     * flink,1637510404000
     * flink,1637510405000
     * flink,1637510406000
     * flink,1637510407000
     * flink,1637510408000
     * flink,1637510409000
     * flink,1637510410000
     */
    public static void countTrigger() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple3<String, Long, Integer>>forMonotonousTimestamps()
                                .withTimestampAssigner((Tuple3<String, Long, Integer> element, long recordTimestamp) -> element.f1))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .trigger(CountTrigger.of(2))
                .sum(2)
                .print();
    }

    /**
     * 基于两次事件之间超过一定阀值就触发计算
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * flink,1637510402001
     */
    @SuppressWarnings("unchecked")
    public static void deltaTrigger() {
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple3<String, Long, Integer>>forMonotonousTimestamps()
                                .withTimestampAssigner((Tuple3<String, Long, Integer> element, long recordTimestamp) -> element.f1))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .trigger(
                        DeltaTrigger.of(
                                // 阀值设置为2000，即2000毫秒
                                2000,
                                // 阀值计算方法
                                new DeltaFunction<Tuple3<String, Long, Integer>>() {
                                    @Override
                                    public double getDelta(Tuple3<String, Long, Integer> oldDataPoint, Tuple3<String, Long, Integer> newDataPoint) {
                                        // 阀值 = 新值的时间戳 - 旧值的时间戳
                                        return newDataPoint.f1 - oldDataPoint.f1;
                                    }
                                },
                                // 序列化类型，这里使用的是元组
                                new TupleSerializer<>((Class<Tuple3<String, Long, Integer>>) (Class<?>) Tuple2.class,
                                        new TypeSerializer[]{StringSerializer.INSTANCE, LongSerializer.INSTANCE, IntSerializer.INSTANCE})))
                .sum(2)
                .print();
    }


    /**
     * 窗口清除器，将窗口中的数据按照一定规则进行清除，清除顺序为先进先出
     * flink,1637510400000
     * flink,1637510401000
     * flink,1637510402000
     * testa,1637510403000
     * flink,1637510404000
     * flink,1637510405000
     * flink,1637510406000
     * flink,1637510407000
     * flink,1637510408000
     * flink,1637510409000
     * flink,1637510410000
     */
    public static void windowEvictor(){
        DataStreamSource<String> stream = sEnv.socketTextStream("127.0.0.1", 7777);
        stream.flatMap(new MyWordSplitWithTimestampFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy
                                .<Tuple3<String, Long, Integer>>forMonotonousTimestamps()
                                .withTimestampAssigner((Tuple3<String, Long, Integer> element, long recordTimestamp) -> element.f1))
                .keyBy(tuple -> tuple.f0)
                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                // 只保留3秒的数据
//                .evictor(TimeEvictor.of(Time.seconds(3)))
                // 只保留3条数据
//                .evictor(CountEvictor.of(3))
                // 只保留与最新值差值在阀值内的元素
                .evictor(DeltaEvictor.of(3000,
                        new DeltaFunction<Tuple3<String, Long, Integer>>() {
                            @Override
                            public double getDelta(Tuple3<String, Long, Integer> oldDataPoint, Tuple3<String, Long, Integer> newDataPoint) {
                                // 阀值 = 新值的时间戳 - 旧值的时间戳
                                return newDataPoint.f1 - oldDataPoint.f1;
                            }
                        }))
                .sum(2)
                .print();
    }



    public static void main(String[] args) throws Exception {
        sEnv.setParallelism(1);
        sEnv.getConfig().setAutoWatermarkInterval(1);

        tumblingEventTimeWindow();
        tumblingProcessTimeWindow();
        slidingEventTimeWindow();
        countWindow();
        sessionWindow();
        join();
        intervalJoin();
        continuousEventTimeTrigger();
        countTrigger();
        deltaTrigger();
        windowEvictor();

        sEnv.execute("window demo");
    }
}
