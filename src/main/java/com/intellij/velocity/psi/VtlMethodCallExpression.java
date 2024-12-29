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

import com.intellij.java.language.psi.PsiType;
import consulo.language.ast.ASTNode;
import consulo.util.collection.ContainerUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
        return ContainerUtil.map2Array(args, PsiType.class, expression -> expression.getPsiType());
    }

    @Nullable
    public PsiType getPsiType() {
        return getReferenceExpression().getPsiType();
    }
}
