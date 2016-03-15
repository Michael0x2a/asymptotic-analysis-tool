package main;

import bigo.BigOVisitor;
import bigo.SimpleGrammarPrettyPrint;
import math.MathExpression;
import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import java.io.File;
import java.lang.System;
import java.util.ArrayList;
import java.util.List;

import grammar.*;
import simplegrammar.AstNode;
import visitors.PrettyPrintVisitor;
import visitors.SimplifierVisitor;

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
            MathExpression rawEq = new BigOVisitor().visit(simpleAst);
            System.out.println(rawEq.toEquation());
        } catch (Exception e) {
            System.err.println("parser exception: " + e);
            e.printStackTrace();   // so we can get stack trace
        }
    }
}