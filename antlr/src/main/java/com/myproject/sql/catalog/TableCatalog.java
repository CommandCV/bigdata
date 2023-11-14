package com.myproject.sql.catalog;

import com.myproject.sql.schema.TableSchema;

public class TableCatalog {

    private String tableName;

    private TableSchema schema;

    public TableCatalog() {
    }

    public TableCatalog(String tableName, TableSchema schema) {
        this.tableName = tableName;
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public TableSchema getSchema() {
        return schema;
    }

}
