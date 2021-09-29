package com.myclass.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.flink.table.data.GenericRowData;
import org.apache.flink.table.data.binary.BinaryStringData;
import org.apache.flink.table.types.logical.LogicalTypeRoot;

import java.util.List;

public class TableUtils {

    public static void setRowData(GenericRowData row, int startIndex, String[] columns, List<LogicalTypeRoot> typeRootList) {
        for (int i = 0, j = typeRootList.size(); i < columns.length && i < j; i++) {
            setRowData(row, i + startIndex, columns[i], typeRootList.get(i + startIndex));
        }
    }

    public static void setRowData(GenericRowData row, int startIndex, JsonNode jsonNode, List<String> columnNames, List<LogicalTypeRoot> typeRootList) {
        for (int i = 0, j = typeRootList.size(); i < jsonNode.size() && i < j; i++) {
            setRowData(row, i + startIndex, JsonUtils.getValueFromJsonNode(jsonNode, columnNames.get(i + startIndex)), typeRootList.get(i + startIndex));
        }
    }

    /**
     * 设置row数据
     * 更多类型对应关系 @see {@link org.apache.flink.table.data.RowData}
     * @param row row实例
     * @param index 在row中的位置
     * @param value 值
     * @param typeRoot 逻辑类型
     */
    public static void setRowData(GenericRowData row, int index, String value, LogicalTypeRoot typeRoot) {
        switch (typeRoot) {
            case INTEGER:
                row.setField(index, Integer.parseInt(value));
                break;
            case BIGINT:
                row.setField(index, Long.parseLong(value));
                break;
            case FLOAT:
                row.setField(index, Float.parseFloat(value));
                break;
            case DOUBLE:
                row.setField(index, Double.parseDouble(value));
                break;
            case CHAR:
            case VARCHAR:
                row.setField(index, new BinaryStringData(value));
                break;
            case BOOLEAN:
                row.setField(index, Boolean.parseBoolean(value));
        }
    }

}
