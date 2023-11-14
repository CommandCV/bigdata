package com.myproject.sql.catalog;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DatabaseCatalog {

    private final ConcurrentHashMap<String, TableCatalog> databaseCatalog;

    public DatabaseCatalog() {
        this.databaseCatalog = new ConcurrentHashMap<>();
    }

    public DatabaseCatalog(ConcurrentHashMap<String, TableCatalog> databaseCatalog) {
        this.databaseCatalog = databaseCatalog;
    }

    public void createTable(String name, TableCatalog tableCatalog) {
        this.databaseCatalog.put(name, tableCatalog);
    }

    public void dropTable(String name) {
        if (name != null) {
            databaseCatalog.remove(name);
        }
    }

    public void alterTable(String name, TableCatalog newTableCatalog) {
        this.databaseCatalog.put(name, newTableCatalog);
    }

    public List<String> listTable() {
        return databaseCatalog.keySet().stream().toList();
    }

    public TableCatalog getTable(String name) {
        return databaseCatalog.get(name);
    }

    public boolean tableExists(String name) {
        return databaseCatalog.containsKey(name);
    }

}
