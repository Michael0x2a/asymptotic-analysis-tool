package bigo;

import org.antlr.v4.tool.ast.GrammarASTErrorNode;
import simplegrammar.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
}
