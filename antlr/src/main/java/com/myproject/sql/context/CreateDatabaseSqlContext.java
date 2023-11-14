package com.myproject.sql.context;

import com.myproject.sql.enums.StatementTypeEnum;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
public class CreateDatabaseSqlContext extends AbstractSqlContext {

    @Override
    public StatementTypeEnum getStatementType() {
        return StatementTypeEnum.CREATE_DATABASE;
    }
}
