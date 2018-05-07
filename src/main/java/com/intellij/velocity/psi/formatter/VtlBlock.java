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

package com.intellij.velocity.psi.formatter;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.templateLanguages.*;
import com.intellij.formatting.templateLanguages.BlockWithParent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.velocity.psi.VtlCompositeElementTypes;
import com.intellij.velocity.psi.VtlDirectiveType;
import com.intellij.velocity.psi.VtlElementTypes;

/**
 * @author Alexey Chmutov
 *         Date: Jun 26, 2009
 *         Time: 4:05:40 PM
 */
public class VtlBlock extends TemplateLanguageBlock
{
	public VtlBlock(@Nonnull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nonnull TemplateLanguageBlockFactory blockFactory, @Nonnull CodeStyleSettings settings, @Nullable List<DataLanguageBlockWrapper> foreignChildren)
	{
		super(node, wrap, alignment, blockFactory, settings, foreignChildren);
	}

	public Indent getIndent()
	{
		final BlockWithParent parent = getParent();
		if(parent == null || parent.getParent() == null)
		{
			return Indent.getNoneIndent();
		}

		IElementType type = getNode().getElementType();
		if((type instanceof VtlDirectiveType && type != VtlCompositeElementTypes.DIRECTIVE_ELSEIF && type != VtlCompositeElementTypes.DIRECTIVE_ELSE) || type == VtlCompositeElementTypes.DIRECTIVE_BREAK || type == VtlCompositeElementTypes.INTERPOLATION)
		{
			return Indent.getNormalIndent();
		}
		return Indent.getNoneIndent();
	}

	protected Indent getChildIndent()
	{
		return getNode().getTreeParent() == null ? Indent.getNoneIndent() : Indent.getNormalIndent();
	}

	@Override
	protected IElementType getTemplateTextElementType()
	{
		return VtlElementTypes.TEMPLATE_TEXT;
	}
}

