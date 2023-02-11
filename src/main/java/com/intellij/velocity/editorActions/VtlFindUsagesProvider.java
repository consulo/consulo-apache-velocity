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
package com.intellij.velocity.editorActions;

import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.VtlVariable;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.findUsage.FindUsagesProvider;
import consulo.language.psi.PsiElement;

import javax.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlFindUsagesProvider implements FindUsagesProvider
{
	public boolean canFindUsagesFor(@Nonnull final consulo.language.psi.PsiElement psiElement)
	{
		return psiElement instanceof VtlVariable;
	}

	@Nonnull
	public String getType(@Nonnull final PsiElement element)
	{
		return VelocityBundle.message("type.name.variable");
	}

	@Nonnull
	public String getDescriptiveName(@Nonnull final consulo.language.psi.PsiElement element)
	{
		return VelocityBundle.message("type.name.variable");
	}

	@Nonnull
	public String getNodeText(@Nonnull final PsiElement element, final boolean useFullName)
	{
		if(element instanceof VtlVariable)
		{
			return ((VtlVariable) element).getName();
		}
		return element.getText();
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return VtlLanguage.INSTANCE;
	}
}
