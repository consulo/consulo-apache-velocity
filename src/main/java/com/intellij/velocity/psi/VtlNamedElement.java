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

import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNamedElement;
import consulo.language.util.IncorrectOperationException;
import consulo.language.ast.ASTNode;
import org.jetbrains.annotations.NonNls;

/**
 * @author Alexey Chmutov
 */
public abstract class VtlNamedElement extends VtlCompositeElement implements PsiNamedElement {
    public VtlNamedElement(@Nonnull final ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public final String getName() {
        PsiElement e = getNameElement();
        if (e == null) {
            return null;
        }
        return e.getText();
    }

    @Nullable
    protected PsiElement getNameElement() {
        // may return null only for an element containing syntax errors
        return findChildByType(VtlElementTypes.IDENTIFIER);
    }

    @Nonnull
    public PsiElement setName(@Nonnull @NonNls String name) throws IncorrectOperationException {
        consulo.language.psi.PsiElement nameElement = getNameElement();
        assert nameElement != null;
        final PsiElement newNameElement = PsiUtil.createIdentifierElement(getProject(), name);
        nameElement.replace(newNameElement);
        return this;
    }

    @Override
    public int getTextOffset() {
        PsiElement e = getNameElement();
        if (e == null) {
            return super.getTextOffset();
        }
        return e.getTextOffset();
    }
}
