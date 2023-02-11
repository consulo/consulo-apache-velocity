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

package com.intellij.velocity.psi.directives;

import com.intellij.velocity.psi.VtlCompositeElement;
import com.intellij.velocity.psi.VtlElementTypes;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.impl.ast.TreeUtil;
import consulo.language.psi.AbstractElementManipulator;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceBase;
import consulo.language.util.IncorrectOperationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
public class VtlBreak extends VtlCompositeElement
{
	public VtlBreak(@Nonnull final ASTNode node)
	{
		super(node);
	}

	@Override
	@Nullable
	public PsiReference getReference()
	{
		final ASTNode foreachNode = findForeachNode();
		if(foreachNode == null)
		{
			return null;
		}
		return new PsiReferenceBase<PsiElement>(this)
		{
			public consulo.language.psi.PsiElement resolve()
			{
				return foreachNode.getLastChildNode().getPsi();
			}

			public Object[] getVariants()
			{
				return EMPTY_ARRAY;
			}
		};
	}

	@Nonnull
	public PsiReference[] getReferences()
	{
		final PsiReference ref = getReference();
		return ref == null ? PsiReference.EMPTY_ARRAY : new PsiReference[]{ref};
	}

	@Nullable
	public ASTNode findForeachNode()
	{
		return TreeUtil.findParent(getNode(), VtlElementTypes.DIRECTIVE_FOREACH);
	}

}
