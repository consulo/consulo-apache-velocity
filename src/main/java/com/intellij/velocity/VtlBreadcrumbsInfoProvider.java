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

package com.intellij.velocity;

import consulo.annotation.component.ExtensionImpl;
import consulo.ide.navigationToolbar.StructureAwareNavBarModelExtension;
import consulo.language.Language;
import consulo.language.psi.PsiElement;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.directives.VtlDirective;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.util.PsiTreeUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlBreadcrumbsInfoProvider extends StructureAwareNavBarModelExtension
{
	@Nonnull
	@Override
	public Language getLanguage()
	{
		return VtlLanguage.INSTANCE;
	}

	@Override
	protected boolean acceptParentFromModel(PsiElement psiElement)
	{
		return psiElement instanceof VtlDirective;
	}

	@Nullable
	@Override
	public String getPresentableText(Object o)
	{
		if(o instanceof VtlDirective vtlDirective)
		{
			return vtlDirective.getPresentableName();
		}
		return null;
	}

	@Override
	@RequiredReadAction
	public consulo.language.psi.PsiElement getParent(@Nonnull final consulo.language.psi.PsiElement e)
	{
		VtlDirective directive = PsiTreeUtil.getParentOfType(e, VtlDirective.class);
		return directive instanceof VtlFile ? null : directive;
	}
}
