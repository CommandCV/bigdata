package com.myproject.sql.plan;

import com.myproject.sql.context.SqlContext;
import com.myproject.sql.enums.StatementTypeEnum;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogicalPlan {

    private StatementTypeEnum type;

    private SqlContext context;

}
