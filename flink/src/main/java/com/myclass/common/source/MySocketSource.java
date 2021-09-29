package com.myclass.common.source;

import com.myclass.common.entry.Student;
import com.myclass.common.utils.DateUtils;
import com.myclass.common.utils.JsonUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

public class MySocketSource extends RichSourceFunction<Tuple2<Long, Student>> {
    private String host;
    private int port;
    private String format;
    private String delimiter;

    private volatile static boolean isRunning = true;
    private Socket socket;

    public MySocketSource(String host, int port, String format, String delimiter) {
        this.host = host;
        this.port = port;
        this.format = format;
        this.delimiter = delimiter;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        socket = new Socket(host, port);
    }

    @Override
    public void run(SourceContext<Tuple2<Long, Student>> ctx) throws Exception {
        BufferedReader reader = null;
        while (isRunning) {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.split(delimiter);
                    Long timestamp = DateUtils.getTimestampFromDateStr(fields[0], "yyyy-MM-dd HH:mm:ss");
                    Student student;
                    if (format.equals("csv")) {
                        String[] valueFields = fields[1].split("\\|");
                        student = new Student(Long.parseLong(valueFields[0]), valueFields[0], valueFields[0], Integer.parseInt(valueFields[0]), valueFields[0]);
                    } else if (format.equals("json")){
                        student = JsonUtils.parseJson(fields[1], Student.class);
                    } else {
                        throw new IllegalArgumentException("nonsupport format type: " + format);
                    }
                    ctx.collect(new Tuple2<>(timestamp, student));
                    reader.reset();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (Objects.nonNull(reader)) {
                    reader.close();
                }
            }
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
