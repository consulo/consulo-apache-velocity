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

import consulo.language.ast.ASTNode;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.language.psi.meta.PsiMetaData;
import org.jetbrains.annotations.NonNls;
import consulo.language.psi.PsiElement;
import consulo.language.psi.meta.PsiMetaOwner;
import consulo.language.psi.meta.PsiPresentableMetaData;
import consulo.util.collection.ArrayUtil;
import consulo.ui.image.Image;

/**
 * @author Alexey Chmutov
 */
public abstract class VtlPresentableNamedElement extends VtlNamedElement implements PsiMetaOwner, PsiPresentableMetaData
{
	public VtlPresentableNamedElement(final ASTNode node)
	{
		super(node);
	}

	public Image getIcon(final int flags)
	{
		return getIcon();
	}

	public PsiMetaData getMetaData()
	{
		return this;
	}

	public PsiElement getDeclaration()
	{
		return this;
	}

	public Image getIcon()
	{
		return IconDescriptorUpdaters.getIcon(this, 0);
	}

	@NonNls
	public String getName(final consulo.language.psi.PsiElement context)
	{
		return getName();
	}

	public void init(PsiElement element)
	{
	}

	public Object[] getDependences()
	{
		return ArrayUtil.EMPTY_OBJECT_ARRAY;
	}
}