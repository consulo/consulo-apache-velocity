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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.language.ast.ASTNode;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 11.06.2008
 */
public abstract class VtlCallExpression extends VtlCompositeElement {

    public VtlCallExpression(@Nonnull final ASTNode node) {
        super(node);
    }

    @Nonnull
    public VtlReferenceExpression getReferenceExpression() {
        return findNotNullChildByClass(VtlReferenceExpression.class);
    }

    @Nonnull
    public VtlCallable[] getCallableCandidates() {
        return getReferenceExpression().getCallableCandidates();
    }

    @Nullable
    public VtlArgumentList findArgumentList() {
        return findChildByClass(VtlArgumentList.class);
    }
}
