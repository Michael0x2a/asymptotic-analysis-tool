package visitors;

import grammar.Java8BaseVisitor;
import grammar.Java8Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

public class PrettyPrintVisitor extends Java8BaseVisitor<String> {
    private Java8Parser parser;

    public PrettyPrintVisitor(Java8Parser parser) {
        this.parser = parser;
    }

    private String indent(StringBuilder build) {
        String out = "";
        for (String line : build.toString().split("\n")) {
            out += "    " + line + "\n";
        }
        return out;
    }

    private String prettyPrint(ParserRuleContext ctx) {
        if (ctx.children.isEmpty()) {
            return ctx.getText();
        } else {
            StringBuilder build = new StringBuilder();
            for (ParseTree child : ctx.children) {
                build.append(child.accept(this));
                build.append("\n");
            }
            String rule = parser.getRuleNames()[ctx.getRuleIndex()];
            return "(" + rule + "\n" + this.indent(build) + ")";
        }
    }

    @Override
    public String visitLiteral(Java8Parser.LiteralContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitType(Java8Parser.TypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimitiveType(Java8Parser.PrimitiveTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitNumericType(Java8Parser.NumericTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitIntegralType(Java8Parser.IntegralTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFloatingPointType(Java8Parser.FloatingPointTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitReferenceType(Java8Parser.ReferenceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassOrInterfaceType(Java8Parser.ClassOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassType(Java8Parser.ClassTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassType_lf_classOrInterfaceType(Java8Parser.ClassType_lf_classOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassType_lfno_classOrInterfaceType(Java8Parser.ClassType_lfno_classOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceType(Java8Parser.InterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceType_lf_classOrInterfaceType(Java8Parser.InterfaceType_lf_classOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceType_lfno_classOrInterfaceType(Java8Parser.InterfaceType_lfno_classOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeVariable(Java8Parser.TypeVariableContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitArrayType(Java8Parser.ArrayTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitDims(Java8Parser.DimsContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeParameter(Java8Parser.TypeParameterContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeParameterModifier(Java8Parser.TypeParameterModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeBound(Java8Parser.TypeBoundContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAdditionalBound(Java8Parser.AdditionalBoundContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeArguments(Java8Parser.TypeArgumentsContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeArgumentList(Java8Parser.TypeArgumentListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeArgument(Java8Parser.TypeArgumentContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitWildcard(Java8Parser.WildcardContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitWildcardBounds(Java8Parser.WildcardBoundsContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPackageName(Java8Parser.PackageNameContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeName(Java8Parser.TypeNameContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPackageOrTypeName(Java8Parser.PackageOrTypeNameContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitExpressionName(Java8Parser.ExpressionNameContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodName(Java8Parser.MethodNameContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAmbiguousName(Java8Parser.AmbiguousNameContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitCompilationUnit(Java8Parser.CompilationUnitContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPackageDeclaration(Java8Parser.PackageDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPackageModifier(Java8Parser.PackageModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitImportDeclaration(Java8Parser.ImportDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSingleTypeImportDeclaration(Java8Parser.SingleTypeImportDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeImportOnDemandDeclaration(Java8Parser.TypeImportOnDemandDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSingleStaticImportDeclaration(Java8Parser.SingleStaticImportDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitStaticImportOnDemandDeclaration(Java8Parser.StaticImportOnDemandDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeDeclaration(Java8Parser.TypeDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassDeclaration(Java8Parser.ClassDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitNormalClassDeclaration(Java8Parser.NormalClassDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassModifier(Java8Parser.ClassModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeParameters(Java8Parser.TypeParametersContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeParameterList(Java8Parser.TypeParameterListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSuperclass(Java8Parser.SuperclassContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSuperinterfaces(Java8Parser.SuperinterfacesContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceTypeList(Java8Parser.InterfaceTypeListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassBody(Java8Parser.ClassBodyContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassBodyDeclaration(Java8Parser.ClassBodyDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassMemberDeclaration(Java8Parser.ClassMemberDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFieldDeclaration(Java8Parser.FieldDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFieldModifier(Java8Parser.FieldModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitVariableDeclaratorList(Java8Parser.VariableDeclaratorListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitVariableDeclarator(Java8Parser.VariableDeclaratorContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitVariableDeclaratorId(Java8Parser.VariableDeclaratorIdContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitVariableInitializer(Java8Parser.VariableInitializerContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannType(Java8Parser.UnannTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannPrimitiveType(Java8Parser.UnannPrimitiveTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannReferenceType(Java8Parser.UnannReferenceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannClassOrInterfaceType(Java8Parser.UnannClassOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannClassType(Java8Parser.UnannClassTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannClassType_lf_unannClassOrInterfaceType(Java8Parser.UnannClassType_lf_unannClassOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannClassType_lfno_unannClassOrInterfaceType(Java8Parser.UnannClassType_lfno_unannClassOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannInterfaceType(Java8Parser.UnannInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannInterfaceType_lf_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lf_unannClassOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannInterfaceType_lfno_unannClassOrInterfaceType(Java8Parser.UnannInterfaceType_lfno_unannClassOrInterfaceTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannTypeVariable(Java8Parser.UnannTypeVariableContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnannArrayType(Java8Parser.UnannArrayTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodDeclaration(Java8Parser.MethodDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodModifier(Java8Parser.MethodModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodHeader(Java8Parser.MethodHeaderContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitResult(Java8Parser.ResultContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodDeclarator(Java8Parser.MethodDeclaratorContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFormalParameterList(Java8Parser.FormalParameterListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFormalParameters(Java8Parser.FormalParametersContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFormalParameter(Java8Parser.FormalParameterContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitVariableModifier(Java8Parser.VariableModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLastFormalParameter(Java8Parser.LastFormalParameterContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitReceiverParameter(Java8Parser.ReceiverParameterContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitThrows_(Java8Parser.Throws_Context ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitExceptionTypeList(Java8Parser.ExceptionTypeListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitExceptionType(Java8Parser.ExceptionTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodBody(Java8Parser.MethodBodyContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInstanceInitializer(Java8Parser.InstanceInitializerContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitStaticInitializer(Java8Parser.StaticInitializerContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConstructorDeclaration(Java8Parser.ConstructorDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConstructorModifier(Java8Parser.ConstructorModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConstructorDeclarator(Java8Parser.ConstructorDeclaratorContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSimpleTypeName(Java8Parser.SimpleTypeNameContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConstructorBody(Java8Parser.ConstructorBodyContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitExplicitConstructorInvocation(Java8Parser.ExplicitConstructorInvocationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnumDeclaration(Java8Parser.EnumDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnumBody(Java8Parser.EnumBodyContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnumConstantList(Java8Parser.EnumConstantListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnumConstant(Java8Parser.EnumConstantContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnumConstantModifier(Java8Parser.EnumConstantModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnumBodyDeclarations(Java8Parser.EnumBodyDeclarationsContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceDeclaration(Java8Parser.InterfaceDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitNormalInterfaceDeclaration(Java8Parser.NormalInterfaceDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceModifier(Java8Parser.InterfaceModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitExtendsInterfaces(Java8Parser.ExtendsInterfacesContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceBody(Java8Parser.InterfaceBodyContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceMemberDeclaration(Java8Parser.InterfaceMemberDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConstantDeclaration(Java8Parser.ConstantDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConstantModifier(Java8Parser.ConstantModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceMethodDeclaration(Java8Parser.InterfaceMethodDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInterfaceMethodModifier(Java8Parser.InterfaceMethodModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAnnotationTypeDeclaration(Java8Parser.AnnotationTypeDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAnnotationTypeBody(Java8Parser.AnnotationTypeBodyContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAnnotationTypeMemberDeclaration(Java8Parser.AnnotationTypeMemberDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAnnotationTypeElementDeclaration(Java8Parser.AnnotationTypeElementDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAnnotationTypeElementModifier(Java8Parser.AnnotationTypeElementModifierContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitDefaultValue(Java8Parser.DefaultValueContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAnnotation(Java8Parser.AnnotationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitNormalAnnotation(Java8Parser.NormalAnnotationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitElementValuePairList(Java8Parser.ElementValuePairListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitElementValuePair(Java8Parser.ElementValuePairContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitElementValue(Java8Parser.ElementValueContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitElementValueArrayInitializer(Java8Parser.ElementValueArrayInitializerContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitElementValueList(Java8Parser.ElementValueListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMarkerAnnotation(Java8Parser.MarkerAnnotationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSingleElementAnnotation(Java8Parser.SingleElementAnnotationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitArrayInitializer(Java8Parser.ArrayInitializerContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitVariableInitializerList(Java8Parser.VariableInitializerListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitBlock(Java8Parser.BlockContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitBlockStatements(Java8Parser.BlockStatementsContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitBlockStatement(Java8Parser.BlockStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLocalVariableDeclarationStatement(Java8Parser.LocalVariableDeclarationStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLocalVariableDeclaration(Java8Parser.LocalVariableDeclarationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitStatement(Java8Parser.StatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitStatementNoShortIf(Java8Parser.StatementNoShortIfContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitStatementWithoutTrailingSubstatement(Java8Parser.StatementWithoutTrailingSubstatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEmptyStatement(Java8Parser.EmptyStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLabeledStatement(Java8Parser.LabeledStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLabeledStatementNoShortIf(Java8Parser.LabeledStatementNoShortIfContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitExpressionStatement(Java8Parser.ExpressionStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitStatementExpression(Java8Parser.StatementExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitIfThenStatement(Java8Parser.IfThenStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitIfThenElseStatement(Java8Parser.IfThenElseStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitIfThenElseStatementNoShortIf(Java8Parser.IfThenElseStatementNoShortIfContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAssertStatement(Java8Parser.AssertStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSwitchStatement(Java8Parser.SwitchStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSwitchBlock(Java8Parser.SwitchBlockContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSwitchBlockStatementGroup(Java8Parser.SwitchBlockStatementGroupContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSwitchLabels(Java8Parser.SwitchLabelsContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSwitchLabel(Java8Parser.SwitchLabelContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnumConstantName(Java8Parser.EnumConstantNameContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitWhileStatement(Java8Parser.WhileStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitWhileStatementNoShortIf(Java8Parser.WhileStatementNoShortIfContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitDoStatement(Java8Parser.DoStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitForStatement(Java8Parser.ForStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitForStatementNoShortIf(Java8Parser.ForStatementNoShortIfContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitBasicForStatement(Java8Parser.BasicForStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitBasicForStatementNoShortIf(Java8Parser.BasicForStatementNoShortIfContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitForInit(Java8Parser.ForInitContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitForUpdate(Java8Parser.ForUpdateContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitStatementExpressionList(Java8Parser.StatementExpressionListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnhancedForStatement(Java8Parser.EnhancedForStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEnhancedForStatementNoShortIf(Java8Parser.EnhancedForStatementNoShortIfContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitBreakStatement(Java8Parser.BreakStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitContinueStatement(Java8Parser.ContinueStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitReturnStatement(Java8Parser.ReturnStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitThrowStatement(Java8Parser.ThrowStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitSynchronizedStatement(Java8Parser.SynchronizedStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTryStatement(Java8Parser.TryStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitCatches(Java8Parser.CatchesContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitCatchClause(Java8Parser.CatchClauseContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitCatchFormalParameter(Java8Parser.CatchFormalParameterContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitCatchType(Java8Parser.CatchTypeContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFinally_(Java8Parser.Finally_Context ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTryWithResourcesStatement(Java8Parser.TryWithResourcesStatementContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitResourceSpecification(Java8Parser.ResourceSpecificationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitResourceList(Java8Parser.ResourceListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitResource(Java8Parser.ResourceContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimary(Java8Parser.PrimaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray(Java8Parser.PrimaryNoNewArrayContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray_lf_arrayAccess(Java8Parser.PrimaryNoNewArray_lf_arrayAccessContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray_lfno_arrayAccess(Java8Parser.PrimaryNoNewArray_lfno_arrayAccessContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lf_arrayAccess_lf_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primary(Java8Parser.PrimaryNoNewArray_lf_primary_lfno_arrayAccess_lf_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lf_arrayAccess_lfno_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primary(Java8Parser.PrimaryNoNewArray_lfno_primary_lfno_arrayAccess_lfno_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassInstanceCreationExpression(Java8Parser.ClassInstanceCreationExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassInstanceCreationExpression_lf_primary(Java8Parser.ClassInstanceCreationExpression_lf_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitClassInstanceCreationExpression_lfno_primary(Java8Parser.ClassInstanceCreationExpression_lfno_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTypeArgumentsOrDiamond(Java8Parser.TypeArgumentsOrDiamondContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFieldAccess(Java8Parser.FieldAccessContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFieldAccess_lf_primary(Java8Parser.FieldAccess_lf_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitFieldAccess_lfno_primary(Java8Parser.FieldAccess_lfno_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitArrayAccess(Java8Parser.ArrayAccessContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitArrayAccess_lf_primary(Java8Parser.ArrayAccess_lf_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitArrayAccess_lfno_primary(Java8Parser.ArrayAccess_lfno_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodInvocation(Java8Parser.MethodInvocationContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodInvocation_lf_primary(Java8Parser.MethodInvocation_lf_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodInvocation_lfno_primary(Java8Parser.MethodInvocation_lfno_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitArgumentList(Java8Parser.ArgumentListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodReference(Java8Parser.MethodReferenceContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodReference_lf_primary(Java8Parser.MethodReference_lf_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMethodReference_lfno_primary(Java8Parser.MethodReference_lfno_primaryContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitArrayCreationExpression(Java8Parser.ArrayCreationExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitDimExprs(Java8Parser.DimExprsContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitDimExpr(Java8Parser.DimExprContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConstantExpression(Java8Parser.ConstantExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitExpression(Java8Parser.ExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLambdaExpression(Java8Parser.LambdaExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLambdaParameters(Java8Parser.LambdaParametersContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInferredFormalParameterList(Java8Parser.InferredFormalParameterListContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLambdaBody(Java8Parser.LambdaBodyContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAssignmentExpression(Java8Parser.AssignmentExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAssignment(Java8Parser.AssignmentContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitLeftHandSide(Java8Parser.LeftHandSideContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAssignmentOperator(Java8Parser.AssignmentOperatorContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConditionalExpression(Java8Parser.ConditionalExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConditionalOrExpression(Java8Parser.ConditionalOrExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitConditionalAndExpression(Java8Parser.ConditionalAndExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitInclusiveOrExpression(Java8Parser.InclusiveOrExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitExclusiveOrExpression(Java8Parser.ExclusiveOrExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAndExpression(Java8Parser.AndExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitEqualityExpression(Java8Parser.EqualityExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitRelationalExpression(Java8Parser.RelationalExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitShiftExpression(Java8Parser.ShiftExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitAdditiveExpression(Java8Parser.AdditiveExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitMultiplicativeExpression(Java8Parser.MultiplicativeExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnaryExpression(Java8Parser.UnaryExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPreIncrementExpression(Java8Parser.PreIncrementExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPreDecrementExpression(Java8Parser.PreDecrementExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitUnaryExpressionNotPlusMinus(Java8Parser.UnaryExpressionNotPlusMinusContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPostfixExpression(Java8Parser.PostfixExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPostIncrementExpression(Java8Parser.PostIncrementExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPostIncrementExpression_lf_postfixExpression(Java8Parser.PostIncrementExpression_lf_postfixExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPostDecrementExpression(Java8Parser.PostDecrementExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitPostDecrementExpression_lf_postfixExpression(Java8Parser.PostDecrementExpression_lf_postfixExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitCastExpression(Java8Parser.CastExpressionContext ctx) {
        return this.prettyPrint(ctx);
    }

    @Override
    public String visitTerminal(TerminalNode node) {
        return node.toString();
    }
}
