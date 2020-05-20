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

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;
import com.intellij.lang.ASTNode;
import com.intellij.lang.properties.references.PropertyReference;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.velocity.psi.directives.VtlDirective;
import com.intellij.velocity.psi.directives.VtlFileReferenceDirective;
import com.intellij.velocity.psi.directives.VtlMacroCall;
import consulo.velocity.api.psi.StandardVelocityType;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 07.06.2008
 */
public class VtlLiteralExpressionType extends VtlCompositeElementType
{
	private final StandardVelocityType myStandardType;

	public VtlLiteralExpressionType(@Nonnull @NonNls String debugName, @Nonnull StandardVelocityType standardType)
	{
		super(debugName);
		myStandardType = standardType;
	}

	@Override
	public PsiElement createPsiElement(ASTNode node)
	{
		return VtlCompositeElementTypes.STRING_LITERALS.contains(this)
				? new VtlStringLiteral(node)
				: new VtlLiteralExpression(node);
	}

	public class VtlLiteralExpression extends VtlCompositeElement implements VtlExpression
	{
		public VtlLiteralExpression(@Nonnull final ASTNode node)
		{
			super(node);
		}

		@Override
		public StandardVelocityType getPsiType()
		{
			return myStandardType;
		}
	}

	public class VtlStringLiteral extends VtlLiteralExpression
	{

		public VtlStringLiteral(@Nonnull final ASTNode node)
		{
			super(node);
		}

		@Override
		@Nonnull
		public PsiReference[] getReferences()
		{
			if(isFileReference())
			{
				return PsiUtil.getFileReferences(getValueText(), this, getFirstChild().getTextLength(), true);
			}
			if(isPropertyReference())
			{
				return new PsiReference[]{new PropertyReference(getValueText(), this, null, true)};
			}
			return PsiReference.EMPTY_ARRAY;
		}

		private boolean isFileReference()
		{
			return isStringLiteralAndArgumentOf(VtlFileReferenceDirective.class);
		}

		private boolean isPropertyReference()
		{
			if(!isStringLiteralAndArgumentOf(VtlMacroCall.class) || getPrevSibling() != null)
			{
				return false;
			}
			VtlMacroCall macroCall = (VtlMacroCall) getParent().getParent();
			String macroName = macroCall.getReferenceExpression().getReferenceName();
			return "springMessageText".equals(macroName) || "springMessage".equals(macroName)
					|| "springThemeText".equals(macroName) || "springTheme".equals(macroName);
		}

		private String getValueText()
		{
			String text = getText();
			return text.substring(1, text.length() - 1);
		}

		private boolean isStringLiteralAndArgumentOf(Class<? extends VtlDirective> directiveClass)
		{
			final PsiElement parent = getParent();
			return parent instanceof VtlArgumentList && directiveClass.isInstance(parent.getParent());
		}

		public TextRange getValueRange()
		{
			return new TextRange(1, getText().length() - 1);
		}

		public VtlStringLiteral setStringValue(final TextRange range, final String newContent)
		{
			String oldText = getText();
			String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
			final PsiElement newElement = PsiUtil.createStringLiteral(getProject(), newText);
			final ASTNode newNode = newElement.getNode();
			getParent().getNode().replaceChild(getNode(), newNode);
			return (VtlStringLiteral) newNode.getPsi();
		}
	}
}
