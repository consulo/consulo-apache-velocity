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
import javax.annotation.Nullable;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.velocity.VelocityBundle;
import consulo.annotation.access.RequiredReadAction;
import consulo.velocity.api.facade.VelocityFacade;
import consulo.velocity.api.facade.VelocityType;

/**
 * @author Alexey Chmutov
 */
public class VtlLoopVariable extends VtlPresentableNamedElement implements VtlVariable
{
	public VtlLoopVariable(final ASTNode node)
	{
		super(node);
	}

	@Override
	public String getTypeName()
	{
		return VelocityBundle.message("type.name.loop.variable");
	}

	@RequiredReadAction
	@Override
	public VelocityType getPsiType()
	{
		return VelocityFacade.getFacade(getIterableExpression()).extractTypeFromIterable(getIterableExpression());
	}

	@Nullable
	public VtlExpression getIterableExpression()
	{
		PsiElement wouldBeIterable = getNextSibling();
		while(wouldBeIterable != null)
		{
			if(wouldBeIterable instanceof VtlExpression)
			{
				return (VtlExpression) wouldBeIterable;
			}
			wouldBeIterable = wouldBeIterable.getNextSibling();
		}
		return null;
	}

	public static String[] getVelocityIterables(@Nonnull String className)
	{
		return new String[]{
				"java.util.Iterator<" + className + ">",
				"java.util.Collection<" + className + ">",
				"java.util.Map<?, " + className + ">",
				className + "[]",
		};
	}
}