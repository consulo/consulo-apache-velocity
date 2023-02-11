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

import com.intellij.java.language.psi.*;
import com.intellij.java.language.psi.util.TypeConversionUtil;
import com.intellij.velocity.VelocityBundle;
import consulo.java.language.module.util.JavaClassNames;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.scope.GlobalSearchScope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
public class VtlLoopVariable extends VtlPresentableNamedElement implements VtlVariable
{
	public VtlLoopVariable(final ASTNode node)
	{
		super(node);
	}

	public String getTypeName()
	{
		return VelocityBundle.message("type.name.loop.variable");
	}

	public PsiType getPsiType()
	{
		return extractTypeFromIterable(getIterableExpression());
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

	@Nullable
	private static PsiType extractTypeFromIterable(VtlExpression expr)
	{
		if(expr == null)
		{
			return null;
		}
		PsiType type = expr.getPsiType();
		if(type == null)
		{
			return null;
		}
		if(type instanceof PsiArrayType)
		{
			return ((PsiArrayType) type).getComponentType();
		}
		if(!(type instanceof PsiClassType))
		{
			return null;
		}
		PsiClassType classType = (PsiClassType) type;
		PsiElementFactory factory = JavaPsiFacade.getInstance(expr.getProject()).getElementFactory();
		GlobalSearchScope scope = expr.getResolveScope();

		for(Object[] iterable : VELOCITY_ITERABLES)
		{
			PsiClassType iterableClassType = factory.createTypeByFQClassName((String) iterable[0], scope);
			if(!TypeConversionUtil.isAssignable(iterableClassType, classType))
			{
				continue;
			}
			final PsiClass iterableClass = iterableClassType.resolve();
			if(iterableClass == null)
			{
				continue;
			}
			final PsiSubstitutor substitutor = PsiUtil.getSuperClassSubstitutor(iterableClass, classType);
			PsiTypeParameter[] paremeters = iterableClass.getTypeParameters();
			int paramIndex = ((Integer) iterable[1]).intValue();
			PsiType result = paramIndex < paremeters.length ? substitutor.substitute(paremeters[paramIndex]) : null;
			return result != null ? result : factory.createTypeByFQClassName(JavaClassNames.JAVA_LANG_OBJECT, scope);
		}
		return null;
	}

	private static final Object[][] VELOCITY_ITERABLES = {
			{
					JavaClassNames.JAVA_UTIL_ITERATOR,
					0
			},
			{
					JavaClassNames.JAVA_UTIL_COLLECTION,
					0
			},
			{
					JavaClassNames.JAVA_UTIL_MAP,
					1
			}
	};

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