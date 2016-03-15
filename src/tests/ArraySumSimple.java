package tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;

import bigo.BigOVisitor;
import grammar.Java8Lexer;
import grammar.Java8Parser;
import simplegrammar.*;
import visitors.PrettyPrintVisitor;
import visitors.SimplifierVisitor;

public class ArraySumSimple {

	public static void main(String[] args) throws IOException {
		//System.out.println(new SimpleGrammarPrettyPrint().visit(get()));
		System.out.println(new BigOVisitor().visit(get()).toEquation());
	}

	public static AstNode get() throws IOException {
		// Create a scanner that reads from the input stream passed to us
        Lexer lexer = new Java8Lexer(new ANTLRFileStream("sample/ArraySum.java"));

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create a parser that reads from the scanner
        Java8Parser parser = new Java8Parser(tokens);

        // start parsing at the compilationUnit rule
        ParserRuleContext t = parser.compilationUnit();
        AstNode node = new SimplifierVisitor(parser).visit(t);
        return node;
	}
//	public static ClassDecl get() {
//		VariableDecl varSumDecl = new VariableDecl(t("int"), "sum");
//		Assignment varSumInitial = new Assignment("sum", lit("0"));
//
//		Assignment varSumLoop = new Assignment("sum",
//				new BinOp("+", l("sum"), l("i")));
//
//		ForEachLoop forEachLoop = new ForEachLoop(l("i"), l("array"), list(varSumLoop));
//
//		Return ret = new Return(l("sum"));
//
//		MethodDecl methodSumDecl = new MethodDecl(new Type("int"), "sum",
//				list(p("array", "int[]")),
//				list(varSumDecl, varSumInitial, forEachLoop, ret));
//
//		ClassDecl arraySum = new ClassDecl("ArraySum", list(methodSumDecl));
//
//		return arraySum;
//	}

//	@SafeVarargs
//	private static <E> List<E> list(E ... expressions) {
//		return Arrays.asList(expressions);
//	}
//
//	private static Lookup l(String s) {
//		return new Lookup(s);
//	}
//
//	private static Type t(String s) {
//		return new Type(s);
//	}
//
//	private static Literal lit(String s) {
//		return new Literal(s);
//	}
//
//	private static Parameter p(String n, String t) {
//		return new Parameter(n, t(t));
//	}

}
