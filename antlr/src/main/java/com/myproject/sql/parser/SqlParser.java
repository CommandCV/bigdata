package com.myproject.sql.parser;

import com.myproject.sql.SqlLexer;
import com.myproject.sql.antlr.listener.SqlErrorListener;
import com.myproject.sql.antlr.visitor.SqlParserVisitor;
import com.myproject.sql.plan.LogicalPlan;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class SqlParser {

    public static LogicalPlan parser(String statement) {
        SqlLexer sqlLexer = new SqlLexer(CharStreams.fromString(statement));
        sqlLexer.removeErrorListeners();
        sqlLexer.addErrorListener(new SqlErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(sqlLexer);
        com.myproject.sql.SqlParser sqlParser = new com.myproject.sql.SqlParser(tokenStream);
        SqlParserVisitor sqlParserVisitor = new SqlParserVisitor();
        sqlParserVisitor.visit(sqlParser.sql());
        return sqlParserVisitor.getLogicalPlan();
    }

    public static boolean isQueryStatement(String statement) {
        SqlLexer sqlLexer = new SqlLexer(CharStreams.fromString(statement));
        sqlLexer.removeErrorListeners();
        sqlLexer.addErrorListener(new SqlErrorListener());
        CommonTokenStream tokenStream = new CommonTokenStream(sqlLexer);
        com.myproject.sql.SqlParser sqlParser = new com.myproject.sql.SqlParser(tokenStream);
        SqlParserVisitor sqlParserVisitor = new SqlParserVisitor();
        sqlParserVisitor.visit(sqlParser.queryStatement());
        LogicalPlan logicalPlan =  sqlParserVisitor.getLogicalPlan();
        return logicalPlan.getType().isQuery();
    }
}
