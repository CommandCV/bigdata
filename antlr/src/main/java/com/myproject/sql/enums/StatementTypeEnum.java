package com.myproject.sql.enums;

public enum StatementTypeEnum {

    CREATE_DATABASE, DROP_DATABASE, CREATE_TABLE, ALTER_TABLE, DROP_TABLE, INSERT, DELETE, QUERY;

    public boolean isQuery() {
        return this == QUERY;
    }
}
