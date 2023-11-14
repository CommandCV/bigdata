package com.myproject.sql.context;

import com.myproject.sql.enums.StatementTypeEnum;
import com.myproject.sql.model.ColumnDefinition;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTableSqlContext extends AbstractSqlContext {

    private List<ColumnDefinition> columnDefinitions;

    @Override
    public StatementTypeEnum getStatementType() {
        return StatementTypeEnum.CREATE_TABLE;
    }

}
