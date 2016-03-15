package tests;

import java.io.IOException;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import bigo.BigOVisitor;
import bigo.EquationSimplifier;
import bigo.SimpleGrammarPrettyPrint;
import grammar.Java8Lexer;
import grammar.Java8Parser;
import math.MathExpression;
import simplegrammar.*;
import visitors.SimplifierVisitor;

public class InsertionSort {

	public static void main(String[] args) throws IOException {
		BigOVisitor b = new BigOVisitor();
		System.out.println(new SimpleGrammarPrettyPrint().visit(get()));
		MathExpression rawEq = b.visit(get());
		System.out.println(rawEq.toEquation());
        System.out.println(new EquationSimplifier().visit(rawEq).toEquation());

        System.out.println("MAPS-------");
        System.out.println("ASSUMPTIONS: " + b.getAssumptions());
        System.out.println("VARIABLES: " + b.getVariables());
	}

	public static AstNode get() throws IOException {
		// Create a scanner that reads from the input stream passed to us
        Lexer lexer = new Java8Lexer(new ANTLRFileStream("sample/InsertionSort.java"));

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create a parser that reads from the scanner
        Java8Parser parser = new Java8Parser(tokens);

        // start parsing at the compilationUnit rule
        ParserRuleContext t = parser.compilationUnit();
        AstNode node = new SimplifierVisitor(parser).visit(t);
        return node;
	}

}
