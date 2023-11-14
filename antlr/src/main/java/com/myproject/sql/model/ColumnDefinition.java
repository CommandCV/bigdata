package com.myproject.sql.model;

import com.myproject.sql.enums.ColumnDataTypeEnum;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDefinition {

    private String columnName;

    private ColumnDataTypeEnum columnDataType;

}
