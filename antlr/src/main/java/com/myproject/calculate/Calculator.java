package com.myproject.calculate;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Calculator {

    public static long calculate(String expression) {
        CalculateLexer lexer = new CalculateLexer(CharStreams.fromString(expression));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        CalculateParser parser = new CalculateParser(tokenStream);
        CalculatorListener calculatorListener = new CalculatorListener();
        parser.addParseListener(calculatorListener);
        parser.expr();
        return calculatorListener.getResult();
    }

    public static void main(String[] args) {
        System.out.println(calculate("1 + 2 * 3"));
        System.out.println(calculate("1 * 2 * 3"));
        System.out.println(calculate("1 * 2 / 2"));
        System.out.println(calculate("4 / 2 + 1"));
        System.out.println(calculate("2 - 1"));
    }

}
