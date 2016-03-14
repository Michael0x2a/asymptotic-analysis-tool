package visitors;

import grammar.Java8BaseVisitor;
import grammar.Java8Parser;
import simplegrammar.*;

import java.util.ArrayList;
import java.util.List;

public class SimplifierVisitor extends Java8BaseVisitor<AstNode> {
    private Java8Parser parser;

    public SimplifierVisitor(Java8Parser parser) {
        this.parser = parser;
    }

    @Override
    public AstNode visitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        assert ctx.typeDeclaration().size() == 1;
        Java8Parser.NormalClassDeclarationContext cls = ctx
                .typeDeclaration(0)
                .classDeclaration()
                .normalClassDeclaration();

        String className = cls.getChild(2).getText();
        Java8Parser.MethodDeclarationContext method = cls
                .classBody()
                .classBodyDeclaration()
                .get(0)
                .classMemberDeclaration()
                .methodDeclaration();

        // TODO: Modify to handle multiple methods
        List<MethodDecl> methods = new ArrayList<>();
        methods.add((MethodDecl) this.visit(method));
        return new ClassDecl(className, methods);
    }

    @Override
    public AstNode visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        Type returnType = (Type) this.visit(ctx.methodHeader().result().unannType());
        String methodName = ctx.methodHeader().methodDeclarator().getChild(0).getText();
        Java8Parser.FormalParameterListContext paramsCtx = ctx.methodHeader().methodDeclarator().formalParameterList();
        Java8Parser.MethodBodyContext bodyCtx = ctx.methodBody();

        List<Parameter> parameters = new ArrayList<>();
        if (paramsCtx.formalParameters() != null) {
            // Jump by two to skip commas
            for (int i = 0; i < paramsCtx.formalParameters().children.size(); i += 2) {
                parameters.add((Parameter) this.visit(paramsCtx.formalParameters().getChild(i)));
            }
        }
        parameters.add((Parameter) this.visit(paramsCtx.lastFormalParameter().formalParameter()));

        MultipleAstNodes nodes = (MultipleAstNodes) this.visit(bodyCtx.block());
        return new MethodDecl(returnType, methodName, parameters, nodes.getNodes());
    }

    @Override
    public AstNode visitUnannType(Java8Parser.UnannTypeContext ctx) {
        return new Type(ctx.getText());
    }

    @Override
    public AstNode visitFormalParameter(Java8Parser.FormalParameterContext ctx) {
        Type type = (Type) this.visit(ctx.unannType());
        String name = ctx.variableDeclaratorId().getChild(0).getText();
        return new Parameter(name, type);
    }

    @Override
    public AstNode visitBlock(Java8Parser.BlockContext ctx) {
        List<AstNode> body = new ArrayList<>();
        // Skip the first and last opening curly brackets
        for (Java8Parser.BlockStatementContext stmt : ctx.blockStatements().blockStatement()) {
            AstNode node;
            if (stmt.localVariableDeclarationStatement() != null) {
                node = this.visit(stmt.localVariableDeclarationStatement().localVariableDeclaration());
            } else if (stmt.statement() != null) {
                node = this.visit(stmt.statement());
            } else {
                throw new UnsupportedOperationException(stmt.toStringTree(this.parser));
            }

            if (node instanceof MultipleAstNodes) {
                body.addAll(((MultipleAstNodes) node).getNodes());
            } else {
                body.add(node);
            }
        }
        return new MultipleAstNodes(body);
    }

    @Override
    public AstNode visitStatement(Java8Parser.StatementContext ctx) {
        return this.visit(ctx.getChild(0));
    }

    @Override
    public AstNode visitStatementExpression(Java8Parser.StatementExpressionContext ctx) {
        return this.visit(ctx.getChild(0));
    }

    @Override
    public AstNode visitStatementWithoutTrailingSubstatement(Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
        if (ctx.returnStatement() != null) {
            return new Return(this.visit(ctx.returnStatement().expression()));
        } else if (ctx.emptyStatement() != null) {
            return new MultipleAstNodes();  // Return empty list
        } else if (ctx.expressionStatement() != null) {
            return this.visit(ctx.expressionStatement().statementExpression());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public AstNode visitLabeledStatement(Java8Parser.LabeledStatementContext ctx) {
        throw new UnsupportedOperationException("labeledStatement is not supported");
    }

    @Override
    public AstNode visitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        return new Literal(ctx.toStringTree(this.parser));
    }

    @Override
    public AstNode visitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        return new Literal(ctx.toStringTree(this.parser));
    }

    @Override
    public AstNode visitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        throw new UnsupportedOperationException("While statement");
    }

    @Override
    public AstNode visitForStatement(Java8Parser.ForStatementContext ctx) {
        if (ctx.enhancedForStatement() != null) {
            Type varType = (Type) this.visit(ctx.enhancedForStatement().unannType());
            String varName = ctx.enhancedForStatement().variableDeclaratorId().getChild(0).getText();
            AstNode seq = this.visit(ctx.enhancedForStatement().expression());

            List<AstNode> body = new ArrayList<>();
            AstNode bodyNode = this.visit(ctx.enhancedForStatement().statement());
            if (bodyNode instanceof MultipleAstNodes) {
                body.addAll(((MultipleAstNodes) bodyNode).getNodes());
            } else {
                body.add(bodyNode);
            }

            return new MultipleAstNodes(
                    new VariableDecl(varType, varName),
                    new ForEachLoop(new Lookup(varName), seq, body));
        } else {
            throw new UnsupportedOperationException("Regular for loops");
        }
    }

    @Override
    public AstNode visitAssignment(Java8Parser.AssignmentContext ctx) {
        String varName = ctx.leftHandSide().expressionName().getChild(0).getText();
        String op = ctx.assignmentOperator().getChild(0).getText();
        AstNode expr = this.visit(ctx.expression());

        if (op.equals("=")) {
            return new Assignment(varName, expr);
        } else {
            return new Assignment(varName, new BinOp(op.substring(0, 1), new Lookup(varName), expr));
        }
    }

    @Override
    public AstNode visitLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx) {
        Type type = (Type) this.visit(ctx.unannType());

        List<AstNode> declarations = new ArrayList<>();
        for (Java8Parser.VariableDeclaratorContext variable : ctx.variableDeclaratorList().variableDeclarator()) {
            String variableName = variable.variableDeclaratorId().getChild(0).getText();
            declarations.add(new VariableDecl(type, variableName));

            if (variable.variableInitializer() != null) {
                declarations.add(new Assignment(variableName, this.visit(variable.variableInitializer())));
            }
        }
        return new MultipleAstNodes(declarations);
    }

    @Override
    public AstNode visitVariableInitializer(Java8Parser.VariableInitializerContext ctx) {
        return this.visit(ctx.expression());
    }

    @Override
    public AstNode visitExpression(Java8Parser.ExpressionContext ctx) {
        if (ctx.lambdaExpression() != null) {
            throw new UnsupportedOperationException("Lambda expressions are not supported");
        }
        return this.visit(ctx.assignmentExpression());
    }

    @Override
    public AstNode visitAssignmentExpression(Java8Parser.AssignmentExpressionContext ctx) {
        if (ctx.assignment() != null) {
            throw new UnsupportedOperationException("Inline assignment is not supported");
        }

        Java8Parser.ConditionalExpressionContext cond = ctx.conditionalExpression();
        if (cond.children.size() > 1) {
            throw new UnsupportedOperationException("Ternary is not supported");
        }

        return this.visit(cond.conditionalOrExpression());
    }

    @Override
    public AstNode visitConditionalOrExpression(Java8Parser.ConditionalOrExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            return new BinOp("||",
                    this.visit(ctx.conditionalOrExpression()),
                    this.visit(ctx.conditionalAndExpression()));
        } else {
            return this.visit(ctx.conditionalAndExpression());
        }
    }

    @Override
    public AstNode visitConditionalAndExpression(Java8Parser.ConditionalAndExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            return new BinOp("&&",
                    this.visit(ctx.conditionalAndExpression()),
                    this.visit(ctx.inclusiveOrExpression()));
        } else {
            return this.visit(ctx.inclusiveOrExpression());
        }
    }

    @Override
    public AstNode visitInclusiveOrExpression(Java8Parser.InclusiveOrExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            return new BinOp("|",
                    this.visit(ctx.inclusiveOrExpression()),
                    this.visit(ctx.exclusiveOrExpression()));
        } else {
            return this.visit(ctx.exclusiveOrExpression());
        }
    }

    @Override
    public AstNode visitExclusiveOrExpression(Java8Parser.ExclusiveOrExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            return new BinOp("^",
                    this.visit(ctx.exclusiveOrExpression()),
                    this.visit(ctx.andExpression()));
        } else {
            return this.visit(ctx.andExpression());
        }

    }

    @Override
    public AstNode visitAndExpression(Java8Parser.AndExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            return new BinOp("&",
                    this.visit(ctx.andExpression()),
                    this.visit(ctx.equalityExpression()));
        } else {
            return this.visit(ctx.equalityExpression());
        }

    }

    @Override
    public AstNode visitEqualityExpression(Java8Parser.EqualityExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            return new BinOp(ctx.getChild(1).getText(),
                    this.visit(ctx.equalityExpression()),
                    this.visit(ctx.relationalExpression()));
        } else {
            return this.visit(ctx.relationalExpression());
        }
    }

    @Override
    public AstNode visitRelationalExpression(Java8Parser.RelationalExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            String op = ctx.getChild(0).getText();
            if (op.equals("instanceof")) {
                return new BinOp(op,
                        this.visit(ctx.relationalExpression()),
                        new Type(ctx.referenceType().getText()));
            } else {
                return new BinOp(op,
                        this.visit(ctx.relationalExpression()),
                        this.visit(ctx.shiftExpression()));
            }
        } else {
            return this.visit(ctx.shiftExpression());
        }
    }

    public AstNode visitShiftExpression(Java8Parser.ShiftExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            throw new UnsupportedOperationException("Shift expressions are not supported");
        } else {
            return this.visit(ctx.additiveExpression());
        }
    }

    @Override
    public AstNode visitAdditiveExpression(Java8Parser.AdditiveExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            return new BinOp(ctx.getChild(1).getText(),
                    this.visit(ctx.additiveExpression()),
                    this.visit(ctx.multiplicativeExpression()));
        } else {
            return this.visit(ctx.multiplicativeExpression());
        }
    }

    @Override
    public AstNode visitMultiplicativeExpression(Java8Parser.MultiplicativeExpressionContext ctx) {
        if (ctx.children.size() > 1) {
            return new BinOp(ctx.getChild(1).getText(),
                    this.visit(ctx.multiplicativeExpression()),
                    this.visit(ctx.unaryExpression()));
        } else {
            return this.visit(ctx.unaryExpression());
        }
    }

    @Override
    public AstNode visitUnaryExpression(Java8Parser.UnaryExpressionContext ctx) {
        if (ctx.preIncrementExpression() != null) {
            return this.visit(ctx.preIncrementExpression());
        } else if (ctx.preDecrementExpression() != null) {
            return this.visit(ctx.preIncrementExpression());
        } else if (ctx.unaryExpressionNotPlusMinus() != null){
            return this.visit(ctx.unaryExpressionNotPlusMinus());
        } else {
            return new UnaryOp(ctx.getChild(0).getText(), this.visit(ctx.unaryExpression()));
        }
    }

    @Override
    public AstNode visitUnaryExpressionNotPlusMinus(Java8Parser.UnaryExpressionNotPlusMinusContext ctx) {
        if (ctx.unaryExpression() != null) {
            return new UnaryOp(ctx.getChild(0).getText(), this.visit(ctx.unaryExpression()));
        } else if (ctx.castExpression() != null) {
            throw new UnsupportedOperationException("Casts are not supported");
        } else {
            return this.visit(ctx.postfixExpression());
        }
    }

    @Override
    public AstNode visitPostfixExpression(Java8Parser.PostfixExpressionContext ctx) {
        AstNode var;
        if (ctx.primary() != null) {
            var = new Literal(ctx.primary().getText());
        } else {
            var = new Lookup(ctx.expressionName().getText());
        }
        if (ctx.children.size() > 1) {
            return new UnaryOp(ctx.getChild(1).getText(), var);
        } else {
            return var;
        }
    }

    @Override
    public AstNode visitPreIncrementExpression(Java8Parser.PreIncrementExpressionContext ctx) {
        return new UnaryOp("++", this.visit(ctx.unaryExpression()));
    }

    @Override
    public AstNode visitPreDecrementExpression(Java8Parser.PreDecrementExpressionContext ctx) {
        return new UnaryOp("--", this.visit(ctx.unaryExpression()));
    }

    @Override
    public AstNode visitPostIncrementExpression(Java8Parser.PostIncrementExpressionContext ctx) {
        return new UnaryOp("++", this.visit(ctx.postfixExpression()));
    }

    @Override
    public AstNode visitPostDecrementExpression(Java8Parser.PostDecrementExpressionContext ctx) {
        return new UnaryOp("--", this.visit(ctx.postfixExpression()));
    }

    @Override
    public AstNode visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        throw new UnsupportedOperationException("ClassInstanceCreationExpression not supported");
    }
}
