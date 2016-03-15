package bigo;

import org.antlr.v4.tool.ast.GrammarASTErrorNode;
import simplegrammar.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Utils {
    public static AstNode pick(Class<? extends AstNode> targetNodeType, AstNode option1, AstNode option2) {
        if (option1.getClass().equals(targetNodeType)) {
            return option1;
        } else if (option2.getClass().equals(targetNodeType)) {
            return option2;
        } else {
            return null;
        }
    }

    public static AstNode findFirst(Class<? extends AstNode> targetNodeType, AstNode root) {
        List<AstNode> options = Utils.pluck(targetNodeType, root);
        if (options == null) {
            return null;
        } else {
            return options.get(0);
        }
    }

    public static List<AstNode> pluck(Class<? extends AstNode> targetNodeType, AstNode root) {
        return new PluckVisitor(targetNodeType).visit(root);
    }

    public static List<AstNode> pluck(Predicate<AstNode> filterFunc, AstNode root) {
        return new PluckVisitor(filterFunc).visit(root);
    }

    public static boolean isRecursiveFunction(MethodDecl method) {
        List<AstNode> methodCalls = Utils.pluck(Call.class, method);
        return methodCalls.stream()
                .map(m -> (Call) m)
                .anyMatch(m -> m.getMethodName().equals(method.getName()));
    }

    public static List<AstNode> getChildren(AstNode node) {
        return new Crawl().visit(node);
    }


    private static class PluckVisitor extends AstNodeVisitor<List<AstNode>> {
        private Predicate<AstNode> filterFunc;
        private List<AstNode> output;

        public PluckVisitor(Class<? extends AstNode> targetType) {
            this(node -> node.getClass().equals(targetType));
        }

        public PluckVisitor(Predicate<AstNode> filterFunc) {
            this.filterFunc = filterFunc;
            this.output = new ArrayList<>();
        }

        public List<AstNode> getOutput() {
            return this.output;
        }

        private <T extends AstNode> List<AstNode> filter(T node) {
            if (this.filterFunc.test(node)) {
                this.output.add(node);
            }
            Utils.getChildren(node).stream().forEach(this::visit);
            return this.output;
        }

        @Override
        public List<AstNode> visitClassDecl(ClassDecl node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitMethodDecl(MethodDecl node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitParameter(Parameter node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitVariableDecl(VariableDecl node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitAssignment(Assignment node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitLookup(Lookup node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitForEachLoop(ForEachLoop node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitForLoop(ForLoop node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitReturn(Return node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitIfElse(IfElse node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitSpecialCall(SpecialCall node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitCall(Call node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitBinOp(BinOp node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitUnaryOp(UnaryOp node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitLiteral(Literal node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitType(Type node) {
            return this.filter(node);
        }

        @Override
        public List<AstNode> visitMultipleAstNodes(MultipleAstNodes node) {
            return this.filter(node);
        }
    }

    public static class Crawl extends AstNodeVisitor<List<AstNode>> {
        private List<AstNode> map(List<? extends AstNode> items) {
            return items.stream().map(m -> (AstNode) m).collect(Collectors.toList());
        }

        private List<AstNode> single(AstNode item) {
            return Collections.singletonList(item);
        }

        private List<AstNode> merge(List<AstNode>... items) {
            List<AstNode> output = new ArrayList<>();
            for (List<AstNode> item : items) {
                output.addAll(item);
            }
            return output;
        }

        private List<AstNode> empty() {
            return new ArrayList<>();
        }

        @Override
        public List<AstNode> visitClassDecl(ClassDecl node) {
            return map(node.getMethods());
        }

        @Override
        public List<AstNode> visitMethodDecl(MethodDecl node) {
            return merge(node.getBody(), map(node.getParameters()), single(node.getReturnType()));
        }

        @Override
        public List<AstNode> visitParameter(Parameter node) {
            return empty();
        }

        @Override
        public List<AstNode> visitVariableDecl(VariableDecl node) {
            return single(node.getType());
        }

        @Override
        public List<AstNode> visitAssignment(Assignment node) {
            return single(node.getValue());
        }

        @Override
        public List<AstNode> visitLookup(Lookup node) {
            return empty();
        }

        @Override
        public List<AstNode> visitForEachLoop(ForEachLoop node) {
            return merge(single(node.getVariable()), single(node.getSequence()), node.getBody());
        }

        @Override
        public List<AstNode> visitForLoop(ForLoop node) {
            return merge(single(node.getChange()), single(node.getCounter()), single(node.getEnd()), node.getBody());
        }

        @Override
        public List<AstNode> visitReturn(Return node) {
            return single(node.getValue());
        }

        @Override
        public List<AstNode> visitIfElse(IfElse node) {
            return merge(single(node.getCondition()), node.getTrueBranch(), node.getFalseBranch());
        }

        @Override
        public List<AstNode> visitSpecialCall(SpecialCall node) {
            return merge(single(node.getObject()), node.getParameters());
        }

        @Override
        public List<AstNode> visitCall(Call node) {
            return node.getParameters();
        }

        @Override
        public List<AstNode> visitBinOp(BinOp node) {
            return merge(single(node.getLeft()), single(node.getRight()));
        }

        @Override
        public List<AstNode> visitUnaryOp(UnaryOp node) {
            return single(node.getValue());
        }

        @Override
        public List<AstNode> visitLiteral(Literal node) {
            return empty();
        }

        @Override
        public List<AstNode> visitType(Type node) {
            return empty();
        }

        @Override
        public List<AstNode> visitMultipleAstNodes(MultipleAstNodes node) {
            return node.getNodes();
        }
    }
}
