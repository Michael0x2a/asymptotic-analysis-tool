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
        List<AstNode> body = nodes.getNodes();
        body.add(new Literal(bodyCtx.toStringTree(this.parser)));

        return new MethodDecl(returnType, methodName, parameters, body);
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
        return new Literal(ctx.toStringTree(this.parser));
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
        System.out.println(declarations);
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

        Java8Parser.AssignmentExpressionContext asgn = ctx.assignmentExpression();
        if (asgn.assignment() != null) {
            throw new UnsupportedOperationException("Inline assignment is not supported");
        }

        Java8Parser.ConditionalExpressionContext cond = asgn.conditionalExpression();
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
            return new UnaryOp("++", this.visit(ctx.unaryExpression()));
        } else if (ctx.preDecrementExpression() != null) {
            return new UnaryOp("--", this.visit(ctx.unaryExpression()));
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
            Java8Parser.PostfixExpressionContext postfix = ctx.postfixExpression();
            AstNode var;
            if (postfix.primary() != null) {
                var = new Literal(postfix.primary().getText());
            } else {
                var = new Lookup(postfix.expressionName().getText());
            }
            if (postfix.children.size() > 1) {
                return new UnaryOp(ctx.getChild(1).getText(), var);
            } else {
                return var;
            }
        }
    }
}
