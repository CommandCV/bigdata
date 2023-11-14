package com.myproject.sql.context;

import com.myproject.sql.enums.StatementTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractSqlContext implements SqlContext {

    private String database;

    private String table;

    @Override
    public abstract StatementTypeEnum getStatementType();

    @Override
    public SqlContext getContext() {
        return this;
    }
}
