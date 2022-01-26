package com.myclass.connector.socket.source;

import com.myclass.common.config.SocketConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.connector.source.SourceFunctionProvider;
import org.apache.flink.table.types.logical.LogicalTypeRoot;
import org.apache.flink.types.RowKind;

import java.util.List;
import java.util.stream.Collectors;

public class SocketDynamicSource implements ScanTableSource {

    private final ReadableConfig config;
    private final List<Column> tableColumns;
    private final String host;
    private final int port;
    private final String format;
    private final String delimiter;

    public SocketDynamicSource(ReadableConfig config, List<Column> tableColumns) {
        this.config = config;
        this.tableColumns = tableColumns;
        this.host = config.get(SocketConfigOption.host);
        this.port = config.get(SocketConfigOption.port);
        this.format = config.get(SocketConfigOption.format);
        this.delimiter = config.get(SocketConfigOption.delimiter);

    }

    @Override
    public ChangelogMode getChangelogMode() {
        return ChangelogMode.newBuilder().addContainedKind(RowKind.INSERT).build();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext scanContext) {
        List<String> columnNames = tableColumns.stream().map(Column::getName).collect(Collectors.toList());
        List<LogicalTypeRoot> columnTypes = tableColumns.stream().map(column -> column.getDataType().getLogicalType().getTypeRoot()).collect(Collectors.toList());
        return SourceFunctionProvider.of(new MySocketTableSource(host, port, format, delimiter, columnNames, columnTypes), false);
    }

    @Override
    public DynamicTableSource copy() {
        return new SocketDynamicSource(config, tableColumns);
    }

    @Override
    public String asSummaryString() {
        return "my-socket";
    }
}
