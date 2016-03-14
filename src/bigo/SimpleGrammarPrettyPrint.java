package bigo;

import simplegrammar.*;

import java.util.List;

public class SimpleGrammarPrettyPrint extends AstNodeVisitor<String> {
    private String indent(StringBuilder build) {
        String out = "";
        for (String line : build.toString().split("\n")) {
            out += "    " + line + "\n";
        }
        return out;
    }

    private String formatNode(AstNode node, String... items) {
        StringBuilder out = new StringBuilder();
        for (String item : items) {
            out.append(item);
            out.append("\n");
        }
        return "(" + node.nodeName() + "\n" + this.indent(out) + ")";
    }

    private <T extends AstNode> String format(String name, List<T> expressions) {
        StringBuilder out = new StringBuilder();
        for (T expr : expressions) {
            out.append(this.visit(expr));
            out.append("\n");
        }
        return name + ": [\n" + this.indent(out) + "]";
    }

    private String format(String name, String value) {
        return name + ": " + value;
    }

    private String format(String name, AstNode expression) {
        return name + ": " + this.visit(expression);
    }

    @Override
    public String visitClassDecl(ClassDecl node) {
        return formatNode(node,
                format("className", node.getClassName()),
                format("methods", node.getMethods()));
    }

    @Override
    public String visitMethodDecl(MethodDecl node) {
        return formatNode(node,
                format("type", node.getReturnType()),
                format("name", node.getName()),
                format("params", node.getParameters()),
                format("body", node.getBody()));
    }

    @Override
    public String visitParameter(Parameter node) {
        return formatNode(node,
                format("name", node.getName()),
                format("type", node.getType()));
    }

    @Override
    public String visitVariableDecl(VariableDecl node) {
        return formatNode(node,
                format("type", node.getType()),
                format("name", node.getName()));
    }

    @Override
    public String visitAssignment(Assignment node) {
        return formatNode(node,
                format("name", node.getName()),
                format("value", node.getValue()));
    }

    @Override
    public String visitLookup(Lookup node) {
        return "(Lookup name: " + node.getName() + ")";
    }

    @Override
    public String visitForEachLoop(ForEachLoop node) {
        return formatNode(node,
                format("variable", node.getVariable()),
                format("sequence", node.getSequence()),
                format("body", node.getBody()));
    }

    @Override
    public String visitForLoop(ForLoop node) {
        return formatNode(node,
                format("counter", node.getCounter()),
                format("end", node.getEnd()),
                format("change", node.getChange()),
                format("body", node.getBody()));
    }

    @Override
    public String visitReturn(Return node) {
        return formatNode(node,
                format("value", node.getValue()));
    }

    @Override
    public String visitIfElse(IfElse node) {
        return formatNode(node,
                format("condition", node.getCondition()),
                format("trueBranch", node.getTrueBranch()),
                format("falseBranch", node.getFalseBranch()));
    }

    @Override
    public String visitSpecialCall(SpecialCall node) {
        return formatNode(node,
                format("object", node.getObject()),
                format("methodName", node.getMethodName()),
                format("parameters", node.getParameters()));
    }

    @Override
    public String visitCall(Call node) {
        return formatNode(node,
                format("methodName", node.getMethodName()),
                format("parameters", node.getParameters()));
    }

    @Override
    public String visitBinOp(BinOp node) {
        return formatNode(node,
                format("operator", node.getOperator()),
                format("left", node.getLeft()),
                format("right", node.getRight()));
    }

    @Override
    public String visitUnaryOp(UnaryOp node) {
        return formatNode(node,
                format("operator", node.getOperator()),
                format("value", node.getValue()));
    }

    @Override
    public String visitLiteral(Literal node) {
        return "(Literal " + node.getText() + ")";
    }

    @Override
    public String visitType(Type node) {
        return "(Type " + node.getFullType() + ")";
    }
}
