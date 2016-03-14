package bigo;

import java.util.Arrays;
import java.util.List;

import simplegrammar.*;

public class ArraySumSimple {

	public static ClassDecl get() {
		VariableDecl varSumDecl = new VariableDecl(t("int"), "sum");
		Assignment varSumInitial = new Assignment("sum", lit("0"));

		Assignment varSumLoop = new Assignment("sum",
				new BinOp("+", l("sum"), l("i")));

		ForEachLoop forEachLoop = new ForEachLoop(l("i"), l("array"), list(varSumLoop));

		Return ret = new Return(l("sum"));

		MethodDecl methodSumDecl = new MethodDecl("sum",
				list(p("array", "int[]")),
				list(varSumDecl, varSumInitial, forEachLoop, ret));

		ClassDecl arraySum = new ClassDecl("ArraySum", list(methodSumDecl));

		return arraySum;
	}

	@SafeVarargs
	private static <E> List<E> list(E ... expressions) {
		return Arrays.asList(expressions);
	}

	private static Lookup l(String s) {
		return new Lookup(s);
	}

	private static Type t(String s) {
		return new Type(s);
	}

	private static Literal lit(String s) {
		return new Literal(s);
	}

	private static Parameter p(String n, String t) {
		return new Parameter(n, t(t));
	}

}
