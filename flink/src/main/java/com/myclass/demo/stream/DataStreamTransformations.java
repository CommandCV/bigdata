package com.myclass.demo.stream;

import com.myclass.FlinkApplication;
import com.myclass.common.entry.Student;
import com.myclass.common.operator.flatmap.MyStudentFlatMapFunction;
import com.myclass.common.operator.flatmap.MyWordSplitFlatMapFunction;
import com.myclass.common.operator.map.MyStudentMapFunction;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class DataStreamTransformations extends FlinkApplication {

// ------------------operator------------------

    /**
     * 一对一转换，一个输入对应一个输出
     */
    public static void map() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.map(new MapFunction<String, Student>() {
            @Override
            public Student map(String value) {
                String[] data = value.split(",");
                return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
            }
        }).print();
    }


    public static void map2() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.map(new MyStudentMapFunction()).print();
    }

    public static void map3() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.map(new RichMapFunction<String, Student>() {

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                System.out.println("RichMap function open...");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("RichMap function close...");
            }

            @Override
            public Student map(String value) {
                String[] data = value.split(",");
                return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
            }
        }).print();
    }

    public static void map4() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.map(line -> {
            String[] data = line.split(",");
            return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
        }).print();
    }


    /**
     * 一对多转换，一个输入可对应多个输出
     */
    public static void flatMap() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.flatMap(new FlatMapFunction<String, Student>() {
            @Override
            public void flatMap(String value, Collector<Student> out) {
                String[] data = value.split(",");
                out.collect(new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim()));
            }
        }).print();
    }

    public static void flatMap2() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.flatMap(new RichFlatMapFunction<String, Student>() {

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                System.out.println("RichFlatMap function open...");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("RichFlatMap function open...");
            }

            @Override
            public void flatMap(String value, Collector<Student> out) throws Exception {
                String[] data = value.split(",");
                out.collect(new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim()));
            }
        }).print();
    }

    public static void flatMap3() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.flatMap(new MyStudentFlatMapFunction()).print();
    }

    public static void flatMap4() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.flatMap((String line, Collector<Student> collector) -> {
            String[] data = line.split(",");
            collector.collect(new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim()));
        }).returns(TypeInformation.of(Student.class)).print();
    }


    /**
     * 过滤数据，当返回结果为true时保留，false则去除
     */
    public static void filter() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.map(new MyStudentMapFunction())
                .filter(new FilterFunction<Student>() {
                    @Override
                    public boolean filter(Student value) {
                        return value.getAge() < 30;
                    }
                }).print();
    }

    public static void filter2() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.map(new MyStudentMapFunction())
                .filter((FilterFunction<Student>) value -> value.getAge() < 30)
                .print();
    }


// ------------------keyedStream------------------

    /**
     * 按key分区, 统计当前单词出现的次数
     */
    public static void keyBy() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/word");
        stream.flatMap(new MyWordSplitFlatMapFunction())
                .keyBy(0).sum(1).print();
    }

    public static void keyBy2() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/word");
        stream.flatMap(new MyWordSplitFlatMapFunction())
                .keyBy(new KeySelector<Tuple2<String, Integer>, String>() {
                    @Override
                    public String getKey(Tuple2<String, Integer> wordCount) throws Exception {
                        return wordCount.f0;
                    }
                }).sum(1).print();
    }

    public static void keyBy3() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/word");
        stream.flatMap(new MyWordSplitFlatMapFunction())
                .keyBy(wordCount -> wordCount.f0).sum(1).print();
    }


    public static void reduce() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/word");
        stream.flatMap(new MyWordSplitFlatMapFunction())
                .keyBy(wordCount -> wordCount.f0)
                .reduce(new ReduceFunction<Tuple2<String, Integer>>() {
                    @Override
                    public Tuple2<String, Integer> reduce(Tuple2<String, Integer> wordCount1, Tuple2<String, Integer> wordCount2) {
                        return new Tuple2<>(wordCount1.f0, wordCount1.f1 + wordCount2.f1);
                    }
                }).print();
    }

    public static void reduce2() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/word");
        stream.flatMap(new MyWordSplitFlatMapFunction())
                .keyBy(wordCount -> wordCount.f0)
                .reduce((wordCount1, wordCount2) -> new Tuple2<>(wordCount1.f0, wordCount1.f1 + wordCount2.f1)).print();
    }


    /**
     * 聚合操作
     */
    public static void aggregation() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.flatMap(new MyStudentFlatMapFunction())
                .keyBy(Student::getName)
                .min("age").print();
