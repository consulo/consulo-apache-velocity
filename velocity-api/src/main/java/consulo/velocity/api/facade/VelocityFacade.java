/*
 * Copyright 2013-2020 consulo.io
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

package consulo.velocity.api.facade;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.velocity.psi.VtlExpression;
import consulo.annotation.access.RequiredReadAction;
import consulo.velocity.api.psi.StandardVelocityType;

/**
 * @author VISTALL
 * @since 2020-05-19
 */
public interface VelocityFacade
{
	public static ExtensionPointName<VelocityFacade> EP_NAME = ExtensionPointName.create("com.intellij.velocity.facade");

	@Nonnull
	@RequiredReadAction
	static VelocityFacade getFacade(@Nonnull PsiElement element)
	{
		Module module = ModuleUtil.findModuleForPsiElement(element);
		if(module == null)
		{
			return DummyVelocityFacade.INSTANCE;
		}

		for(VelocityFacade facade : EP_NAME.getExtensionList())
		{
			if(facade.isMyModule(module))
			{
				return facade;
			}
		}
		return DummyVelocityFacade.INSTANCE;
	}

	@RequiredReadAction
	boolean isMyModule(Module module);

	default boolean isPrimitiveType(@Nullable VelocityType type)
	{
		return type instanceof StandardVelocityType && ((StandardVelocityType) type).isPrimitive();
	}

	default boolean isVoidType(@Nullable VelocityType type)
	{
		return type == StandardVelocityType.VOID;
	}

	default VelocityType extractTypeFromIterable(@Nonnull VtlExpression vtlExpression)
	{
		return null;
	}

	@Nullable
	default VelocityType createTypeFromText(@Nonnull String type, @Nonnull PsiFile file, @Nullable PsiElement context)
	{
		return null;
	}
}
