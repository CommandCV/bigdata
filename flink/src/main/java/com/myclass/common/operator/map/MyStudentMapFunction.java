package com.myclass.common.operator.map;

import com.myclass.common.entry.Student;
import org.apache.flink.api.common.functions.MapFunction;

public class MyStudentMapFunction implements MapFunction<String, Student> {

    @Override
    public Student map(String value) {
        String[] data = value.split(",");
        return new Student(null, data[0].trim(), data[1].trim(), Integer.valueOf(data[2].trim()), data[3].trim());
    }

}