package com.myclass.connector.socket.source;

import com.myclass.common.utils.DateUtils;
import com.myclass.common.utils.JsonUtils;
import com.myclass.common.utils.TableUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.logical.LogicalTypeRoot;
import org.apache.flink.types.RowKind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

public class MySocketTableSource extends RichSourceFunction<RowData> {
    private String host;
    private int port;
    private String format;
    private String delimiter;
    private List<String> columnNames;
    private List<LogicalTypeRoot> columnTypes;

    private volatile static boolean isRunning = true;
    private Socket socket;

    public MySocketTableSource(String host, int port, String format, String delimiter, List<String> columnNames, List<LogicalTypeRoot> columnTypes) {
        this.host = host;
        this.port = port;
        this.format = format;
        this.delimiter = delimiter;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        socket = new Socket(host, port);
    }

    @Override
    public void run(SourceContext<RowData> ctx) throws Exception {
        BufferedReader reader = null;
        while (isRunning) {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    final GenericRowData rowData = new GenericRowData(RowKind.INSERT, columnNames.size());
                    String[] fields = line.split(delimiter);
                    if (StringUtils.isBlank(line) || fields.length < 2) {
                        continue;
                    }
                    try {
                        rowData.setField(0, DateUtils.getTimestampFromDateStr(fields[0], "yyyy-MM-dd HH:mm:ss"));
                        if (format.equals("csv")) {
                            String[] valueFields = fields[1].split(",");
                            TableUtils.setRowData(rowData, 1, valueFields, columnTypes);
                        } else if (format.equals("json")){
                            TableUtils.setRowData(rowData, 1, JsonUtils.readJson(fields[1]), columnNames, columnTypes);
                        } else {
                            throw new IllegalArgumentException("nonsupport format type: " + format);
                        }
                        ctx.collect(rowData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
