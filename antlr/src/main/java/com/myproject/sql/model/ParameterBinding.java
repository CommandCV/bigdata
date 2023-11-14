package com.myproject.sql.model;

import com.myproject.sql.enums.ColumnDataTypeEnum;
import com.myproject.sql.enums.OperateTypeEnum;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterBinding {

    private String name;

    private Object value;

    private OperateTypeEnum operate;

    private ColumnDataTypeEnum type;

    private int columnIndex;

}
