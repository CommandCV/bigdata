package com.myproject.sql.context;

import com.myproject.sql.enums.StatementTypeEnum;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class DropTableSqlContext extends AbstractSqlContext {

    @Override
    public StatementTypeEnum getStatementType() {
        return StatementTypeEnum.DROP_TABLE;
    }
}
