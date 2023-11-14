package com.myproject.sql.context;

import com.myproject.sql.enums.SelectTypeEnum;
import com.myproject.sql.enums.SortingTypeEnum;
import com.myproject.sql.enums.StatementTypeEnum;
import com.myproject.sql.model.ParameterBinding;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryStatementSqlContext extends AbstractSqlContext {

    private SelectTypeEnum selectType;

    private List<String> selectColumns;

    private List<ParameterBinding> filter;

    private String order;

    private SortingTypeEnum sorting;

    @Override
    public StatementTypeEnum getStatementType() {
        return StatementTypeEnum.QUERY;
    }
}
