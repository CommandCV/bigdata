package com.myclass.common.operator.flatmap;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.util.Collector;

public class MyWordSplitWithTimestampFlatMapFunction implements FlatMapFunction<String, Tuple3<String, Long, Integer>> {

    @Override
    public void flatMap(String value, Collector<Tuple3<String, Long, Integer>> out) {
        if (StringUtils.isNotBlank(value)) {
            String[] line = value.split(",");
            String word = line[0];
            Long timestamp = Long.valueOf(line[1]);
            out.collect(new Tuple3<>(word, timestamp, 1));
        }
    }
}
