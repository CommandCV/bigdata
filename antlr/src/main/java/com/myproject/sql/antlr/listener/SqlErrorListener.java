package com.myproject.sql.antlr.listener;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

@Slf4j
public class SqlErrorListener extends BaseErrorListener {

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        log.error("Syntax error, line: {}, position: {}, msg: {}", line, charPositionInLine, msg);
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e);
    }
}
