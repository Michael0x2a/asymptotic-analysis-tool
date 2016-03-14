package visitors;

import grammar.Java8BaseVisitor;
import grammar.Java8Parser;
import simplegrammar.*;

import java.util.ArrayList;
import java.util.Arrays;
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

        return new MethodDecl(returnType, methodName, parameters, Arrays.asList(
                new Literal(bodyCtx.toStringTree(this.parser))));
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
}
