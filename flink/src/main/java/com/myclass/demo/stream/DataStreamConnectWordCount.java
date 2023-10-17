package com.myclass.demo.stream;

import com.myclass.FlinkApplication;
import com.myclass.common.operator.flatmap.MyWordSplitFlatMapFunction;
import com.myclass.common.operator.reduce.MyWordCountReduceFunction;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

public class DataStreamConnectWordCount extends FlinkApplication {

    private static void connect() {
        SingleOutputStreamOperator<Tuple2<String, Integer>> stream1 = sEnv.readTextFile(
                        "flink/src/main/resources/common/word")
                .flatMap(new MyWordSplitFlatMapFunction());
        SingleOutputStreamOperator<Tuple2<String, Integer>> stream2 = sEnv.readTextFile(
                        "flink/src/main/resources/common/word2")
                .flatMap(new MyWordSplitFlatMapFunction());
        stream1.connect(stream2)
                .map(new CoMapFunction<Tuple2<String, Integer>, Tuple2<String, Integer>, Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> map1(Tuple2<String, Integer> value) {
                        return value;
                    }

                    @Override
                    public Tuple2<String, Integer> map2(Tuple2<String, Integer> value) {
                        return value;
                    }
                })
                .keyBy(tuple -> tuple.f0)
                .reduce(new MyWordCountReduceFunction())
                .writeAsText("flink/src/main/resources/result/two_stream_connect.txt");
    }

    private static void union() {
        SingleOutputStreamOperator<Tuple2<String, Integer>> stream1 = sEnv.readTextFile(
                        "flink/src/main/resources/common/word")
                .flatMap(new MyWordSplitFlatMapFunction());
        SingleOutputStreamOperator<Tuple2<String, Integer>> stream2 = sEnv.readTextFile(
                        "flink/src/main/resources/common/word2")
                .flatMap(new MyWordSplitFlatMapFunction());
        stream1.union(stream2).keyBy(tuple2 -> tuple2.f0)
                .reduce(new MyWordCountReduceFunction())
                .writeAsText("flink/src/main/resources/result/two_stream_union.txt");
    }

    public static void main(String[] args) throws Exception {
        // 设置并行度
        sEnv.setParallelism(1);
        sEnv.setRuntimeMode(RuntimeExecutionMode.BATCH);
        connect();
//        union();

        sEnv.execute("two streams word count");
    }

}
