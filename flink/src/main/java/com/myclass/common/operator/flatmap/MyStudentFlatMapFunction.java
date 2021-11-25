package com.myclass.common.operator.flatmap;

import com.myclass.common.entry.Student;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class MyStudentFlatMapFunction implements FlatMapFunction<String, Student> {

    @Override
    public void flatMap(String value, Collector<Student> out) {
        String[] data = value.split(",");
        out.collect(new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim()));
    }

}
