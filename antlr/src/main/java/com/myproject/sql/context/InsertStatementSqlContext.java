package com.myproject.sql.context;

import com.myproject.sql.enums.StatementTypeEnum;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsertStatementSqlContext extends AbstractSqlContext {

    private List<List<Object>> rows;

    @Override
    public StatementTypeEnum getStatementType() {
        return StatementTypeEnum.INSERT;
    }
}