//                .minBy("age").print();
    }

    public static void aggregation2() {
        DataStreamSource<String> stream = sEnv.readTextFile("flink/src/main/resources/common/student");
        stream.flatMap(new MyStudentFlatMapFunction())
                .keyBy(Student::getName)
                .max("age").print();
//                .maxBy("age").print();
    }


    /**
     * 多流连接操作，将两条流连接起来对两条流做共同的操作，例如两条流都要做map->flatMap操作转化成某一格式，但具体的实现逻辑不同，
     * 这时就可以使用connect进行连接，然后对连接后的流进行map->flatMap操作，分别对两条流执行不同的逻辑转化成最终的格式
     * nc -lk 7777
     * flink    test
     * demo haha
     * <p>
     * nc -lk 8888
     * aaa,bbb
     */
    public static void connect() {
        DataStreamSource<String> stream1 = sEnv.socketTextStream("127.0.0.1", 7777);
        DataStreamSource<String> stream2 = sEnv.socketTextStream("127.0.0.1", 8888);
        stream1.connect(stream2).flatMap(new CoFlatMapFunction<String, String, String>() {
            @Override
            public void flatMap1(String value, Collector<String> out) throws Exception {
                String[] words = value.split("\t");
                for (String word : words) {
                    out.collect(word);
                }
            }

            @Override
            public void flatMap2(String value, Collector<String> out) throws Exception {
                String[] words = value.split(",");
                for (String word : words) {
                    out.collect(word);
                }
            }
        }).print();
    }

    /**
     * 多流合并操作，对两条流进行合并，但两条流的数据格式要保持一致
     */
    public static void union() {
        SingleOutputStreamOperator<Tuple2<String, Integer>> stream1 = sEnv.readTextFile("flink/src/main/resources/common/word")
                .flatMap(new MyWordSplitFlatMapFunction());
        SingleOutputStreamOperator<Tuple2<String, Integer>> stream2 = sEnv.socketTextStream("127.0.0.1", 7777)
                .flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (line, out) -> {
                    String[] words = line.split(",");
                    for (String word : words) {
                        out.collect(new Tuple2<>(word, 1));
                    }
                }).returns(Types.TUPLE(Types.STRING, Types.INT));
        stream1.union(stream2).print();
    }


    /**
     * 旁路输出，将流中的数据进行切分
     */
    public static void sideOutput() {
        DataStreamSource<Long> source = sEnv.fromSequence(1, 10);
        final OutputTag<Long> oddOutputTag = new OutputTag<Long>("odd") {
        };
        final OutputTag<Long> evenOutputTag = new OutputTag<Long>("even") {
        };
        SingleOutputStreamOperator<Long> process = source.process(new ProcessFunction<Long, Long>() {
            @Override
            public void processElement(Long value, Context ctx, Collector<Long> out) throws Exception {
                if (value % 2 == 0) {
                    ctx.output(evenOutputTag, value);
                } else {
                    ctx.output(oddOutputTag, value);
                }
            }
        });
        // 输出奇数
//        process.getSideOutput(oddOutputTag).print();
        // 输出偶数
        process.getSideOutput(evenOutputTag).print();
    }


    public static void main(String[] args) throws Exception {
        sEnv.setParallelism(1);
        sEnv.getConfig().setAutoWatermarkInterval(1);

        map();
        map2();
        map3();
        map4();
        flatMap();
        flatMap2();
        flatMap3();
        flatMap4();
        filter();
        filter2();
        keyBy();
        keyBy2();
        keyBy3();
        reduce();
        reduce2();
        aggregation();
        aggregation2();
        connect();
        union();
        sideOutput();

        sEnv.execute("flink demo job");
    }

}
