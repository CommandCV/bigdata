package com.myproject.sql.service.frontend;

import com.myproject.sql.catalog.DatabaseCatalog;
import com.myproject.sql.catalog.TableCatalog;
import com.myproject.sql.config.Configuration;
import com.myproject.sql.context.*;
import com.myproject.sql.enums.AlterTypeEnum;
import com.myproject.sql.enums.SelectTypeEnum;
import com.myproject.sql.enums.SortingTypeEnum;
import com.myproject.sql.enums.StorageTypeEnum;
import com.myproject.sql.model.ColumnDefinition;
import com.myproject.sql.model.ParameterBinding;
import com.myproject.sql.model.Sorting;
import com.myproject.sql.parser.SqlParser;
import com.myproject.sql.plan.LogicalPlan;
import com.myproject.sql.schema.ColumnSchema;
import com.myproject.sql.schema.TableSchema;
import com.myproject.sql.service.CatalogService;
import com.myproject.sql.service.FrontendService;
import com.myproject.sql.service.StorageService;
import com.myproject.sql.service.catalog.InternalCatalogService;
import com.myproject.sql.service.storage.FileSystemStorageService;
import com.myproject.sql.service.storage.InMemoryStorageService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class InternalFrontendService implements FrontendService {

    private final Configuration configuration;

    private final CatalogService catalogService;

    private final StorageService storageService;

    private InternalFrontendService() {
        this.configuration = new Configuration();
        this.catalogService = new InternalCatalogService(configuration);
        if (StorageTypeEnum.IN_MEMORY.equals(configuration.getStorageType())) {
            this.storageService = new InMemoryStorageService(configuration);
        } else {
            this.storageService = new FileSystemStorageService(configuration);
        }
    }

    public InternalFrontendService(CatalogService catalogService, StorageService storageService) {
        this.configuration = new Configuration();
        this.catalogService = catalogService;
        this.storageService = storageService;
    }

    public InternalFrontendService(Configuration configuration, CatalogService catalogService,
                                   StorageService storageService) {
        this.configuration = configuration;
        this.catalogService = catalogService;
        this.storageService = storageService;
    }

    @Override
    public void execute(String statement) {
        log.info("Execute statement: {}", statement);
        LogicalPlan logicalPlan = SqlParser.parser(statement);
        AbstractSqlContext sqlContext = (AbstractSqlContext) logicalPlan.getContext();
        String database = sqlContext.getDatabase();
        String table = sqlContext.getTable();
        switch (logicalPlan.getType()) {
            case CREATE_DATABASE -> {
                CreateDatabaseSqlContext context = (CreateDatabaseSqlContext) sqlContext;
                catalogService.createDatabase(database, new DatabaseCatalog());
                storageService.createDatabase(database);
            }
            case DROP_DATABASE -> {
                DropDatabaseSqlContext context = (DropDatabaseSqlContext) sqlContext;
                catalogService.dropDatabase(database);
                storageService.dropDatabase(database);
            }
            case CREATE_TABLE -> {
                CreateTableSqlContext context = (CreateTableSqlContext) sqlContext;
                List<ColumnSchema> columnSchemas = context.getColumnDefinitions().stream().map(columnDefinition ->
                        ColumnSchema.builder()
                                .columnName(columnDefinition.getColumnName())
                                .columnType(columnDefinition.getColumnDataType())
                                .build()
                ).toList();
                TableSchema tableSchema = new TableSchema(columnSchemas);
                TableCatalog tableCatalog = new TableCatalog(table, tableSchema);
                catalogService.createTable(database, tableCatalog);
                storageService.createTable(database, table);
            }
            case ALTER_TABLE -> {
                AlterTableSqlContext context = (AlterTableSqlContext) sqlContext;
                ColumnDefinition columnDefinition = context.getColumnDefinition();
                TableSchema tableSchema = catalogService.getTableSchema(database, table);
                if (AlterTypeEnum.ADD_COLUMN.equals(context.getType())) {
                    ColumnSchema columnSchema = ColumnSchema.builder()
                            .columnName(columnDefinition.getColumnName())
                            .columnType(columnDefinition.getColumnDataType())
                            .build();
                    catalogService.addColumn(database, table, columnSchema);
                    int columnIndex = tableSchema.getColumnIndex(columnDefinition.getColumnName());
                    Object defaultValue = columnDefinition.getColumnDataType().getDefaultValue();
                    storageService.addColumn(database, table, columnIndex, defaultValue);
                } else {
                    int columnIndex = tableSchema.getColumnIndex(columnDefinition.getColumnName());
                    catalogService.dropColumn(database, table, columnDefinition.getColumnName());
                    storageService.dropColumn(database, table, columnIndex);
                }
            }
            case DROP_TABLE -> {
                DropTableSqlContext context = (DropTableSqlContext) sqlContext;
                catalogService.dropTable(database, table);
                storageService.dropTable(database, table);
            }
            case QUERY -> {
                // ignore
            }
            case INSERT -> {
                InsertStatementSqlContext context = (InsertStatementSqlContext) sqlContext;
                List<List<Object>> rows = context.getRows();
                TableSchema tableSchema = catalogService.getTableSchema(database, table);
                List<String> columnList = tableSchema.columnList();
                if (columnList.size() != rows.get(0).size()) {
                    throw new IllegalArgumentException("column length is: " + columnList.size() + ", but found: "
                            + rows.size());
                }
                storageService.insert(database, table, rows);
            }
            case DELETE -> {
                DeleteStatementSqlContext context = (DeleteStatementSqlContext) sqlContext;
                List<ParameterBinding> filter = context.getFilter();
                if (filter != null) {
                    List<String> columnNames = filter.stream().map(ParameterBinding::getName).toList();
                    catalogService.validateColumnName(database, table, columnNames);
                    // build filter parameter
                    TableSchema tableSchema = catalogService.getTableSchema(database, table);
                    filter.forEach(parameter -> {
                        ColumnSchema columnSchema = tableSchema.getColumn(parameter.getName());
                        parameter.setType(columnSchema.getColumnType());
                        parameter.setColumnIndex(tableSchema.getColumnIndex(parameter.getName()));
                    });
                }
                storageService.delete(database, table, filter);
            }
        }
    }

    @Override
    public List<List<Object>> executeQuery(String statement) {
        log.info("Execute query: {}", statement);
        if (!SqlParser.isQueryStatement(statement)) {
            throw new IllegalArgumentException("The statement is not a query");
        }
        LogicalPlan logicalPlan = SqlParser.parser(statement);
        QueryStatementSqlContext context = (QueryStatementSqlContext) logicalPlan.getContext();
        String database = context.getDatabase();
        String table = context.getTable();
        List<String> select = context.getSelectColumns();
        if (SelectTypeEnum.ALL.equals(context.getSelectType())) {
            select = catalogService.getTableSchema(database, table).columnList();
        }
        List<ParameterBinding> filter = context.getFilter();
        String order = context.getOrder();
        SortingTypeEnum sortingType = context.getSorting();

        // validate column name
        Set<String> columnList = new HashSet<>(select);
        if (filter != null) {
            columnList.addAll(filter.stream().map(ParameterBinding::getName).toList());
        }
        if (order != null) {
            columnList.add(order);
        }
        catalogService.validateColumnName(database, table, columnList.stream().toList());

        // build query parameters
        TableSchema tableSchema = catalogService.getTableSchema(database, table);
        List<Integer> selectColumns = select.stream().map(tableSchema::getColumnIndex).toList();

        if (filter != null) {
            filter.forEach(parameter -> {
                ColumnSchema columnSchema = tableSchema.getColumn(parameter.getName());
                parameter.setType(columnSchema.getColumnType());
                parameter.setColumnIndex(tableSchema.getColumnIndex(parameter.getName()));
            });
        }
        Sorting sorting = null;
        if (order != null) {
            sorting = Sorting.builder()
                    .order(order)
                    .index(tableSchema.getColumnIndex(order))
                    .dataType(tableSchema.getColumn(order).getColumnType())
                    .sortingType(sortingType)
                    .build();
        }
        return storageService.query(database, table, selectColumns, filter, sorting);
    }

}
