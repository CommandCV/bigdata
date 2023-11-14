package com.myproject.sql.enums;

import lombok.Getter;

@Getter
public enum ColumnDataTypeEnum {

    INT(0),

    BIGINT(0),

    CHAR(null),

    VARCHAR(null);

    private final Object defaultValue;

    ColumnDataTypeEnum(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isNumber() {
        return this == INT || this == BIGINT;
    }

    public boolean isString() {
        return this == CHAR || this == VARCHAR;
    }

}
