package com.myproject.sql.context;

import com.myproject.sql.enums.StatementTypeEnum;

public interface SqlContext {

    StatementTypeEnum getStatementType();

    SqlContext getContext();

}
