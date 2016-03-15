package bigo;

import simplegrammar.*;

import java.util.*;

import math.Addition;
import math.Constant;
import math.MathExpression;
import math.Multiplication;
import math.Variable;

public class BigOVisitor extends AstNodeVisitor<MathExpression> {
    private OutputComplexityVisitor outputComplexity;

    public BigOVisitor() {
        this.outputComplexity = new OutputComplexityVisitor();
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
            this.outputComplexity.recordAssumption(p);
        }

        // Visit body
        return new Addition(addAstNodes(node.getBody()));
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
        this.outputComplexity.recordVariable(node);
        return this.visit(node.getValue());
    }

    @Override
    public MathExpression visitLookup(Lookup node) {
        return new Constant(2);
    }

    @Override
    public MathExpression visitForEachLoop(ForEachLoop node) {
        MathExpression bodyRuntime = this.addAstNodes(node.getBody());
        MathExpression outerRuntime;

        AstNode sequence = node.getSequence();
        switch(sequence.nodeName()) {
            case "Lookup":
                outerRuntime = this.outputComplexity.lookupExpression((Lookup) sequence);
                break;
            default:
                throw new UnsupportedOperationException("what's " + node.nodeName() + "?");
        }

        return new Multiplication(Arrays.asList(outerRuntime, bodyRuntime));
    }

    @Override
    public MathExpression visitForLoop(ForLoop node) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public MathExpression visitReturn(Return node) {
        return this.visit(node.getValue());
    }

    @Override
    public MathExpression visitIfElse(IfElse node) {
        List<MathExpression> body = new ArrayList<>();
        body.add(addAstNodes(node.getFalseBranch()));
        body.add(addAstNodes(node.getTrueBranch()));
        return new Addition(body);
    }

    @Override
    public MathExpression visitSpecialCall(SpecialCall node) {
        switch(node.getMethodName()) {
            case "length":
                return this.outputComplexity.lookupExpression(node.getObject());
            case "arrayget":
            case "arrayput":
                return addAstNodes(node.getParameters());
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

    private MathExpression addAstNodes(List<AstNode> list) {
        List<MathExpression> sum = new ArrayList<>();
        for (AstNode node : list)
            sum.add(visit(node));
        return new Addition(sum);
    }
}
