package com.myproject.sql.service.catalog;

import com.myproject.sql.catalog.Catalog;
import com.myproject.sql.catalog.DatabaseCatalog;
import com.myproject.sql.catalog.TableCatalog;
import com.myproject.sql.config.Configuration;
import com.myproject.sql.schema.ColumnSchema;
import com.myproject.sql.schema.TableSchema;
import com.myproject.sql.service.CatalogService;

import java.util.List;

public class InternalCatalogService implements CatalogService {

    private final Configuration configuration;

    private final Catalog catalog;

    public InternalCatalogService() {
        this.configuration = new Configuration();
        this.catalog = new Catalog();
    }

    public InternalCatalogService(Configuration configuration) {
        this.configuration = configuration;
        this.catalog = new Catalog();
    }

    @Override
    public void createDatabase(String database, DatabaseCatalog databaseCatalog) {
        this.catalog.createDatabase(database, databaseCatalog);
    }

    @Override
    public void dropDatabase(String database) {
        this.catalog.dropDatabase(database);
    }

    @Override
    public List<String> listDatabase() {
        return this.catalog.listDatabase();
    }

    @Override
    public boolean databaseExists(String database) {
        return this.catalog.databaseExists(database);
    }

    @Override
    public void createTable(String database, TableCatalog tableCatalog) {
        this.catalog.createTable(database, tableCatalog);
    }

    @Override
    public void dropTable(String database, String table) {
        this.catalog.dropTable(database, table);
    }

    @Override
    public List<String> listTable(String database) {
        return this.catalog.listTable(database);
    }

    @Override
    public boolean tableExists(String database, String table) {
        return this.catalog.tableExists(database, table);
    }

    @Override
    public void addColumn(String database, String table, ColumnSchema columnSchema) {
        this.catalog.addColumn(database, table, columnSchema);
    }

    @Override
    public void dropColumn(String database, String table, String column) {
        this.catalog.dropColumn(database, table, column);
    }

    @Override
    public TableSchema getTableSchema(String database, String table) {
        return this.catalog.getTableSchema(database, table);
    }

    @Override
    public void validateColumnName(String database, String table, List<String> columns) {
        this.catalog.validateColumnName(database, table, columns);
    }
}
