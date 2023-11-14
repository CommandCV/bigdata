package com.myproject.sql.context;

import com.myproject.sql.enums.AlterTypeEnum;
import com.myproject.sql.enums.StatementTypeEnum;
import com.myproject.sql.model.ColumnDefinition;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlterTableSqlContext extends AbstractSqlContext {

    private AlterTypeEnum type;

    private ColumnDefinition columnDefinition;

    @Override
    public StatementTypeEnum getStatementType() {
        return StatementTypeEnum.ALTER_TABLE;
    }
}
