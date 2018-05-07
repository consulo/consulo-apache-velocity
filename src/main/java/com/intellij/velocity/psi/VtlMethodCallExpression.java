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
package com.intellij.velocity.psi;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiType;
import com.intellij.util.NullableFunction;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author Alexey Chmutov
 */
public class VtlMethodCallExpression extends VtlCallExpression implements VtlExpression {
    public VtlMethodCallExpression(final ASTNode node) {
        super(node);
    }

    @Nonnull
    public VtlArgumentList getArgumentList() {
        return findNotNullChildByClass(VtlArgumentList.class);
    }

    public PsiType[] getArgumentTypes() {
        VtlExpression[] args = getArgumentList().getArguments();
        return ContainerUtil.map2Array(args, PsiType.class, new NullableFunction<VtlExpression, PsiType>() {
            public PsiType fun(final VtlExpression expression) {
                return expression.getPsiType();
            }
        });
    }

    @Nullable
    public PsiType getPsiType() {
        return getReferenceExpression().getPsiType();
    }
}
