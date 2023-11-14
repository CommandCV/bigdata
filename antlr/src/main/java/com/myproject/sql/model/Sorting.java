package com.myproject.sql.model;

import com.myproject.sql.enums.ColumnDataTypeEnum;
import com.myproject.sql.enums.SortingTypeEnum;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sorting {

    private String order;

    private int index;

    private ColumnDataTypeEnum dataType;

    private SortingTypeEnum sortingType;

}
