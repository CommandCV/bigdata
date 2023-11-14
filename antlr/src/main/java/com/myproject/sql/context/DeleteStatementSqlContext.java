package com.myproject.sql.context;

import com.myproject.sql.enums.StatementTypeEnum;
import com.myproject.sql.model.ParameterBinding;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteStatementSqlContext extends AbstractSqlContext {

    private List<ParameterBinding> filter;

    @Override
    public StatementTypeEnum getStatementType() {
        return StatementTypeEnum.DELETE;
    }
}
