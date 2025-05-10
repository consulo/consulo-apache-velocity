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
package com.intellij.velocity.psi.reference;

import com.intellij.java.language.psi.*;
import com.intellij.java.language.psi.search.searches.SuperMethodsSearch;
import com.intellij.java.language.psi.util.MethodSignature;
import com.intellij.java.language.psi.util.MethodSignatureBackedByPsiMethod;
import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.velocity.psi.reference.VelocityNamingUtil.isPropertyGetter;
import static com.intellij.velocity.psi.reference.VelocityNamingUtil.isPropertySetter;
import static consulo.util.lang.StringUtil.toLowerCase;

/**
 * @author Alexey Chmutov
 */
public class VelocityStylePropertyResolveHelper
{

	private enum PropertyAccessorMatchLevel
	{
		NotAccessor, NameMismatch, GetByObjectMethod, GetByStringMethod, OnlyFirstCharCaseMismatch, ExactNameMatch
	}

	private PropertyAccessorMatchLevel myMatchLevel = PropertyAccessorMatchLevel.NameMismatch;
	private final Map<MethodSignature, PsiMethod> myMethods = new HashMap<MethodSignature, PsiMethod>();
	private final boolean mySetterNeeded;
	@Nullable
	private final String myReferenceNameWithoutFirstChar;
	private final char myReferenceNameFirstChar;

	public VelocityStylePropertyResolveHelper(@Nullable String referenceName, boolean setterNeeded)
	{
		mySetterNeeded = setterNeeded;
		if(referenceName == null)
		{
			myReferenceNameWithoutFirstChar = null;
			myReferenceNameFirstChar = 0;
			myMatchLevel = PropertyAccessorMatchLevel.NameMismatch;
		}
		else
		{
			myReferenceNameWithoutFirstChar = referenceName.substring(1);
			myReferenceNameFirstChar = referenceName.charAt(0);
			myMatchLevel = PropertyAccessorMatchLevel.GetByObjectMethod;
		}
	}

	public boolean checkAndAddMethod(PsiMethod method, PsiSubstitutor substitutor)
	{
		PropertyAccessorMatchLevel matchLevel = getPropertyAccessorMatchLevel(method);
		if(matchLevel == PropertyAccessorMatchLevel.NotAccessor)
		{
			return false;
		}
		final MethodSignature signature = method.getSignature(substitutor);
		final PsiMethod alreadyStoredMethod = myMethods.get(signature);

		if(alreadyStoredMethod != null)
		{
			for(final MethodSignatureBackedByPsiMethod methodSignature : SuperMethodsSearch.search(alreadyStoredMethod,
					alreadyStoredMethod.getContainingClass(), true, false).findAll())
			{
				if(methodSignature.equals(signature))
				{
					return true;
				}
			}
		}
		if(matchLevel.compareTo(myMatchLevel) > 0)
		{
			myMethods.clear();
			myMethods.put(signature, method);
			myMatchLevel = matchLevel;
		}
		else if(matchLevel == myMatchLevel)
		{
			myMethods.put(signature, method);
		}
		return true;
	}

	@SuppressWarnings("HardCodedStringLiteral")
	private PropertyAccessorMatchLevel getPropertyAccessorMatchLevel(PsiMethod method)
	{
		String propertyName;
		String methodName = method.getName();
		if(mySetterNeeded)
		{
			if(!isPropertySetter(method))
			{
				return PropertyAccessorMatchLevel.NotAccessor;
			}
			propertyName = methodName.substring("set".length());
		}
		else
		{
			if(myReferenceNameWithoutFirstChar != null && "get".equals(methodName))
			{
				PsiParameterList parameterList = method.getParameterList();
				if(parameterList.getParametersCount() != 1)
				{
					return PropertyAccessorMatchLevel.NotAccessor;
				}
				PsiParameter parameter = parameterList.getParameters()[0];
				final PsiElementFactory factory = JavaPsiFacade.getInstance(method.getProject()).getElementFactory();
				PsiClassType javaLangObject = factory.createTypeByFQClassName(CommonClassNames.JAVA_LANG_OBJECT, method.getResolveScope());
				if(parameter.getType().isAssignableFrom(javaLangObject))
				{
					return PropertyAccessorMatchLevel.GetByObjectMethod;
				}
				PsiClassType javaLangString = factory.createTypeByFQClassName(CommonClassNames.JAVA_LANG_STRING, method.getResolveScope());
				if(parameter.getType().isAssignableFrom(javaLangString))
				{
					return PropertyAccessorMatchLevel.GetByStringMethod;
				}
				return PropertyAccessorMatchLevel.NotAccessor;
			}
			if(!isPropertyGetter(method))
			{
				return PropertyAccessorMatchLevel.NotAccessor;
			}
			int prefixLength = methodName.startsWith("get") ? "get".length() : "is".length();
			propertyName = methodName.substring(prefixLength);
		}

		if(myReferenceNameWithoutFirstChar != null && myReferenceNameWithoutFirstChar.equals(propertyName.substring(1)))
		{
			if(myReferenceNameFirstChar == propertyName.charAt(0))
			{
				return PropertyAccessorMatchLevel.ExactNameMatch;
			}
			else if(toLowerCase(myReferenceNameFirstChar) == toLowerCase(propertyName.charAt(0)))
			{
				return PropertyAccessorMatchLevel.OnlyFirstCharCaseMismatch;
			}
		}
		return PropertyAccessorMatchLevel.NameMismatch;
	}

	public Collection<PsiMethod> getMethods()
	{
		return myMethods.values();
	}
}
