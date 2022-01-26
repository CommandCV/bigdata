package com.myclass.demo.stream;

import akka.event.Logging;
import com.myclass.FlinkApplication;
import com.myclass.common.entry.Word;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class DataStreamCEPDemo extends FlinkApplication {


    public static void test() {
        SingleOutputStreamOperator<Word> stream = sEnv.fromElements(
                new Word(4L, "ignore", 0L),
                new Word(3L, "test", 0L),
                new Word(2L, "spark", 1644000000000L),
                new Word(1L, "flink", 1643075571545L),
                new Word(1L, "flink", 1643075571545L)
        ).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Word>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner((SerializableTimestampAssigner<Word>) (element, recordTimestamp) -> element.getLog_time()));

        Pattern<Word, ?> pattern = Pattern.<Word>begin("start").where(
                new SimpleCondition<Word>() {
                    @Override
                    public boolean filter(Word event) {
                        return event.getWord().equals("flink");
                    }
                }
        ).next("middle").where(
                new SimpleCondition<Word>() {
                    @Override
                    public boolean filter(Word event) {
                        return event.getWord().equals("flink");
                    }
                }
        );

        PatternStream<Word> patternStream = CEP.pattern(stream, pattern);
        SingleOutputStreamOperator<Logging.Warning> process = patternStream.process(
                new PatternProcessFunction<Word, Logging.Warning>() {
                    @Override
                    public void processMatch(
                            Map<String, List<Word>> pattern,
                            Context ctx,
                            Collector<Logging.Warning> out) {
                        System.out.println(pattern);
                        out.collect(new Logging.Warning("cep", Slf4j.class, "match!!!"));
                    }
                });
        process.print("----------cep:");
    }

    public static void test2() {
        KeyedStream<Word, Long> stream = sEnv.fromElements(
                new Word(3L, "test", 0L),
                new Word(4L, "ignore", 0L),
                new Word(2L, "spark", 1644000000000L),
                new Word(1L, "flink", 1643075571545L),
                new Word(1L, "flink", 1643075571545L),
                new Word(1L, "flink", 1643080000000L)
        ).assignTimestampsAndWatermarks(
                WatermarkStrategy.<Word>forBoundedOutOfOrderness(Duration.ZERO)
                        .withTimestampAssigner((SerializableTimestampAssigner<Word>) (element, recordTimestamp) -> element.getLog_time()))
                .keyBy(Word::getId);

        Pattern<Word, ?> pattern = Pattern.<Word>begin("start").where(new SimpleCondition<Word>() {
                    @Override
                    public boolean filter(Word event) {
                        return event.getWord().equals("flink");
                    }
                }
        ).next("middle").where(new SimpleCondition<Word>() {
                    @Override
                    public boolean filter(Word event) {
                        return event.getWord().equals("flink");
                    }
                }
        ).within(Time.seconds(5));

        PatternStream<Word> patternStream = CEP.pattern(stream, pattern);
        SingleOutputStreamOperator<Logging.Warning> process = patternStream.process(
                new PatternProcessFunction<Word, Logging.Warning>() {
                    @Override
                    public void processMatch(
                            Map<String, List<Word>> pattern,
                            Context ctx,
                            Collector<Logging.Warning> out) {
                        System.out.println(pattern);
                        out.collect(new Logging.Warning("cep", Slf4j.class, "match!!!"));
                    }
                });
        process.print("----------cep:");
    }


    public static void main(String[] args) throws Exception {
        sEnv.setParallelism(1);
        sEnv.getConfig().setAutoWatermarkInterval(1L);

        test();
        test2();

        sEnv.execute("cep demo");
    }
}
