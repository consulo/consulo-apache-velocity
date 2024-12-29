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

import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiType;
import com.intellij.java.language.psi.util.PsiUtil;
import com.intellij.velocity.psi.VtlImplicitVariable;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.language.editor.action.TypeDeclarationProvider;
import consulo.language.psi.PsiElement;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlTypeDeclarationProvider extends TypeDeclarationProvider
{
	@RequiredReadAction
	@Nullable
	@Override
	public consulo.language.psi.PsiElement[] getSymbolTypeDeclarations(@Nonnull PsiElement symbol, @Nullable Editor editor, int offset)
	{
		if(symbol instanceof VtlImplicitVariable)
		{
			PsiType type = ((VtlImplicitVariable) symbol).getPsiType();
			PsiClass psiClass = PsiUtil.resolveClassInType(type);
			return psiClass == null ? null : new consulo.language.psi.PsiElement[]{psiClass};
		}
		return null;
	}
}
