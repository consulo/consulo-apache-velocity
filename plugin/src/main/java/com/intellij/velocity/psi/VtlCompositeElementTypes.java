/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.velocity.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.velocity.psi.directives.VtlBreak;
import com.intellij.velocity.psi.directives.VtlDefine;
import com.intellij.velocity.psi.directives.VtlFileReferenceDirective;
import com.intellij.velocity.psi.directives.VtlForeach;
import com.intellij.velocity.psi.directives.VtlMacroCall;
import com.intellij.velocity.psi.directives.VtlMacroImpl;
import com.intellij.velocity.psi.directives.VtlParse;
import com.intellij.velocity.psi.directives.VtlSet;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.velocity.api.psi.StandardVelocityType;

/**
 * @author Alexey Chmutov
 */
public interface VtlCompositeElementTypes
{

	IFileElementType VTL_FILE = new IFileElementType("VTL_FILE", VtlLanguage.INSTANCE);

	VtlCompositeElementType DIRECTIVE_ELSE = new VtlDirectiveType("DirectiveElse", "else", false);
	VtlCompositeElementType DIRECTIVE_ELSEIF = new VtlDirectiveType("DirectiveElseif", "elseif", false);
	VtlCompositeElementType DIRECTIVE_FOREACH = new VtlDirectiveType("DirectiveForeach")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlForeach(node);
		}
	};
	VtlCompositeElementType DIRECTIVE_BREAK = new VtlCompositeElementType("DirectiveBreak")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlBreak(node);
		}
	};
	VtlCompositeElementType DIRECTIVE_IF = new VtlDirectiveType("DirectiveIf", "if", true);
	VtlCompositeElementType DIRECTIVE_EVALUATE = new VtlDirectiveType("DirectiveEvaluate", "evaluate", false);
	VtlCompositeElementType DIRECTIVE_DEFINE = new VtlDirectiveType("DirectiveDefine")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlDefine(node);
		}
	};
	VtlCompositeElementType DIRECTIVE_LITERAL = new VtlDirectiveType("DirectiveLiteral", "literal", true);
	VtlCompositeElementType DIRECTIVE_MACROCALL = new VtlDirectiveType("DirectiveMacroCall")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlMacroCall(node);
		}
	};
	VtlCompositeElementType DIRECTIVE_MACRODECL = new VtlDirectiveType("DirectiveMacroDecl")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlMacroImpl(node);
		}
	};
	VtlCompositeElementType DIRECTIVE_INCLUDE = new VtlDirectiveType("DirectiveInclude")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlFileReferenceDirective(node, "include");
		}
	};
	VtlCompositeElementType DIRECTIVE_PARSE = new VtlDirectiveType("DirectiveParse")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlParse(node);
		}
	};
	VtlCompositeElementType DIRECTIVE_SET = new VtlDirectiveType("DirectiveSet")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlSet(node);
		}
	};
	VtlCompositeElementType DIR_HEADER = new VtlCompositeElementType("HeaderOfDirective")
	{
		@Override
		public PsiElement createPsiElement(final ASTNode node)
		{
			return new VtlDirectiveHeader(node);
		}
	};

	VtlCompositeElementType INTERPOLATION = new VtlExpressionType("Interpolation")
	{
		@Override
		public PsiElement createPsiElement(final ASTNode node)
		{
			return new VtlInterpolation(node);
		}
	};

	VtlCompositeElementType REFERENCE_EXPRESSION = new VtlCompositeElementType("ReferenceExpression")
	{
		@Override
		public PsiElement createPsiElement(final ASTNode node)
		{
			return new VtlReferenceExpression(node);
		}
	};
	VtlCompositeElementType METHOD_CALL_EXPRESSION = new VtlCompositeElementType("MethodCallExpression")
	{
		@Override
		public PsiElement createPsiElement(final ASTNode node)
		{
			return new VtlMethodCallExpression(node);
		}
	};
	VtlCompositeElementType PARENTHESIZED_EXPRESSION = new VtlExpressionType("ParenthesizedExpression");
	VtlCompositeElementType ARGUMENT_LIST = new VtlCompositeElementType("ArgumentList")
	{
		@Override
		public PsiElement createPsiElement(final ASTNode node)
		{
			return new VtlArgumentList(node);
		}
	};
	VtlCompositeElementType PARAMETER_LIST = new VtlCompositeElementType("ParameterList");
	VtlCompositeElementType PARAMETER = new VtlCompositeElementType("Parameter")
	{
		@Override
		public PsiElement createPsiElement(final ASTNode node)
		{
			return new VtlParameterDeclaration(node);
		}
	};
	VtlCompositeElementType LOOP_VARIABLE = new VtlCompositeElementType("LoopVariable")
	{
		@Override
		public PsiElement createPsiElement(final ASTNode node)
		{
			return new VtlLoopVariable(node);
		}
	};
	VtlCompositeElementType UNARY_EXPRESSION = new VtlCompositeElementType("UnaryExpression")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlOperatorExpression(node, false);
		}
	};
	VtlCompositeElementType BINARY_EXPRESSION = new VtlCompositeElementType("BinaryExpression")
	{
		@Override
		public PsiElement createPsiElement(ASTNode node)
		{
			return new VtlOperatorExpression(node, true);
		}
	};

	VtlCompositeElementType INTEGER_LITERAL = new VtlLiteralExpressionType("IntegerLiteral", StandardVelocityType.INT);
	VtlCompositeElementType DOUBLE_LITERAL = new VtlLiteralExpressionType("DoubleLiteral", StandardVelocityType.DOUBLE);
	VtlCompositeElementType BOOLEAN_LITERAL = new VtlLiteralExpressionType("BooleanLiteral", StandardVelocityType.BOOLEAN);
	VtlCompositeElementType DOUBLEQUOTED_TEXT = new VtlLiteralExpressionType("DoubleQuotedText", StandardVelocityType.STRING);
	VtlCompositeElementType STRING_LITERAL = new VtlLiteralExpressionType("StringLiteral", StandardVelocityType.STRING);
	VtlCompositeElementType LIST_EXPRESSION = new VtlLiteralExpressionType("ListExpression", StandardVelocityType.LIST);
	VtlCompositeElementType RANGE_EXPRESSION = new VtlCompositeElementType("RangeExpression");
	VtlCompositeElementType MAP_EXPRESSION = new VtlLiteralExpressionType("MapExpression", StandardVelocityType.MAP);

	TokenSet STRING_LITERALS = TokenSet.create(STRING_LITERAL, DOUBLEQUOTED_TEXT);
}