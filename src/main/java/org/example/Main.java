package org.example;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.example.interpreter.Interpreter;
import org.example.interpreter.antlr.ImpLexer;
import org.example.interpreter.antlr.ImpParser;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        Path path = Paths.get(Main.class.getResource("../../program.txt").toURI());
        String program = Files.readString(path);
        execute(program);
    }

    public static void execute(String str) {
        CodePointCharStream stream = CharStreams.fromString(str);
        ImpLexer lexer = new ImpLexer(stream);
        CommonTokenStream token = new CommonTokenStream(lexer);
        ImpParser parser = new ImpParser(token);
        ParseTree tree = parser.prog();
        Interpreter interpreter = new Interpreter();
        interpreter.visit(tree);
    }
}