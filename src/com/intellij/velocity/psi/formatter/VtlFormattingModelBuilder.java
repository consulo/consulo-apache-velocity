package com.intellij.velocity.psi.formatter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.templateLanguages.DataLanguageBlockWrapper;
import com.intellij.formatting.templateLanguages.TemplateLanguageBlock;
import com.intellij.formatting.templateLanguages.TemplateLanguageFormattingModelBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;

/**
 * @author Alexey Chmutov
 *         Date: Jun 26, 2009
 *         Time: 4:07:09 PM
 */
public class VtlFormattingModelBuilder extends TemplateLanguageFormattingModelBuilder
{
	@Override
	public TemplateLanguageBlock createTemplateLanguageBlock(@NotNull ASTNode astNode, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable List<DataLanguageBlockWrapper> dataLanguageBlockWrappers, @NotNull CodeStyleSettings codeStyleSettings)
	{
		return new VtlBlock(astNode, wrap, alignment, this, codeStyleSettings, dataLanguageBlockWrappers);
	}
}
