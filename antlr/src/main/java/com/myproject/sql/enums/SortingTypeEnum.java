package com.myproject.sql.enums;

public enum SortingTypeEnum {

    ASC, DESC;

    public boolean isAsc() {
        return this == ASC;
    }

}
