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

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.ast.IElementType;

import jakarta.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
public class VtlDirectiveHeader extends VtlCompositeElement {
    public VtlDirectiveHeader(@Nonnull final ASTNode node) {
        super(node);
    }

    @Nullable
    public PsiElement findChildByType(IElementType type) {
        return super.findChildByType(type);
    }

    @Nonnull
    public <T> T[] findChildrenByClass(Class<T> aClass) {
        return super.findChildrenByClass(aClass);
    }

    @Nullable
    public <T> T findChildByClass(Class<T> aClass) {
        return super.findChildByClass(aClass);
    }
}
