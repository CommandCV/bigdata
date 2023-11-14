package com.myproject.sql.service;

import com.myproject.sql.catalog.DatabaseCatalog;
import com.myproject.sql.catalog.TableCatalog;
import com.myproject.sql.schema.ColumnSchema;
import com.myproject.sql.schema.TableSchema;

import java.util.List;

public interface CatalogService {

    void createDatabase(String database, DatabaseCatalog databaseCatalog);

    void dropDatabase(String database);

    List<String> listDatabase();

    boolean databaseExists(String database);

    void createTable(String database, TableCatalog tableCatalog);

    void dropTable(String database, String table);

    List<String> listTable(String database);

    boolean tableExists(String database, String table);

    void addColumn(String database, String table, ColumnSchema columnSchema);

    void dropColumn(String database, String table, String column);

    TableSchema getTableSchema(String database, String table);

    void validateColumnName(String database, String table, List<String> columns);

}
