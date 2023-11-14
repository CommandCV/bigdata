package com.myproject.sql.enums;

public enum OperateTypeEnum {

    EQUALS, GREATER_THAN, LESS_THAN;

    public static OperateTypeEnum of(String operate) {
        if ("=".equalsIgnoreCase(operate)) {
            return EQUALS;
        } else if (">".equalsIgnoreCase(operate)) {
            return GREATER_THAN;
        } else if ("<".equalsIgnoreCase(operate)) {
            return LESS_THAN;
        }
        return null;
    }
}
