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

import com.intellij.velocity.psi.VtlLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.codeStyle.Alignment;
import consulo.language.codeStyle.CodeStyleSettings;
import consulo.language.codeStyle.Wrap;
import consulo.language.codeStyle.template.DataLanguageBlockWrapper;
import consulo.language.codeStyle.template.TemplateLanguageBlock;
import consulo.language.codeStyle.template.TemplateLanguageFormattingModelBuilder;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.List;

/**
 * @author Alexey Chmutov
 *         Date: Jun 26, 2009
 *         Time: 4:07:09 PM
 */
@ExtensionImpl
public class VtlFormattingModelBuilder extends TemplateLanguageFormattingModelBuilder
{
	@Override
	public TemplateLanguageBlock createTemplateLanguageBlock(@Nonnull ASTNode astNode, @Nullable Wrap wrap, @Nullable Alignment alignment, @Nullable List<DataLanguageBlockWrapper> dataLanguageBlockWrappers, @Nonnull CodeStyleSettings codeStyleSettings)
	{
		return new VtlBlock(astNode, wrap, alignment, this, codeStyleSettings, dataLanguageBlockWrappers);
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return VtlLanguage.INSTANCE;
	}
}
