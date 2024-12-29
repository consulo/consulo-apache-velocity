package com.intellij.velocity.inspections;

import com.intellij.velocity.psi.VtlLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.AnnotatorFactory;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author VISTALL
 * @since 11/02/2023
 */
@ExtensionImpl
public class VtlVariableTypeAnnotatorFactory implements AnnotatorFactory
{
	@Nullable
	@Override
	public Annotator createAnnotator()
	{
		return new VtlVariableTypeAnnotator();
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return VtlLanguage.INSTANCE;
	}
}
