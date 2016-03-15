package visitors;

import grammar.Java8BaseVisitor;
import grammar.Java8Parser;
import simplegrammar.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SimplifierVisitor extends Java8BaseVisitor<AstNode> {
    private Java8Parser parser;

    public SimplifierVisitor(Java8Parser parser) {
        this.parser = parser;
    }

    private List<AstNode> expand(AstNode node) {
        if (node instanceof MultipleAstNodes) {
            return new ArrayList<>(((MultipleAstNodes) node).getNodes());
        } else {
            List<AstNode> out = new ArrayList<>();
            out.add(node);
            return out;
        }
    }

    @Override
    public AstNode visitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        assert ctx.typeDeclaration().size() == 1;
        Java8Parser.NormalClassDeclarationContext cls = ctx
                .typeDeclaration(0)
                .classDeclaration()
                .normalClassDeclaration();

        String className = cls.getChild(2).getText();

        List<MethodDecl> methods = new ArrayList<>();
        for (Java8Parser.ClassBodyDeclarationContext item : cls.classBody().classBodyDeclaration()) {
            if (item.classMemberDeclaration().methodDeclaration() != null) {
                methods.add((MethodDecl) this.visit(item.classMemberDeclaration().methodDeclaration()));
            }
        }

        return new ClassDecl(className, methods);
    }

    @Override
    public AstNode visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        Type returnType;
        if (ctx.methodHeader().result().getChild(0).getText().equals("void")) {
            returnType = new Type("void");
        } else {
            returnType = (Type) this.visit(ctx.methodHeader().result().unannType());
        }
        String methodName = ctx.methodHeader().methodDeclarator().getChild(0).getText();
        Java8Parser.FormalParameterListContext paramsCtx = ctx.methodHeader().methodDeclarator().formalParameterList();
        Java8Parser.MethodBodyContext bodyCtx = ctx.methodBody();

        List<Parameter> parameters = new ArrayList<>();
        if (paramsCtx != null) {
            if (paramsCtx.formalParameters() != null) {
                // Jump by two to skip commas
                for (int i = 0; i < paramsCtx.formalParameters().children.size(); i += 2) {
                    parameters.add((Parameter) this.visit(paramsCtx.formalParameters().getChild(i)));
                }
            }
            parameters.add((Parameter) this.visit(paramsCtx.lastFormalParameter().formalParameter()));
        }

        return new MethodDecl(returnType, methodName, parameters, expand(this.visit(bodyCtx.block())));
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

            body.addAll(expand(node));
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
        } else if (ctx.block() != null) {
            return this.visit(ctx.block());
        } else {
            throw new UnsupportedOperationException(ctx.toStringTree(this.parser));
        }
    }

    @Override
    public AstNode visitLabeledStatement(Java8Parser.LabeledStatementContext ctx) {
        throw new UnsupportedOperationException("labeledStatement is not supported");
    }

    @Override
    public AstNode visitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        return new IfElse(
                this.visit(ctx.expression()),
                expand(this.visit(ctx.statement())),
                Collections.emptyList());
    }

    @Override
    public AstNode visitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        return new IfElse(
                this.visit(ctx.expression()),
                expand(this.visit(ctx.getChild(4).getChild(0))),
                expand(this.visit(ctx.getChild(6).getChild(0))));
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

            List<AstNode> body = expand(this.visit(ctx.enhancedForStatement().statement()));

            return new MultipleAstNodes(
                    new VariableDecl(varType, varName),
                    new ForEachLoop(new Lookup(varName), seq, body));
        } else {
            Java8Parser.BasicForStatementContext loop = ctx.basicForStatement();
            if ((loop.forInit().statementExpressionList() != null && loop.forInit().statementExpressionList().children.size() > 1) ||
                    loop.forUpdate().statementExpressionList().children.size() > 1) {
                throw new UnsupportedOperationException("For loops w/ multiple statements are not allowed");
            }

            AstNode init;
            if (loop.forInit().statementExpressionList() != null) {
                init = this.visit(loop.forInit().statementExpressionList().statementExpression(0));
            } else {
                init = expand(this.visit(loop.forInit().localVariableDeclaration())).get(0);
            }

            AstNode expr = this.visit(loop.expression());

            AstNode update = this.visit(loop.forUpdate().statementExpressionList().statementExpression(0));

            List<AstNode> body = expand(this.visit(loop.statement()));

            return new ForLoop(init, expr, update, body);
        }
    }

    @Override
    public AstNode visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        // TODO: Make method invocation less ghetto
        String methodName = ctx.getText().split("\\(")[0];
        List<AstNode> params = ctx.argumentList().children.stream().map(this::visit).collect(Collectors.toList());
        return new Call(methodName, params);
    }

    @Override
    public AstNode visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        // TODO: Make method invocation less ghetto
        String methodName = ctx.getText().split("\\(")[0];
        List<AstNode> params = ctx.argumentList().children.stream().map(this::visit).collect(Collectors.toList());
        return new Call(methodName, params);
    }

    @Override
    public AstNode visitAssignment(Java8Parser.AssignmentContext ctx) {
        if (ctx.leftHandSide().expressionName() != null) {
            String varName = ctx.leftHandSide().expressionName().getChild(0).getText();
            String op = ctx.assignmentOperator().getChild(0).getText();
            AstNode expr = this.visit(ctx.expression());

            if (op.equals("=")) {
                return new Assignment(varName, expr);
            } else {
                return new Assignment(varName, new BinOp(op.substring(0, 1), new Lookup(varName), expr));
            }
        } else if (ctx.leftHandSide().arrayAccess() != null) {
            Java8Parser.ArrayAccessContext arr = ctx.leftHandSide().arrayAccess();
            if (arr.expression().size() > 1) {
                throw new UnsupportedOperationException("Only 1d array access is currently supported");
            }

            String arrayName = arr.expressionName().getText();
            AstNode index = this.visit(arr.expression(0));
            String op = ctx.assignmentOperator().getChild(0).getText();
            AstNode expr = this.visit(ctx.expression());

            List<AstNode> params = new ArrayList<>();
            params.add(index);

            if (op.equals("=")) {
                params.add(expr);
            } else {
                AstNode prev = new SpecialCall(new Lookup(arrayName), "arrayget", new ArrayList<>(params));
                params.add(new BinOp(op.substring(0, 1), prev, expr));
            }
            return new SpecialCall(new Lookup(arrayName), "arrayput", params);
        } else if (ctx.leftHandSide().fieldAccess() != null) {
            throw new UnsupportedOperationException("field set is not supported");
        } else {
            throw new AssertionError();
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
            String op = ctx.getChild(1).getText();
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
            var = this.visit(ctx.primary());
        } else {
            var = this.visit(ctx.expressionName());
        }
        if (ctx.children.size() > 1) {
            return new UnaryOp(ctx.getChild(1).getText(), var);
        } else {
            return var;
        }
    }

    @Override
    public AstNode visitPrimary(Java8Parser.PrimaryContext ctx) {
        if (ctx.arrayCreationExpression() != null) {
            throw new UnsupportedOperationException("Array creation currently not supported");
        } else if (ctx.primaryNoNewArray_lfno_primary() != null){
            return this.visit(ctx.primaryNoNewArray_lfno_primary());
        } else {
            return new Literal(ctx.toStringTree(this.parser));
        }
    }

    @Override
    public AstNode visitPrimaryNoNewArray_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) {
        if (ctx.literal() != null) {
            return new Literal(ctx.literal().getText());
        } else if (ctx.expression() != null) {
            return this.visit(ctx.expression());
        } else if (ctx.methodInvocation_lfno_primary() != null) {
            return this.visit(ctx.methodInvocation_lfno_primary());
        } else if (ctx.methodReference_lfno_primary() != null) {
            throw new UnsupportedOperationException("methodReference_lfno_primary");
        } else if (ctx.fieldAccess_lfno_primary() != null) {
            throw new UnsupportedOperationException("field access");
        } else if (ctx.arrayAccess_lfno_primary() != null) {
            return this.visit(ctx.arrayAccess_lfno_primary());
        } else {
            throw new UnsupportedOperationException(ctx.toStringTree(this.parser));
        }
    }

    @Override
    public AstNode visitArrayAccess_lfno_primary(Java8Parser.ArrayAccess_lfno_primaryContext ctx) {
        if (ctx.expressionName() != null) {
            if (ctx.expression().size() > 1) {
                throw new UnsupportedOperationException("Only accessing 1d arrays is supported");
            }
            AstNode var = this.visit(ctx.expressionName());
            AstNode index = this.visit(ctx.expression(0));

            return new SpecialCall(var, "arrayget", Arrays.asList(index));
        } else {
            throw new UnsupportedOperationException(ctx.toStringTree(this.parser));
        }
    }

    @Override
    public AstNode visitExpressionName(Java8Parser.ExpressionNameContext ctx) {
        if (ctx.ambiguousName() != null) {
            return new SpecialCall(
                    new Lookup(ctx.ambiguousName().getText()),
                    ctx.Identifier().getText(),
                    new ArrayList<>());
        } else {
            return new Lookup(ctx.Identifier().getText());
        }
    }

    @Override
    public AstNode visitPreIncrementExpression(Java8Parser.PreIncrementExpressionContext ctx) {
        return new BinOp("+", this.visit(ctx.unaryExpression()), new Literal("1"));
    }

    @Override
    public AstNode visitPreDecrementExpression(Java8Parser.PreDecrementExpressionContext ctx) {
        return new BinOp("-", this.visit(ctx.unaryExpression()), new Literal("1"));
    }

    @Override
    public AstNode visitPostIncrementExpression(Java8Parser.PostIncrementExpressionContext ctx) {
        return new BinOp("+", this.visit(ctx.postfixExpression()), new Literal("1"));
    }

    @Override
    public AstNode visitPostDecrementExpression(Java8Parser.PostDecrementExpressionContext ctx) {
        return new BinOp("-", this.visit(ctx.postfixExpression()), new Literal("1"));
    }

    @Override
    public AstNode visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        throw new UnsupportedOperationException("ClassInstanceCreationExpression not supported");
    }

    @Override
    public AstNode visitLiteral(Java8Parser.LiteralContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitType(Java8Parser.TypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimitiveType(Java8Parser.PrimitiveTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitNumericType(Java8Parser.NumericTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitIntegralType(Java8Parser.IntegralTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFloatingPointType(Java8Parser.FloatingPointTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitReferenceType(Java8Parser.ReferenceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassOrInterfaceType(Java8Parser.ClassOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassType(Java8Parser.ClassTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassType_lf_classOrInterfaceType(Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassType_lfno_classOrInterfaceType(Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceType(Java8Parser.InterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceType_lf_classOrInterfaceType(Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceType_lfno_classOrInterfaceType(Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeVariable(Java8Parser.TypeVariableContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitArrayType(Java8Parser.ArrayTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitDims(Java8Parser.DimsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeParameter(Java8Parser.TypeParameterContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeParameterModifier(Java8Parser.TypeParameterModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeBound(Java8Parser.TypeBoundContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAdditionalBound(Java8Parser.AdditionalBoundContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeArguments(Java8Parser.TypeArgumentsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeArgumentList(Java8Parser.TypeArgumentListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeArgument(Java8Parser.TypeArgumentContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitWildcard(Java8Parser.WildcardContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitWildcardBounds(Java8Parser.WildcardBoundsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPackageName(Java8Parser.PackageNameContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeName(Java8Parser.TypeNameContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPackageOrTypeName(Java8Parser.PackageOrTypeNameContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodName(Java8Parser.MethodNameContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAmbiguousName(Java8Parser.AmbiguousNameContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPackageModifier(Java8Parser.PackageModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSingleTypeImportDeclaration(Java8Parser.SingleTypeImportDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeImportOnDemandDeclaration(Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSingleStaticImportDeclaration(Java8Parser.SingleStaticImportDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitStaticImportOnDemandDeclaration(Java8Parser.StaticImportOnDemandDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeDeclaration(Java8Parser.TypeDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassModifier(Java8Parser.ClassModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeParameters(Java8Parser.TypeParametersContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeParameterList(Java8Parser.TypeParameterListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSuperclass(Java8Parser.SuperclassContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSuperinterfaces(Java8Parser.SuperinterfacesContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceTypeList(Java8Parser.InterfaceTypeListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassBody(Java8Parser.ClassBodyContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassMemberDeclaration(Java8Parser.ClassMemberDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFieldModifier(Java8Parser.FieldModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitVariableDeclarator(Java8Parser.VariableDeclaratorContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannPrimitiveType(Java8Parser.UnannPrimitiveTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannReferenceType(Java8Parser.UnannReferenceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannClassOrInterfaceType(Java8Parser.UnannClassOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannClassType(Java8Parser.UnannClassTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannClassType_lf_unannClassOrInterfaceType(Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannClassType_lfno_unannClassOrInterfaceType(Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannInterfaceType(Java8Parser.UnannInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannInterfaceType_lf_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannInterfaceType_lfno_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannTypeVariable(Java8Parser.UnannTypeVariableContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitUnannArrayType(Java8Parser.UnannArrayTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodModifier(Java8Parser.MethodModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodHeader(Java8Parser.MethodHeaderContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitResult(Java8Parser.ResultContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFormalParameterList(Java8Parser.FormalParameterListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFormalParameters(Java8Parser.FormalParametersContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitVariableModifier(Java8Parser.VariableModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitLastFormalParameter(Java8Parser.LastFormalParameterContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitReceiverParameter(Java8Parser.ReceiverParameterContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitThrows_(Java8Parser.Throws_Context ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitExceptionTypeList(Java8Parser.ExceptionTypeListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitExceptionType(Java8Parser.ExceptionTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodBody(Java8Parser.MethodBodyContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInstanceInitializer(Java8Parser.InstanceInitializerContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitStaticInitializer(Java8Parser.StaticInitializerContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitConstructorModifier(Java8Parser.ConstructorModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitConstructorDeclarator(Java8Parser.ConstructorDeclaratorContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSimpleTypeName(Java8Parser.SimpleTypeNameContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitConstructorBody(Java8Parser.ConstructorBodyContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitExplicitConstructorInvocation(Java8Parser.ExplicitConstructorInvocationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnumBody(Java8Parser.EnumBodyContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnumConstantList(Java8Parser.EnumConstantListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnumConstant(Java8Parser.EnumConstantContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnumConstantModifier(Java8Parser.EnumConstantModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnumBodyDeclarations(Java8Parser.EnumBodyDeclarationsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceDeclaration(Java8Parser.InterfaceDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceModifier(Java8Parser.InterfaceModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitExtendsInterfaces(Java8Parser.ExtendsInterfacesContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceBody(Java8Parser.InterfaceBodyContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceMemberDeclaration(Java8Parser.InterfaceMemberDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitConstantModifier(Java8Parser.ConstantModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceMethodDeclaration(Java8Parser.InterfaceMethodDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInterfaceMethodModifier(Java8Parser.InterfaceMethodModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAnnotationTypeDeclaration(Java8Parser.AnnotationTypeDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAnnotationTypeBody(Java8Parser.AnnotationTypeBodyContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAnnotationTypeMemberDeclaration(Java8Parser.AnnotationTypeMemberDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAnnotationTypeElementDeclaration(Java8Parser.AnnotationTypeElementDeclarationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAnnotationTypeElementModifier(Java8Parser.AnnotationTypeElementModifierContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitDefaultValue(Java8Parser.DefaultValueContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAnnotation(Java8Parser.AnnotationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitNormalAnnotation(Java8Parser.NormalAnnotationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitElementValuePairList(Java8Parser.ElementValuePairListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitElementValuePair(Java8Parser.ElementValuePairContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitElementValue(Java8Parser.ElementValueContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitElementValueArrayInitializer(Java8Parser.ElementValueArrayInitializerContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitElementValueList(Java8Parser.ElementValueListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMarkerAnnotation(Java8Parser.MarkerAnnotationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSingleElementAnnotation(Java8Parser.SingleElementAnnotationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitArrayInitializer(Java8Parser.ArrayInitializerContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitVariableInitializerList(Java8Parser.VariableInitializerListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitBlockStatements(Java8Parser.BlockStatementsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitBlockStatement(Java8Parser.BlockStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitLocalVariableDeclarationStatement(Java8Parser.LocalVariableDeclarationStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitStatementNoShortIf(Java8Parser.StatementNoShortIfContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEmptyStatement(Java8Parser.EmptyStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitLabeledStatementNoShortIf(Java8Parser.LabeledStatementNoShortIfContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitExpressionStatement(Java8Parser.ExpressionStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitIfThenElseStatementNoShortIf(Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAssertStatement(Java8Parser.AssertStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSwitchStatement(Java8Parser.SwitchStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSwitchBlock(Java8Parser.SwitchBlockContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSwitchBlockStatementGroup(Java8Parser.SwitchBlockStatementGroupContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSwitchLabels(Java8Parser.SwitchLabelsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSwitchLabel(Java8Parser.SwitchLabelContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnumConstantName(Java8Parser.EnumConstantNameContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitDoStatement(Java8Parser.DoStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitForStatementNoShortIf(Java8Parser.ForStatementNoShortIfContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitBasicForStatement(Java8Parser.BasicForStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitBasicForStatementNoShortIf(Java8Parser.BasicForStatementNoShortIfContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitForInit(Java8Parser.ForInitContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitForUpdate(Java8Parser.ForUpdateContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitStatementExpressionList(Java8Parser.StatementExpressionListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitEnhancedForStatementNoShortIf(Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitBreakStatement(Java8Parser.BreakStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitContinueStatement(Java8Parser.ContinueStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitReturnStatement(Java8Parser.ReturnStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitThrowStatement(Java8Parser.ThrowStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitSynchronizedStatement(Java8Parser.SynchronizedStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTryStatement(Java8Parser.TryStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitCatches(Java8Parser.CatchesContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitCatchClause(Java8Parser.CatchClauseContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitCatchFormalParameter(Java8Parser.CatchFormalParameterContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitCatchType(Java8Parser.CatchTypeContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFinally_(Java8Parser.Finally_Context ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTryWithResourcesStatement(Java8Parser.TryWithResourcesStatementContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitResourceSpecification(Java8Parser.ResourceSpecificationContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitResourceList(Java8Parser.ResourceListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitResource(Java8Parser.ResourceContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimaryNoNewArray(Java8Parser.PrimaryNoNewArrayContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimaryNoNewArray_lf_arrayAccess(Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimaryNoNewArray_lfno_arrayAccess(Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimaryNoNewArray_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitTypeArgumentsOrDiamond(Java8Parser.TypeArgumentsOrDiamondContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFieldAccess(Java8Parser.FieldAccessContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFieldAccess_lf_primary(Java8Parser.FieldAccess_lf_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitFieldAccess_lfno_primary(Java8Parser.FieldAccess_lfno_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitArrayAccess(Java8Parser.ArrayAccessContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitArrayAccess_lf_primary(Java8Parser.ArrayAccess_lf_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitArgumentList(Java8Parser.ArgumentListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodReference(Java8Parser.MethodReferenceContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodReference_lf_primary(Java8Parser.MethodReference_lf_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitMethodReference_lfno_primary(Java8Parser.MethodReference_lfno_primaryContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitArrayCreationExpression(Java8Parser.ArrayCreationExpressionContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitDimExprs(Java8Parser.DimExprsContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitDimExpr(Java8Parser.DimExprContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitConstantExpression(Java8Parser.ConstantExpressionContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitLambdaExpression(Java8Parser.LambdaExpressionContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitLambdaParameters(Java8Parser.LambdaParametersContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitInferredFormalParameterList(Java8Parser.InferredFormalParameterListContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitLambdaBody(Java8Parser.LambdaBodyContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitLeftHandSide(Java8Parser.LeftHandSideContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitConditionalExpression(Java8Parser.ConditionalExpressionContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPostIncrementExpression_lf_postfixExpression(Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitPostDecrementExpression_lf_postfixExpression(Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AstNode visitCastExpression(Java8Parser.CastExpressionContext ctx) {
        throw new UnsupportedOperationException();
    }
}
