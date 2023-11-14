package com.myproject.sql.antlr.visitor;

import com.myproject.sql.SqlBaseVisitor;
import com.myproject.sql.SqlParser;
import com.myproject.sql.context.*;
import com.myproject.sql.enums.*;
import com.myproject.sql.model.ColumnDefinition;
import com.myproject.sql.model.ParameterBinding;
import com.myproject.sql.plan.LogicalPlan;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SqlParserVisitor extends SqlBaseVisitor<LogicalPlan> {

    private final LogicalPlan logicalPlan = new LogicalPlan();

    private SqlContext sqlContext;

    @Override
    public LogicalPlan visitCreateDatabaseStatement(SqlParser.CreateDatabaseStatementContext ctx) {
        String database = ctx.databaseName().getText();
        CreateDatabaseSqlContext createDatabaseSqlContext = new CreateDatabaseSqlContext();
        createDatabaseSqlContext.setDatabase(database);
        this.sqlContext = createDatabaseSqlContext;
        logicalPlan.setType(StatementTypeEnum.CREATE_DATABASE);
        logicalPlan.setContext(sqlContext);
        return logicalPlan;

    }

    @Override
    public LogicalPlan visitDropDatabaseStatement(SqlParser.DropDatabaseStatementContext ctx) {
        String database = ctx.databaseName().getText();
        DropDatabaseSqlContext dropDatabaseSqlContext = new DropDatabaseSqlContext();
        dropDatabaseSqlContext.setDatabase(database);
        this.sqlContext = dropDatabaseSqlContext;
        logicalPlan.setType(StatementTypeEnum.DROP_DATABASE);
        logicalPlan.setContext(sqlContext);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitCreateTableStatement(SqlParser.CreateTableStatementContext ctx) {
        String database = ctx.databaseName().getText();
        String table = ctx.tableName().getText();
        CreateTableSqlContext createTableSqlContext = new CreateTableSqlContext();
        createTableSqlContext.setDatabase(database);
        createTableSqlContext.setTable(table);
        this.sqlContext = createTableSqlContext;
        logicalPlan.setType(StatementTypeEnum.CREATE_TABLE);
        logicalPlan.setContext(sqlContext);
        this.visitColumnDefinitions(ctx.columnDefinitions());
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitAlterTableStatement(SqlParser.AlterTableStatementContext ctx) {
        String database = ctx.databaseName().getText();
        String table = ctx.tableName().getText();
        AlterTableSqlContext alterTableSqlContext = new AlterTableSqlContext();
        alterTableSqlContext.setDatabase(database);
        alterTableSqlContext.setTable(table);
        this.sqlContext = alterTableSqlContext;
        logicalPlan.setType(StatementTypeEnum.ALTER_TABLE);
        logicalPlan.setContext(sqlContext);
        if (ctx.alterOperator() instanceof SqlParser.AddColumnContext addColumnContext) {
            this.visitAddColumn(addColumnContext);
        } else {
            this.visitDropColumn((SqlParser.DropColumnContext) ctx.alterOperator());
        }
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitDropTableStatement(SqlParser.DropTableStatementContext ctx) {
        String database = ctx.databaseName().getText();
        String table = ctx.tableName().getText();
        DropTableSqlContext dropTableSqlContext = new DropTableSqlContext();
        dropTableSqlContext.setDatabase(database);
        dropTableSqlContext.setTable(table);
        this.sqlContext = dropTableSqlContext;
        logicalPlan.setType(StatementTypeEnum.DROP_TABLE);
        logicalPlan.setContext(sqlContext);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitQueryStatement(SqlParser.QueryStatementContext ctx) {
        String database = ctx.databaseName().getText();
        String table = ctx.tableName().getText();
        QueryStatementSqlContext queryStatementSqlContext = new QueryStatementSqlContext();
        queryStatementSqlContext.setDatabase(database);
        queryStatementSqlContext.setTable(table);
        this.sqlContext = queryStatementSqlContext;
        logicalPlan.setType(StatementTypeEnum.QUERY);
        logicalPlan.setContext(sqlContext);
        if (ctx.selectColumn() instanceof SqlParser.SelectAllContext selectAllContext) {
            this.visitSelectAll(selectAllContext);
        } else {
            this.visitSelectCol((SqlParser.SelectColContext) ctx.selectColumn());
        }
        if (ctx.filter() != null) {
            this.visitFilter(ctx.filter());
        }
        if (ctx.order() != null) {
            this.visitOrder(ctx.order());
        }
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitInsertStatement(SqlParser.InsertStatementContext ctx) {
        String database = ctx.databaseName().getText();
        String table = ctx.tableName().getText();
        InsertStatementSqlContext insertStatementSqlContext = new InsertStatementSqlContext();
        insertStatementSqlContext.setDatabase(database);
        insertStatementSqlContext.setTable(table);
        this.sqlContext = insertStatementSqlContext;
        logicalPlan.setType(StatementTypeEnum.INSERT);
        logicalPlan.setContext(sqlContext);
        this.visitRows(ctx.rows());
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitDeleteStatement(SqlParser.DeleteStatementContext ctx) {
        String database = ctx.databaseName().getText();
        String table = ctx.tableName().getText();
        DeleteStatementSqlContext deleteStatementSqlContext = new DeleteStatementSqlContext();
        deleteStatementSqlContext.setDatabase(database);
        deleteStatementSqlContext.setTable(table);
        this.sqlContext = deleteStatementSqlContext;
        logicalPlan.setType(StatementTypeEnum.DELETE);
        logicalPlan.setContext(sqlContext);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitDatabaseName(SqlParser.DatabaseNameContext ctx) {
        AbstractSqlContext context = (AbstractSqlContext) this.sqlContext;
        String database = ctx.getText();
        System.out.println(database);
        log.info("Visit database name: {}", database);
        context.setDatabase(database);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitTableName(SqlParser.TableNameContext ctx) {
        AbstractSqlContext context = (AbstractSqlContext) this.sqlContext;
        String table = ctx.getText();
        System.out.println(table);
        log.info("Visit table name: {}", table);
        context.setTable(table);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitColumnDefinitions(SqlParser.ColumnDefinitionsContext ctx) {
        CreateTableSqlContext context = (CreateTableSqlContext) this.sqlContext;
        List<SqlParser.ColumnDefinitionContext> columnDefinitionContexts = ctx.columnDefinition();
        List<ColumnDefinition> columnDefinitions = new ArrayList<>();
        columnDefinitionContexts.forEach(columnDefinitionContext -> {
            String columnName = columnDefinitionContext.columnName().getText();
            SqlParser.DataTypeContext dataTypeContext = columnDefinitionContext.dataType();
            ColumnDataTypeEnum columnDataType;
            if (dataTypeContext instanceof SqlParser.IntTypeContext) {
                columnDataType = ColumnDataTypeEnum.INT;
            } else if (dataTypeContext instanceof SqlParser.BigintTypeContext) {
                columnDataType = ColumnDataTypeEnum.BIGINT;
            } else if (dataTypeContext instanceof SqlParser.CharTypeContext) {
                columnDataType = ColumnDataTypeEnum.CHAR;
            } else {
                columnDataType = ColumnDataTypeEnum.VARCHAR;
            }
            ColumnDefinition columnDefinition = ColumnDefinition.builder()
                    .columnName(columnName)
                    .columnDataType(columnDataType)
                    .build();
            columnDefinitions.add(columnDefinition);
        });
        context.setColumnDefinitions(columnDefinitions);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitAddColumn(SqlParser.AddColumnContext ctx) {
        AlterTableSqlContext context = (AlterTableSqlContext) this.sqlContext;
        context.setType(AlterTypeEnum.ADD_COLUMN);
        String columnName = ctx.columnDefinition().columnName().getText();
        ColumnDataTypeEnum columnDataType = ColumnDataTypeEnum.valueOf(ctx.columnDefinition().dataType().getText().toUpperCase());
        ColumnDefinition columnDefinition = ColumnDefinition.builder()
                .columnName(columnName)
                .columnDataType(columnDataType)
                .build();
        context.setColumnDefinition(columnDefinition);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitDropColumn(SqlParser.DropColumnContext ctx) {
        AlterTableSqlContext context = (AlterTableSqlContext) this.sqlContext;
        context.setType(AlterTypeEnum.DROP_COLUMN);
        String columnName = ctx.columnName().getText();
        ColumnDefinition columnDefinition = ColumnDefinition.builder()
                .columnName(columnName)
                .build();
        context.setColumnDefinition(columnDefinition);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitSelectAll(SqlParser.SelectAllContext ctx) {
        QueryStatementSqlContext context = (QueryStatementSqlContext) this.sqlContext;
        context.setSelectType(SelectTypeEnum.ALL);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitSelectCol(SqlParser.SelectColContext ctx) {
        QueryStatementSqlContext context = (QueryStatementSqlContext) this.sqlContext;
        context.setSelectType(SelectTypeEnum.SELECT);
        List<SqlParser.ColumnNameContext> columnNameContexts = ctx.columnName();
        List<String> selectColumns = new ArrayList<>();
        columnNameContexts.forEach(columnNameContext -> selectColumns.add(columnNameContext.getText()));
        context.setSelectColumns(selectColumns);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitFilter(SqlParser.FilterContext ctx) {
        QueryStatementSqlContext context = (QueryStatementSqlContext) this.sqlContext;
        context.setFilter(this.buildParameterBindings(ctx.filterCondition()));
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitOrder(SqlParser.OrderContext ctx) {
        QueryStatementSqlContext context = (QueryStatementSqlContext) this.sqlContext;
        String columnName = ctx.columnName().getText();
        context.setOrder(columnName);
        this.visitSorting(ctx.sorting());
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitSorting(SqlParser.SortingContext ctx) {
        QueryStatementSqlContext context = (QueryStatementSqlContext) this.sqlContext;
        SortingTypeEnum sortingType = SortingTypeEnum.valueOf(ctx.getText().toUpperCase());
        context.setSorting(sortingType);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitRows(SqlParser.RowsContext ctx) {
        InsertStatementSqlContext context = (InsertStatementSqlContext) this.sqlContext;
        List<SqlParser.RowContext> rowContexts = ctx.row();
        List<List<Object>> rows = new ArrayList<>();
        rowContexts.forEach(rowContext -> {
            List<Object> row = new ArrayList<>();
            rowContext.value().forEach(valueContext -> {
                if (valueContext instanceof SqlParser.NumberValueContext numberValueContext) {
                    row.add(Long.valueOf(numberValueContext.getText()));
                } else {
                    row.add(String.valueOf(valueContext.getText()));
                }
            });
            rows.add(row);
        });
        context.setRows(rows);
        return logicalPlan;
    }

    @Override
    public LogicalPlan visitMatch(SqlParser.MatchContext ctx) {
        DeleteStatementSqlContext context = (DeleteStatementSqlContext) this.sqlContext;
        context.setFilter(this.buildParameterBindings(ctx.filterCondition()));
        return logicalPlan;
    }

    public LogicalPlan getLogicalPlan() {
        return logicalPlan;
    }

    private List<ParameterBinding> buildParameterBindings(List<SqlParser.FilterConditionContext> filterConditionContexts) {
        List<ParameterBinding> parameterBindings = new ArrayList<>();
        filterConditionContexts.forEach(filterConditionContext -> {
            String columnName = filterConditionContext.columnName().getText();
            OperateTypeEnum operateType = OperateTypeEnum.of(filterConditionContext.op.getText());
            String value = filterConditionContext.value().getText();
            ParameterBinding parameterBinding = ParameterBinding.builder()
                    .name(columnName)
                    .operate(operateType)
                    .value(value)
                    .build();
            parameterBindings.add(parameterBinding);
        });
        return parameterBindings;
    }
}
