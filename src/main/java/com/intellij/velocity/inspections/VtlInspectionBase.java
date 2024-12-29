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
package com.intellij.velocity.inspections;

import com.intellij.velocity.psi.VtlLanguage;
import consulo.language.Language;
import consulo.language.editor.inspection.LocalInspectionTool;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
public abstract class VtlInspectionBase extends LocalInspectionTool
{

	@Nonnull
	public PsiElementVisitor buildVisitor(@Nonnull final ProblemsHolder holder, final boolean isOnTheFly)
	{
		return new consulo.language.psi.PsiElementVisitor()
		{
			public void visitElement(final PsiElement element)
			{
				registerProblems(element, holder);
			}
		};
	}

	protected abstract void registerProblems(consulo.language.psi.PsiElement element, ProblemsHolder holder);

	@Nls
	@Nonnull
	public String getGroupDisplayName()
	{
		return "";
	}

	@Nullable
	@Override
	public Language getLanguage()
	{
		return VtlLanguage.INSTANCE;
	}

	@Nonnull
	public HighlightDisplayLevel getDefaultLevel()
	{
		return HighlightDisplayLevel.WARNING;
	}

	public boolean isEnabledByDefault()
	{
		return true;
	}

}