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

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Alexey Chmutov
 */
public class VtlDirectiveHeader extends VtlCompositeElement {
    public VtlDirectiveHeader(@NotNull final ASTNode node) {
        super(node);
    }

    @Nullable
    public PsiElement findChildByType(IElementType type) {
        return super.findChildByType(type);
    }

    @NotNull
    public <T> T[] findChildrenByClass(Class<T> aClass) {
        return super.findChildrenByClass(aClass);
    }

    @Nullable
    public <T> T findChildByClass(Class<T> aClass) {
        return super.findChildByClass(aClass);
    }
}
