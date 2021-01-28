package org.example.interpreter;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.example.Utils;
import org.example.interpreter.antlr.ImpParser;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Fun {

    // Set of the function arguments with respect for the order
    private final LinkedHashSet<String> parameters;
    // The body of the function which could be missing
    @Nullable
    private final ImpParser.ComContext body;
    // The expression for the function return value
    private final ImpParser.ExpContext ret;

    public Fun(ImpParser.FunContext fun) {
        // Get the list of ID form the context and exclude the first one since it's the function name.
        List<TerminalNode> ids = fun.ID();
        this.parameters = new LinkedHashSet<>();
        List<String> args = ids.subList(1, ids.size())
                .stream()
                .map(ParseTree::getText)
                .collect(Collectors.toList());
        // Insert the names of the parameters, panic if it's already present
        for (String arg : args) {
            if (!this.parameters.add(arg)) {
                Utils.panic(fun, "Parameter name " + arg + "clashes with previous parameters");
            }
        }
        this.body = fun.com();
        this.ret = fun.exp();
    }

    public ImpParser.@Nullable ComContext getBody() {
        return body;
    }

    public ImpParser.ExpContext getRet() {
        return ret;
    }

    public List<String> getParameters() {
        return new ArrayList<>(parameters);
    }

}
