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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.ExpressionContext;
import com.intellij.codeInsight.template.TextResult;
import com.intellij.util.Function;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @author Alexey Chmutov
 */
class StringCollectionExpression extends Expression {
    private final Collection<String> myAllOptions;
    private final Function<String, LookupElement> myMapper;

    public StringCollectionExpression(@NotNull Collection<String> allOptions) {
        this.myAllOptions = allOptions;
        this.myMapper = new Function<String, LookupElement>() {
            public LookupElement fun(final String option) {
                return LookupElementBuilder.create(option);
            }
        };
    }

    public StringCollectionExpression(@NotNull Collection<String> allOptions, @NotNull Function<String, LookupElement> mapper) {
        this.myAllOptions = allOptions;
        this.myMapper = mapper;
    }

    public com.intellij.codeInsight.template.Result calculateResult(final ExpressionContext context) {
        return calculateQuickResult(context);
    }

    public com.intellij.codeInsight.template.Result calculateQuickResult(final ExpressionContext context) {
        return myAllOptions.size() == 1 ? new TextResult(myAllOptions.iterator().next()) : null;
    }

    public LookupElement[] calculateLookupItems(final ExpressionContext context) {
        return ContainerUtil.map2Array(myAllOptions, LookupElement.class, myMapper);
    }
}
