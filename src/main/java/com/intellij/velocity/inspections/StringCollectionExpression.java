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

package com.intellij.velocity.inspections;

import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.editor.template.Expression;
import consulo.language.editor.template.ExpressionContext;
import consulo.language.editor.template.Result;
import consulo.language.editor.template.TextResult;
import consulo.util.collection.ContainerUtil;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Alexey Chmutov
 */
class StringCollectionExpression extends Expression
{
	private final Collection<String> myAllOptions;
	private final java.util.function.Function<String, LookupElement> myMapper;

	public StringCollectionExpression(@Nonnull Collection<String> allOptions)
	{
		this.myAllOptions = allOptions;
		this.myMapper = LookupElementBuilder::create;
	}

	public StringCollectionExpression(@Nonnull Collection<String> allOptions, @Nonnull java.util.function.Function<String, LookupElement> mapper)
	{
		this.myAllOptions = allOptions;
		this.myMapper = mapper;
	}

	public Result calculateResult(final ExpressionContext context)
	{
		return calculateQuickResult(context);
	}

	public Result calculateQuickResult(final ExpressionContext context)
	{
		return myAllOptions.size() == 1 ? new TextResult(myAllOptions.iterator().next()) : null;
	}

	public LookupElement[] calculateLookupItems(final ExpressionContext context)
	{
		return ContainerUtil.map2Array(myAllOptions, LookupElement.class, myMapper);
	}
}
