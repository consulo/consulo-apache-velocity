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
