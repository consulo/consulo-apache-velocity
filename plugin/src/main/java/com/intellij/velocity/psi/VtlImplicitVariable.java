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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.util.Factory;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.annotation.access.RequiredReadAction;
import consulo.ide.IconDescriptorUpdaters;
import consulo.ui.image.Image;
import consulo.velocity.api.facade.VelocityFacade;
import consulo.velocity.api.facade.VelocityType;

/**
 * @author Alexey Chmutov
 */
public class VtlImplicitVariable extends RenameableFakePsiElement implements VtlVariable
{
	@Nullable
	private final PsiComment myComment;
	private final String myName;
	private String myType;
	private final VtlFile myScopeFile;

	private VtlImplicitVariable(@Nonnull final PsiFile containingFile, @Nullable final PsiComment comment, @Nonnull final String name, @Nullable VtlFile scopeFile)
	{
		super(containingFile);
		myComment = comment;
		myName = name;
		myScopeFile = scopeFile;
	}

	@RequiredReadAction
	@Override
	@Nonnull
	public String getName()
	{
		return myName;
	}

	@Nonnull
	@Override
	public PsiElement getNavigationElement()
	{
		return myComment != null ? myComment : getContainingFile();
	}

	@Override
	public PsiElement getParent()
	{
		return myComment;
	}

	@Override
	public String getTypeName()
	{
		return VelocityBundle.message("type.name.variable");
	}

	@Override
	public String toString()
	{
		return "ImplicitVariable " + myName;
	}

	public void setType(final String type)
	{
		myType = type;
	}

	@RequiredReadAction
	@Override
	@Nullable
	public VelocityType getPsiType()
	{
		if(myType == null)
		{
			return null;
		}

		return VelocityFacade.getFacade(getContainingFile()).createTypeFromText(myType, getContainingFile(), myComment);
	}

	@Override
	public Image getIcon()
	{
		return IconDescriptorUpdaters.getIcon(this, 0);
	}

	public static VtlImplicitVariable getOrCreate(@Nonnull final Map<String, VtlImplicitVariable> mapToAddTo,
												  @Nonnull final PsiFile containingFile,
												  @Nullable final PsiComment comment,
												  final String name,
												  @Nullable final VtlFile scopeFile)
	{
		assert comment == null || comment.getContainingFile() == containingFile;
		return ContainerUtil.getOrCreate(mapToAddTo, name, (Factory<VtlImplicitVariable>) () -> new VtlImplicitVariable(containingFile, comment, name, scopeFile));
	}


	public boolean isVisibleIn(@Nullable VtlFile placeFile)
	{
		return placeFile == null || myScopeFile == null || placeFile.isEquivalentTo(myScopeFile);
	}
}