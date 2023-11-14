package com.myproject.sql.schema;

import com.myproject.sql.enums.ColumnDataTypeEnum;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnSchema {

    private String columnName;

    private ColumnDataTypeEnum columnType;

}
