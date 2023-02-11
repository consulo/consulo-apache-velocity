package com.intellij.velocity.psi.directives;

import com.intellij.velocity.psi.VtlElementTypes;
import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.psi.AbstractElementManipulator;
import consulo.language.util.IncorrectOperationException;

import javax.annotation.Nonnull;

@ExtensionImpl
public class VtlBreakManipulator extends AbstractElementManipulator<VtlBreak>
{
	public VtlBreak handleContentChange(final VtlBreak element, final TextRange range, final String newContent)
			throws IncorrectOperationException
	{
		return element;
	}

	public TextRange getRangeInElement(final VtlBreak element)
	{
		return new TextRange(0, VtlElementTypes.SHARP_BREAK.toString().length());
	}

	@Nonnull
	@Override
	public Class<VtlBreak> getElementClass()
	{
		return VtlBreak.class;
	}
}
