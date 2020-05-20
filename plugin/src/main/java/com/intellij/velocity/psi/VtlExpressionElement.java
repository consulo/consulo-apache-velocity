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

import com.intellij.lang.ASTNode;
import consulo.velocity.api.facade.VelocityType;

import javax.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
class VtlExpressionElement extends VtlCompositeElement implements VtlExpression
{
	public VtlExpressionElement(@Nonnull final ASTNode node)
	{
		super(node);
	}

	public VelocityType getPsiType()
	{
		VtlExpression childExpression = findChildByClass(VtlExpression.class);
		if(childExpression != null)
		{
			return childExpression.getPsiType();
		}
		return null;
	}
}