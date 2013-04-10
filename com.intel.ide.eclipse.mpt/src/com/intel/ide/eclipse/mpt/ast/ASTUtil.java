package com.intel.ide.eclipse.mpt.ast;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.corext.dom.ASTNodeFactory;

/**
 * Utitility class.
 * 
 */
public class ASTUtil {
	/**
	 * Hidden. Call the static methods.
	 */
	private ASTUtil() {
	}

	/**
	 * Finds the parent {@link Block} of a {@link Statement}.
	 * 
	 * @param s
	 *            the {@link Statement} to find the its parent {@link Block} for
	 * @return the parent block of {@code s}
	 */
	public static Block getParentBlock(Statement s) {
		ASTNode node = s;
		while (!(node instanceof Block)) {
			node = node.getParent();
		}
		return (Block) node;
	}

	/**
	 * Get the short (unqualified) class name from a fully qualified one.
	 * 
	 * @param cTypeFull
	 * @return 2
	 */
	public static String getShortClassName(String cTypeFull) {
		return cTypeFull.replaceAll("\\w*\\.", "");
	}

	/**
	 * 
	 * 
	 */
	public static MethodDeclaration generateStubMethodBody(AST ast, String methodName, Type returnType, int modifiers, List parameters) {

		MethodDeclaration md = ast.newMethodDeclaration();
        md.setName(ast.newSimpleName(methodName));
        md.setConstructor(false);
         
        md.modifiers().addAll(ASTNodeFactory.newModifiers(ast, modifiers));
        if (returnType.isPrimitiveType()) {
        	PrimitiveType primitiveType = (PrimitiveType)returnType;
        	md.setReturnType2(ast.newPrimitiveType(primitiveType.getPrimitiveTypeCode()));
        } else {
        	md.setReturnType2(returnType);
        }
        
        // ARGUMENTS
        List mdParameters = md.parameters();
 		for (int i = 0; i < parameters.size(); i++) {
 			SingleVariableDeclaration hashCodeParam= ast.newSingleVariableDeclaration();
 			SingleVariableDeclaration para = (SingleVariableDeclaration) parameters.get(i);
 			
 			if (para.getType().isPrimitiveType()) {
 				PrimitiveType primitiveType = (PrimitiveType)para.getType();
 				hashCodeParam.setType(ast.newPrimitiveType(primitiveType.getPrimitiveTypeCode()));
 			} else {
 				hashCodeParam.setType(para.getType());
 			}
 			hashCodeParam.setName(ast.newSimpleName(para.getName().toString()));
 			mdParameters.add(hashCodeParam);
 		}
 		
 		// Stub method Body
        Block block = ast.newBlock();
		
		ImportDeclaration importDeclaration = ast.newImportDeclaration();
		QualifiedName name = ast.newQualifiedName(ast.newSimpleName("java"),
				ast.newSimpleName("util"));
		importDeclaration.setName(name);
		importDeclaration.setOnDemand(true);

		MethodInvocation methodInvocation = ast.newMethodInvocation();
		name = ast.newQualifiedName(ast.newSimpleName("System"),
				ast.newSimpleName("out"));
		methodInvocation.setExpression(name);
		methodInvocation.setName(ast.newSimpleName("println"));
		InfixExpression infixExpression = ast.newInfixExpression();
		infixExpression.setOperator(InfixExpression.Operator.PLUS);
		StringLiteral literal = ast.newStringLiteral();
		literal.setLiteralValue("Stub");
		infixExpression.setLeftOperand(literal);
		literal = ast.newStringLiteral();
		literal.setLiteralValue(" Function : " + methodName);
		infixExpression.setRightOperand(literal);
		methodInvocation.arguments().add(infixExpression);
		ExpressionStatement expressionStatement = ast
				.newExpressionStatement(methodInvocation);
		block.statements().add(expressionStatement);
		
		// Stub method END Return
		ReturnStatement endReturn= ast.newReturnStatement();
		if (returnType.isPrimitiveType()) {
        	PrimitiveType primitiveType = (PrimitiveType)returnType;
        	if (primitiveType.getPrimitiveTypeCode().equals(PrimitiveType.VOID)) {
        		endReturn.setExpression(null);
        	} else {
        		endReturn.setExpression(ast.newNumberLiteral("0"));
        	}
        }
		
		block.statements().add(endReturn);
		
		md.setBody(block);

		return md;
	}

}
