package com.myproject.sql.catalog;

import com.myproject.sql.model.ParameterBinding;
import com.myproject.sql.schema.ColumnSchema;
import com.myproject.sql.schema.TableSchema;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Catalog {

    private static final ConcurrentHashMap<String, DatabaseCatalog> catalog = new ConcurrentHashMap<>();

    public void createDatabase(String database, DatabaseCatalog databaseCatalog) {
        if (databaseExists(database)) {
            throw new IllegalArgumentException("database already exists");
        }
        catalog.put(database, databaseCatalog);
    }

    public void dropDatabase(String database) {
        validateDatabase(database);
        catalog.remove(database);
    }

    public List<String> listDatabase() {
        return catalog.keySet().stream().toList();
    }

    public boolean databaseExists(String database) {
        return catalog.containsKey(database);
    }

    public void createTable(String database, TableCatalog tableCatalog) {
        validateDatabase(database);
        if (catalog.get(database).tableExists(tableCatalog.getTableName())) {
            throw new IllegalArgumentException("table already exists");
        } else {
            catalog.get(database).createTable(tableCatalog.getTableName(), tableCatalog);
        }
    }

    public void dropTable(String database, String table) {
        validateTable(database, table);
        catalog.get(database).dropTable(table);
    }

    public List<String> listTable(String database) {
        validateDatabase(database);
        return catalog.get(database).listTable();
    }

    public boolean tableExists(String database, String table) {
        validateDatabase(database);
        return catalog.get(database).tableExists(table);
    }

    public void addColumn(String database, String table, ColumnSchema columnSchema) {
        validateTable(database, table);
        TableCatalog tableCatalog = catalog.get(database).getTable(table);
        tableCatalog.getSchema().addColumn(columnSchema);
    }

    public void dropColumn(String database, String table, String column) {
        validateTable(database, table);
        TableCatalog tableCatalog = catalog.get(database).getTable(table);
        tableCatalog.getSchema().dropColumn(column);
    }

    public TableSchema getTableSchema(String database, String table) {
        validateTable(database, table);
        TableCatalog tableCatalog = catalog.get(database).getTable(table);
        return tableCatalog.getSchema();
    }

    public void validateDatabase(String database) {
        if (!catalog.containsKey(database)) {
            throw new IllegalArgumentException("database not exists");
        }
    }

    public void validateTable(String database, String table) {
        validateDatabase(database);
        if (!catalog.get(database).tableExists(table)) {
            throw new IllegalArgumentException("table not exists");
        }
    }

    public void validateColumnName(String database, String table, String column) {
        validateTable(database, table);
        if (catalog.get(database).getTable(table).getSchema().getColumn(column) == null) {
            throw new IllegalArgumentException("column not exists");
        }
    }

    public void validateColumnName(String database, String table, List<String> columns) {
        validateTable(database, table);
        List<String> columnList = catalog.get(database).getTable(table).getSchema().columnList();
        for (String column : columns) {
            if (!columnList.contains(column)) {
                throw new IllegalArgumentException("column not exists: " + column);
            }
        }
    }

}
