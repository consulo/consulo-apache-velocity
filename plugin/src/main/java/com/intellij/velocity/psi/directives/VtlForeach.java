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

import static com.intellij.velocity.psi.VtlCompositeElementTypes.LOOP_VARIABLE;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.IncorrectOperationException;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlDirectiveHeader;
import com.intellij.velocity.psi.VtlVariable;
import consulo.ide.IconDescriptorUpdaters;
import consulo.ui.image.Image;
import consulo.velocity.api.facade.VelocityType;
import consulo.velocity.api.psi.StandardVelocityType;

/**
 * @author : Alexey Chmutov
 */
public class VtlForeach extends VtlDirectiveImpl
{
	private FixedNameReferenceElement velocityCountElement = null;
	private FixedNameReferenceElement velocityHasNextElement = null;

	public VtlForeach(ASTNode node)
	{
		super(node, "foreach", true);
	}

	@Override
	public boolean processDeclarations(
			@Nonnull final PsiScopeProcessor processor,
			@Nonnull final ResolveState state,
			final PsiElement lastParent,
			@Nonnull final PsiElement place)
	{
		final VtlDirectiveHeader header = findHeaderOfDirective();
		PsiElement ancestorOfPlace = place.getParent();
		while(ancestorOfPlace != null && ancestorOfPlace != this)
		{
			if(ancestorOfPlace == header)
			{
				return true;
			}
			ancestorOfPlace = ancestorOfPlace.getParent();
		}
		if(lastParent != getVelocityCountElement() && !processor.execute(getVelocityCountElement(), state))
		{
			return false;
		}
		if(lastParent != getVelocityHasNextElement() && !processor.execute(getVelocityHasNextElement(), state))
		{
			return false;
		}
		PsiElement loopVariable = header.findChildByType(LOOP_VARIABLE);
		if(loopVariable != null && lastParent != loopVariable && !processor.execute(loopVariable, state))
		{
			return false;
		}
		return super.processDeclarations(processor, state, lastParent, place);
	}

	private FixedNameReferenceElement getVelocityCountElement()
	{
		if(velocityCountElement == null)
		{
			velocityCountElement = new FixedNameReferenceElement("velocityCount", StandardVelocityType.INT);
		}
		return velocityCountElement;
	}

	private FixedNameReferenceElement getVelocityHasNextElement()
	{
		if(velocityHasNextElement == null)
		{
			velocityHasNextElement = new FixedNameReferenceElement("velocityHasNext", StandardVelocityType.BOOLEAN);
		}
		return velocityHasNextElement;
	}

	public class FixedNameReferenceElement extends RenameableFakePsiElement implements VtlVariable
	{
		@NonNls
		private final String myName;
		private final VelocityType myType;

		private FixedNameReferenceElement(@Nonnull String name, @Nonnull VelocityType type)
		{
			super(VtlForeach.this.getContainingFile());
			myName = name;
			myType = type;
		}

		@Override
		public PsiElement getParent()
		{
			return VtlForeach.this;
		}

		@Nonnull
		@Override
		public PsiElement getNavigationElement()
		{
			return VtlForeach.this;
		}

		@Override
		public Image getIcon()
		{
			return IconDescriptorUpdaters.getIcon(this, 0);
		}

		@Nonnull
		@Override
		public String getName()
		{
			return myName;
		}

		@Override
		public String getTypeName()
		{
			return PsiUtil.getUnqualifiedName(myType);
		}

		@Override
		public PsiElement setName(@Nonnull @NonNls String s) throws IncorrectOperationException
		{
			throw new IncorrectOperationException(VelocityBundle.message("operation.not.allowed"));
		}

		public Collection<PsiReference> findReferences(final PsiElement element)
		{
			return ReferencesSearch.search(element).findAll();
		}

		@Override
		@Nullable
		public VelocityType getPsiType()
		{
			return myType;
		}
	}
}