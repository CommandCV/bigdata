package com.myproject.calculate;

import java.util.Stack;

public class CalculatorListener extends CalculateBaseListener {

    private final Stack<Integer> stack = new Stack<>();

    public int getResult() {
        return stack.peek();
    }

    @Override
    public void exitTimesDiv(CalculateParser.TimesDivContext ctx) {
        int right = stack.pop();
        int left = stack.pop();
        if (ctx.op.getType() == CalculateParser.TIMES) {
            stack.push(left * right);
        } else {
            stack.push(left / right);
        }
    }

    @Override
    public void exitPlusMinus(CalculateParser.PlusMinusContext ctx) {
        int right = stack.pop();
        int left = stack.pop();
        if (ctx.op.getType() == CalculateParser.PLUS) {
            stack.push(left + right);
        } else {
            stack.push(left - right);
        }
    }

    @Override
    public void exitNumber(CalculateParser.NumberContext ctx) {
        int number = Integer.parseInt(ctx.NUMBER().getText());
        stack.push(number);
    }

}
