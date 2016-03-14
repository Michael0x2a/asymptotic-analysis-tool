package bigo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import math.*;
import simplegrammar.*;

public class OutputComplexityVisitor extends AstNodeVisitor<MathExpression> {

	private final Map<String, String> symbolMap;
	private final Map<String, MathExpression> variableMap;
	private final Supplier<String> genId;

	public OutputComplexityVisitor(Map<String, String> symbolMap,
			Map<String, MathExpression> variableMap, Supplier<String> genId) {
		this.symbolMap = symbolMap;
		this.variableMap = variableMap;
		this.genId = genId;
	}

	@Override
	public MathExpression visitClassDecl(ClassDecl node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitMethodDecl(MethodDecl node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitParameter(Parameter node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitVariableDecl(VariableDecl node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitAssignment(Assignment node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitLookup(Lookup node) {
		String lookup = node.getName();
		if (symbolMap.containsKey(lookup)) {
			return visitLookup(new Lookup(symbolMap.get(lookup)));
		} else if (variableMap.containsKey(lookup)) {
			return variableMap.get(lookup);
		} else {
			symbolMap.put(lookup, genId.get());
			return new Variable(symbolMap.get(lookup));
		}
	}

	@Override
	public MathExpression visitForEachLoop(ForEachLoop node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitForLoop(ForLoop node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitReturn(Return node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitIfElse(IfElse node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitSpecialCall(SpecialCall node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MathExpression visitCall(Call node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MathExpression visitBinOp(BinOp node) {
		List<MathExpression> list = new ArrayList<>();
		list.add(visit(node.getLeft()));
		list.add(visit(node.getRight()));

		switch (node.getOperator()) {
		case "+": return new Addition(list);
		case "-": return new Subtraction(list.get(0), list.get(1));
		case "*": return new Multiplication(list);
		}

		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitUnaryOp(UnaryOp node) {
		MathExpression e = visit(node.getValue());

		switch (node.getOperator()) {
		case "-": return new Negation(e);
		}

		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitLiteral(Literal node) {
		try {
			return new Constant(Integer.parseInt(node.getText()));
		} catch (NumberFormatException e) {
			throw new UnsupportedOperationException();
		}
	}

	@Override
	public MathExpression visitType(Type node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public MathExpression visitMultipleAstNodes(MultipleAstNodes node) {
		throw new UnsupportedOperationException();
	}

}
