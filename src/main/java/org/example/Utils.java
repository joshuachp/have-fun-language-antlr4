package org.example;

import org.antlr.v4.runtime.ParserRuleContext;

public class Utils {

    public static void panic(ParserRuleContext ctx, String err) {
        System.err.println(err);
        System.err.println("@" + ctx.start.getLine() + ":" + ctx.start.getCharPositionInLine());
        /* Debug
        System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>");
        System.err.println(ctx.getText());
        System.err.println("<<<<<<<<<<<<<<<<<<<<<<<<");
         */

        throw new RuntimeException();
    }
}
