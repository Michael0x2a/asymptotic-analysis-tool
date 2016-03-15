package main;

import bigo.BigOVisitor;
import bigo.EquationDisplayPrinter;
import bigo.EquationSimplifier;
import bigo.SimpleGrammarPrettyPrint;
import math.MathExpression;
import org.antlr.v4.runtime.*;

import java.io.File;
import java.lang.System;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import grammar.*;
import simplegrammar.AstNode;
import visitors.PrettyPrintVisitor;
import visitors.SimplifierVisitor;
import wolfram.WolframQuery;

public class Main {
    public static void main(String[] args) {
        List<String> inputFiles = new ArrayList<>();
        try {
            if (args.length > 0) {
                // for each directory/file specified on the command line
                for (int i = 0; i < args.length; i++) {
                    inputFiles.add(args[i]);
                }
                List<String> javaFiles = new ArrayList<>();
                for (String fileName : inputFiles) {
                    List<String> files = getFilenames(new File(fileName));
                    javaFiles.addAll(files);
                }
                doFiles(javaFiles);
            } else {
                System.err.println("Usage: java Main <directory or file name>");
            }
        } catch (Exception e) {
            System.err.println("exception: " + e);
            e.printStackTrace(System.err);   // so we can get stack trace
        }
    }

    public static void doFiles(List<String> files) throws Exception {
        long parserStart = System.currentTimeMillis();
        for (String f : files) {
            parseFile(f);
        }
        long parserStop = System.currentTimeMillis();
        System.out.println("Total lexer+parser time " + (parserStop - parserStart) + "ms.");
    }


    public static List<String> getFilenames(File f) throws Exception {
        List<String> files = new ArrayList<String>();
        getFilenames_(f, files);
        return files;
    }

    public static void getFilenames_(File f, List<String> files) throws Exception {
        // If this is a directory, walk each file/dir in that directory
        if (f.isDirectory()) {
            String flist[] = f.list();
            for (int i = 0; i < flist.length; i++) {
                getFilenames_(new File(f, flist[i]), files);
            }
        }

        // otherwise, if this is a java file, parse it!
        else if (((f.getName().length() > 5) &&
                f.getName().substring(f.getName().length() - 5).equals(".java"))) {
            files.add(f.getAbsolutePath());
        }
    }

    public static WolframEquation parseEquation(String simplifiedEquation) {
        String finalEquation = null;
        String error = null;
        try {
            WolframQuery wolf = new WolframQuery();
            String solved = wolf.getWolframPlaintext(simplifiedEquation);
            finalEquation = solved == null ? simplifiedEquation : solved;
        } catch (Exception e) {
            error = e.getMessage();
            e.printStackTrace();
        }
        return new WolframEquation(finalEquation, error);
    }

    public static Result parsePartial(String text) {
        String antlrAst = null;
        String simplifiedAst = null;
        String rawEquation = null;
        String simplifiedEquation = null;
        String underlyingEquation = null;
        Map<String, String> assumptions = new HashMap<>();
        String error = null;

        try {
            Lexer lexer = new Java8Lexer(new ANTLRInputStream(text));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Java8Parser parser = new Java8Parser(tokens);

            // Parse
            ParserRuleContext t = parser.compilationUnit();

            // Generate ANTLR AST
            antlrAst = new PrettyPrintVisitor(parser).visit(t);

            // Generate Simple AST
            AstNode simpleAst = new SimplifierVisitor(parser).visit(t);
            simplifiedAst = new SimpleGrammarPrettyPrint().visit(simpleAst);

            // Generate raw equation
            BigOVisitor b = new BigOVisitor();
            MathExpression rawEq = b.visit(simpleAst);
            rawEquation = new EquationDisplayPrinter().visit(rawEq);

            // Generate simplified equation
            MathExpression simpleEq = new EquationSimplifier().visit(rawEq);
            underlyingEquation = simpleEq.toEquation();
            simplifiedEquation = new EquationDisplayPrinter().visit(simpleEq);

            // Grab assumptions
            assumptions = b.getAssumptions().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, pair -> pair.getValue().getName()));
        } catch (Exception e) {
            error = e.getMessage();
            e.printStackTrace();
        }

        return new Result(
                antlrAst,
                simplifiedAst,
                rawEquation,
                simplifiedEquation,
                underlyingEquation,
                null,
                assumptions,
                error);

    }

    public static Result parseFull(String text) {
        Result result = parsePartial(text);
        if (result.getError() != null) {
            WolframEquation eq = parseEquation(text);
            return new Result(
                    result.getAntlrAst(),
                    result.getSimplifiedAst(),
                    result.getRawEquation(),
                    result.getSimplifiedEquation(),
                    result.getUnderlyingEquation(),
                    eq.getFinalEquation(),
                    result.getAssumptions(),
                    eq.getError());
        } else {
            return result;
        }
    }

    public static void parseFile(String f) {
        try {
            System.err.println(f);

            // Setup
            Lexer lexer = new Java8Lexer(new ANTLRFileStream(f));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            Java8Parser parser = new Java8Parser(tokens);

            // Parse
            ParserRuleContext t = parser.compilationUnit();
            System.out.println(parser.getRuleNames()[t.getRuleIndex()]);

            // Generate ANTLR AST
            System.out.println(t.toStringTree(parser));
            System.out.println(new PrettyPrintVisitor(parser).visit(t));

            // Generate Simple AST
            AstNode simpleAst = new SimplifierVisitor(parser).visit(t);
            System.out.println(new SimpleGrammarPrettyPrint().visit(simpleAst));

            // Generate raw equation
            BigOVisitor b = new BigOVisitor();
            MathExpression rawEq = b.visit(simpleAst);
            System.out.println(rawEq.toEquation());

            // Generate simplified equation
            String simplified = new EquationSimplifier().visit(rawEq).toEquation();
            System.out.println(simplified);

            // Call wolfram
            WolframQuery wolf = new WolframQuery();
            String solved = wolf.getWolframPlaintext(simplified);
            System.out.println(solved);
            solved = solved == null ? simplified : solved;
            System.out.println(solved);

            // Print assumptions
            System.out.println(b.getAssumptions());
        } catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace();   // so we can get stack trace
        }
    }
}