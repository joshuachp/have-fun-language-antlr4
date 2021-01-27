package org.example.interpreter;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.interpreter.antlr.ImpParser;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Fun {
    private final LinkedHashSet<String> parameters;
    private final ImpParser.ComContext body;
    private final ImpParser.ExpContext ret;

    public Fun(ImpParser.FunContext fun) {
        List<TerminalNode> ids = fun.ID();
        this.parameters = new LinkedHashSet<>();
        List<String> args = ids.subList(1, ids.size())
                .stream()
                .map(ParseTree::getText)
                .collect(Collectors.toList());
        for (String arg : args) {
            // Panics if parameter is already present
            if (!this.parameters.add(arg)) {
                Utils.panic(fun, "Parameter name " + arg + "clashes with previous parameters");
            }
        }
        this.body = fun.com();
        this.ret = fun.exp();
    }

    public ImpParser.ComContext getBody() {
        return body;
    }

    public ImpParser.ExpContext getRet() {
        return ret;
    }

    public List<String> getParameters() {
        return new ArrayList<>(parameters);
    }

}
