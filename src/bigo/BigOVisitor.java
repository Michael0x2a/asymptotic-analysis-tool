package bigo;

import simplegrammar.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import math.Addition;
import math.Constant;
import math.MathExpression;
import math.Variable;

public class BigOVisitor extends AstNodeVisitor<MathExpression> {

	// Maps java variables to abstract symbols
	private Map<String, Variable> assumptions;
	
	// Maps java variables to math expressions representing their basic output complexities
	private Map<String, MathExpression> outputComplexities;
	
	// invariant: assumptions.keySet() and outputComplexities.keySet() is disjoint
	
	// Represents the remaining abstract symbols we can declare
	private Queue<Variable> symbols;
	
	public BigOVisitor() {
		assumptions = new HashMap<>();
		outputComplexities = new HashMap<>();
		String[] variables = {"n", "m", "x", "y", "z", "r", "s", "t", "u", "v", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "p", "q", "w"};
		symbols = new LinkedList<>();
		for (String s : variables)
			symbols.add(new Variable(s));
	}
	
    @Override
    public MathExpression visitClassDecl(ClassDecl node) {
    	List<MethodDecl> methods = node.getMethods();
    	if (methods.isEmpty()) {
    		throw new IllegalArgumentException("Class needs at least one method");
    	}
        return visit(methods.get(0));
    }

    @Override
    public MathExpression visitMethodDecl(MethodDecl node) {
    	// Add parameters to our assumptions
    	for (Parameter p : node.getParameters()) {
    		if (symbols.isEmpty()) {
    			throw new IllegalStateException("Ran out of abstract symbols!");
    		}
    		assumptions.put(p.toString(), symbols.remove());
    	}
    	
    	// Visit body
    	List<MathExpression> body = new ArrayList<>();
    	for (AstNode n : node.getBody())
    		body.add(visit(n));
    	return new Addition(body);
        
    }

    @Override
    public MathExpression visitParameter(Parameter node) {
        throw new IllegalStateException("tried to find Big-O of parameter declaration");
    }

    @Override
    public MathExpression visitVariableDecl(VariableDecl node) {
        return new Constant(1);
    }

    @Override
    public MathExpression visitAssignment(Assignment node) {
    	// TODO: update maps
    	String variableName = node.getName();
    	AstNode value = node.getValue();
    	if (!assumptions.containsKey(variableName)) {
    		// This is a local variable assignment
    		// calculate its value if possible and overwrite its entry in outputComplexities
    	} else {
    		// This is overwriting a parameter
    		// calculate its value if possible, overwrite its entry in output complexities, and remove it from assumptions
    	}
        return visit(node.getValue());
    }

    @Override
    public MathExpression visitLookup(Lookup node) {
        return new Constant(2);
    }

    @Override
    public MathExpression visitForEachLoop(ForEachLoop node) {
//        throw new UnsupportedOperationException("TODO");
        /*return formatNode(node,
                format("variable", node.getVariable()),
                format("sequence", node.getSequence()),
                format("body", node.getBody()));*/
    }

    @Override
    public MathExpression visitForLoop(ForLoop node) {
        throw new UnsupportedOperationException("TODO");
        /*return formatNode(node,
                format("counter", node.getCounter()),
                format("end", node.getEnd()),
                format("change", node.getChange()),
                format("body", node.getBody()));*/
    }

    @Override
    public MathExpression visitReturn(Return node) {
        return visit(node.getValue());
    }

    @Override
    public MathExpression visitIfElse(IfElse node) {
    	List<MathExpression> body = new ArrayList<>();
    	for (AstNode n : node.getFalseBranch())
    		body.add(visit(n));
    	for (AstNode n : node.getTrueBranch())
    		body.add(visit(n));
    	return new Addition(body);
    }

    @Override
    public MathExpression visitSpecialCall(SpecialCall node) {
    	switch(node.getMethodName()) {
    	case "length":
    		return new Constant(3);
    	case "arrayget":
    	case "arrayput":
    		List<MathExpression> list = new ArrayList<>();
    		for (AstNode n : node.getParameters()) {
    			list.add(visit(n));
    		}
            return new Addition(list);
    		default:
    			throw new IllegalArgumentException("what's " + node.getMethodName());
    	}
    }

    @Override
    public MathExpression visitCall(Call node) {
    	// TODO
        throw new UnsupportedOperationException("Call is not yet implemented");
    }

    @Override
    public MathExpression visitBinOp(BinOp node) {
    	List<MathExpression> list = new ArrayList<>();
    	list.add(visit(node.getLeft()));
    	list.add(visit(node.getRight()));
        return new Addition(list);
    }

    @Override
    public MathExpression visitUnaryOp(UnaryOp node) {
        return visit(node.getValue());
    }

    @Override
    public MathExpression visitLiteral(Literal node) {
        return new Constant(4);
    }

	@Override
	public MathExpression visitType(Type node) {
		throw new IllegalStateException("asked for big-O runtime of a type declaration");
	}

    @Override
    public MathExpression visitMultipleAstNodes(MultipleAstNodes node) {
        throw new IllegalStateException("MultipleAstNodes should never be in the final AST");
    }
}
