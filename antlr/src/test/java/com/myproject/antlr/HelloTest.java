package com.myproject.antlr;

import com.myproject.antlr.Hello.HelloLexer;
import com.myproject.antlr.Hello.HelloParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class HelloTest {

    public static void main(String[] args) {
        String str = "hello antlr";
        HelloLexer lexer = new HelloLexer(CharStreams.fromString(str));
        CommonTokenStream token = new CommonTokenStream(lexer);
        HelloParser parser = new HelloParser(token);
        ParseTree tree = parser.expression();
        System.out.println(tree.toStringTree(parser));
        System.out.println(tree.getText());
    }
}
