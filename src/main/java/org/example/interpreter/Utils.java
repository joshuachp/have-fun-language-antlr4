package org.example.interpreter;

import org.antlr.v4.runtime.ParserRuleContext;

public class Utils {


    public static void panic(ParserRuleContext ctx, String err) {
        System.err.println(err);
        System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
        /* Debug context
        System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>");
        System.err.println(ctx.getText());
        System.err.println("<<<<<<<<<<<<<<<<<<<<<<<<");
        */
        System.exit(1);
    }
}
