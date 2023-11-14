package com.myproject.sql.schema;

import com.myproject.sql.enums.ColumnDataTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableSchema {

    private final List<ColumnSchema> columnSchemaList;

    private final Map<String, ColumnSchema> columnNameMapping;

    private final Map<String, Integer> columnIndexMapping;

    public TableSchema() {
        this.columnSchemaList = new ArrayList<>();
        this.columnNameMapping = new HashMap<>();
        this.columnIndexMapping = new HashMap<>();
    }

    public TableSchema(List<ColumnSchema> columnSchemaList) {
        this.columnSchemaList = new ArrayList<>(columnSchemaList);
        this.columnNameMapping = new HashMap<>();
        this.columnIndexMapping = new HashMap<>();
        for (int i = 0; i < columnSchemaList.size(); i++) {
            ColumnSchema columnSchema = columnSchemaList.get(i);
            columnNameMapping.put(columnSchema.getColumnName(), columnSchema);
            columnIndexMapping.put(columnSchema.getColumnName(), i);
        }
    }

    public ColumnSchema addColumn(ColumnSchema columnSchema) {
        if (columnNameMapping.containsKey(columnSchema.getColumnName())) {
            throw new IllegalArgumentException("column name already exists");
        }
        columnNameMapping.put(columnSchema.getColumnName(), columnSchema);
        columnIndexMapping.put(columnSchema.getColumnName(), columnSchemaList.size());
        columnSchemaList.add(columnSchema);
        return columnSchema;
    }

    public ColumnSchema addColumn(String columnName, ColumnDataTypeEnum dataType) {
        ColumnSchema columnSchema = ColumnSchema.builder()
                .columnName(columnName)
                .columnType(dataType)
                .build();
        return addColumn(columnSchema);
    }

    public void dropColumn(String columnName) {
        if (columnNameMapping.containsKey(columnName)) {
            throw new IllegalArgumentException("column name not exist");
        }
        ColumnSchema columnSchema = columnNameMapping.get(columnName);
        columnSchemaList.remove(columnSchema);
        columnIndexMapping.remove(columnName);
        columnNameMapping.remove(columnName);
    }

    public List<String> columnList() {
        return columnSchemaList.stream().map(ColumnSchema::getColumnName).toList();
    }

    public ColumnSchema getColumn(String columnName) {
        return columnNameMapping.getOrDefault(columnName, null);
    }

    public int getColumnIndex(String columnName) {
        return columnIndexMapping.getOrDefault(columnName, -1);
    }

}
