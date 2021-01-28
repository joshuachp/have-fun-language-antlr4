package org.example.interpreter;


import org.example.Utils;
import org.example.interpreter.antlr.ImpBaseVisitor;
import org.example.interpreter.antlr.ImpParser;
import org.example.interpreter.values.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Interpreter extends ImpBaseVisitor<AbstractReturnValue> {

    // Function map, indexed by the name of the function
    private final HashMap<String, Fun> functions;
    // Program output to file, used to test output
    private final BufferedWriter out;
    // Memory map, indexed by the name of the Variable
    private HashMap<String, AbstractValue<?>> memory;

    public Interpreter() {
        // Initialize all the proprieties
        memory = new HashMap<>();
        functions = new HashMap<>();
        try {
            this.out = Files.newBufferedWriter(
                    Paths.get("out.txt"),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Convert expression to Integer, panics if expression doesn't return an IntegerValue
    private Integer visitNatExp(ImpParser.ExpContext ctx) {
        Integer value = null;
        try {
            value = ((IntegerValue) visit(ctx)).getValue();
        } catch (ClassCastException e) {
            Utils.panic(ctx, "Type mismatch, natural expression expected.");
        }
        assert value != null;
        return value;
    }

    // Convert expression to Boolean, panics if expression doesn't return an BoolValue
    private Boolean visitBoolExp(ImpParser.ExpContext ctx) {
        Boolean value = null;
        try {
            value = ((BoolValue) visit(ctx)).getValue();
        } catch (ClassCastException e) {
            Utils.panic(ctx, "Type mismatch, boolean expression expected.");
        }
        assert value != null;
        return value;
    }

    @Override
    public VoidValue visitProg(ImpParser.ProgContext ctx) {
        for (ImpParser.FunContext fun : ctx.fun()) {
            visitFun(fun);
        }
        return (VoidValue) visit(ctx.com());
    }

    @Override
    public VoidValue visitIf(ImpParser.IfContext ctx) {
        return (VoidValue) (visitBoolExp(ctx.exp())
                ? visit(ctx.com(0))
                : visit(ctx.com(1)));
    }

    @Override
    public VoidValue visitAssign(ImpParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        AbstractValue<?> value = (AbstractValue<?>) visit(ctx.exp());
        memory.put(id, value);
        return new VoidValue();
    }

    @Override
    public VoidValue visitSkip(ImpParser.SkipContext ctx) {
        return new VoidValue();
    }

    @Override
    public VoidValue visitSeq(ImpParser.SeqContext ctx) {
        visit(ctx.com(0));
        return (VoidValue) visit(ctx.com(1));
    }

    @Override
    public VoidValue visitWhile(ImpParser.WhileContext ctx) {
        while (visitBoolExp(ctx.exp())) {
            visit(ctx.com());
        }
        return new VoidValue();
    }

    @Override
    public VoidValue visitOut(ImpParser.OutContext ctx) {
        String str = visit(ctx.exp()).toString();
        System.out.println(str);
        try {
            out.write(str);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new VoidValue();
    }

    @Override
    public IntegerValue visitNat(ImpParser.NatContext ctx) {
        return new IntegerValue(Integer.parseInt(ctx.NAT().getText()));
    }

    @Override
    public BoolValue visitBool(ImpParser.BoolContext ctx) {
        return new BoolValue(Boolean.parseBoolean(ctx.BOOL().getText()));
    }

    @Override
    public AbstractReturnValue visitParExp(ImpParser.ParExpContext ctx) {
        return visit(ctx.exp());
    }

    @Override
    public IntegerValue visitPow(ImpParser.PowContext ctx) {
        int base = visitNatExp(ctx.exp(0));
        int exp = visitNatExp(ctx.exp(1));

        return new IntegerValue((int) Math.pow(base, exp));
    }

    @Override
    public BoolValue visitNot(ImpParser.NotContext ctx) {
        return new BoolValue(!visitBoolExp(ctx.exp()));
    }

    @Override
    public IntegerValue visitDivMulMod(ImpParser.DivMulModContext ctx) {
        int left = visitNatExp(ctx.exp(0));
        int right = visitNatExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.DIV -> new IntegerValue(left / right);
            case ImpParser.MUL -> new IntegerValue(left * right);
            case ImpParser.MOD -> new IntegerValue(left % right);
            default -> throw new IllegalStateException("Unexpected value: " + ctx.op.getType());
        };
    }

    @Override
    public IntegerValue visitPlusMinus(ImpParser.PlusMinusContext ctx) {
        int left = visitNatExp(ctx.exp(0));
        int right = visitNatExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.PLUS -> new IntegerValue(left + right);
            case ImpParser.MINUS -> new IntegerValue(Math.max(left - right, 0));
            default -> throw new IllegalStateException("Unexpected value: " + ctx.op.getType());
        };
    }

    @Override
    public BoolValue visitEqExp(ImpParser.EqExpContext ctx) {
        AbstractValue<?> left = (AbstractValue<?>) visit(ctx.exp(0));
        AbstractValue<?> right = (AbstractValue<?>) visit(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.EQQ -> new BoolValue(left.equals(right));
            case ImpParser.NEQ -> new BoolValue(!left.equals(right));
            default -> throw new IllegalStateException("Unexpected value: " + ctx.op.getType());
        };
    }

    @Override
    public AbstractValue<?> visitId(ImpParser.IdContext ctx) {
        String id = ctx.ID().getText();

        if (!memory.containsKey(id)) {
            Utils.panic(ctx, "Variable " + id + " used but never instantiated");
        }

        return memory.get(id);
    }

    @Override
    public BoolValue visitCmpExp(ImpParser.CmpExpContext ctx) {
        int left = visitNatExp(ctx.exp(0));
        int right = visitNatExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.GEQ -> new BoolValue(left >= right);
            case ImpParser.LEQ -> new BoolValue(left <= right);
            case ImpParser.LT -> new BoolValue(left < right);
            case ImpParser.GT -> new BoolValue(left > right);
            default -> null;
        };
    }

    @Override
    public BoolValue visitLogicExp(ImpParser.LogicExpContext ctx) {
        boolean left = visitBoolExp(ctx.exp(0));
        boolean right = visitBoolExp(ctx.exp(1));

        return switch (ctx.op.getType()) {
            case ImpParser.AND -> new BoolValue(left && right);
            case ImpParser.OR -> new BoolValue(left || right);
            default -> null;
        };
    }

    @Override
    public VoidValue visitFun(ImpParser.FunContext ctx) {
        String name = ctx.ID(0).getText();
        if (functions.containsKey(name)) {
            Utils.panic(ctx, "Fun " + name + " already defined.");
        }
        functions.put(name, new Fun(ctx));
        return new VoidValue();
    }

    @Override
    public AbstractReturnValue visitCall(ImpParser.CallContext ctx) {
        // Get the name of the function
        String name = ctx.ID().getText();
        // Check if it was previously declared
        if (!functions.containsKey(name)) {
            Utils.panic(ctx, "Function " + name + " used but never declared");
        }

        Fun fun = functions.get(name);
        List<String> argsName = fun.getParameters();
        // Get the argument context to be evaluated into AbstractValues
        List<ImpParser.ExpContext> argsCtx = ctx.exp();

        // Check if we have the same number of arguments in the function and values provided
        if (argsCtx.size() != argsName.size()) {
            Utils.panic(ctx, "Function f called with the wrong number of arguments");
        }

        // Create a new memory map for the value of the arguments
        HashMap<String, AbstractValue<?>> newMemory = new HashMap<>();

        // We first evaluate the expressions in the arguments before changing to the function context (newMemory)
        List<AbstractValue<?>> args = argsCtx.stream()
                .map((exp) -> (AbstractValue<?>) visit(exp))
                .collect(Collectors.toList());
        for (int i = 0; i < args.size(); i++) {
            // Put the values in memory with the respective argument name
            newMemory.put(argsName.get(i), args.get(i));
        }

        // Then save the memory state before the call and swap to the function new memory state
        HashMap<String, AbstractValue<?>> snapshot = this.memory;
        this.memory = newMemory;

        // Visit the function body (if there is one) and get the return value
        if (fun.getBody() != null) {
            visit(fun.getBody());
        }
        AbstractValue<?> ret = (AbstractValue<?>) visit(fun.getRet());

        // Reset the memory to the state before the function call
        this.memory = snapshot;
        return ret;
    }
}
