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

import com.intellij.velocity.psi.VtlLiteralExpressionType.VtlStringLiteral;
import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.psi.AbstractElementManipulator;
import consulo.language.util.IncorrectOperationException;

import javax.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlStringManipulator extends AbstractElementManipulator<VtlStringLiteral>
{
	public VtlStringLiteral handleContentChange(final VtlStringLiteral element, final consulo.document.util.TextRange range, final String newContent)
			throws IncorrectOperationException
	{
		return element.setStringValue(range, newContent);
	}

	public TextRange getRangeInElement(final VtlStringLiteral element)
	{
		return element.getValueRange();
	}

	@Nonnull
	@Override
	public Class<VtlStringLiteral> getElementClass()
	{
		return VtlStringLiteral.class;
	}
}
