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

package consulo.apache.velocity;

import com.intellij.velocity.psi.VtlParameterDeclaration;
import com.intellij.velocity.psi.VtlVariable;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.AllIcons;
import consulo.language.icon.IconDescriptor;
import consulo.language.icon.IconDescriptorUpdater;
import consulo.language.psi.PsiElement;

import jakarta.annotation.Nonnull;

/**
 * @author VISTALL
 * @since 06.10.13.
 */
@ExtensionImpl
public class VtlIconDescriptorUpdater implements IconDescriptorUpdater
{
	@Override
	public void updateIcon(@Nonnull IconDescriptor iconDescriptor, @Nonnull PsiElement element, int i)
	{
		if(element instanceof VtlParameterDeclaration)
		{
			iconDescriptor.setMainIcon(AllIcons.Nodes.Parameter);
		}
		if(element instanceof VtlVariable)
		{
			iconDescriptor.setMainIcon(AllIcons.Nodes.Variable);
		}
	}
}
