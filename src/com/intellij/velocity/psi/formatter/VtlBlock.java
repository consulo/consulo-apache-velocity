package com.intellij.velocity.psi.formatter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
	public VtlBlock(@NotNull ASTNode node, @Nullable Wrap wrap, @Nullable Alignment alignment, @NotNull TemplateLanguageBlockFactory blockFactory, @NotNull CodeStyleSettings settings, @Nullable List<DataLanguageBlockWrapper> foreignChildren)
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

