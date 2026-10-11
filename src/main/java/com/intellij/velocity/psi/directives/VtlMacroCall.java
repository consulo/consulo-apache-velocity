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

package com.intellij.velocity.psi.directives;

import com.intellij.velocity.psi.VtlArgumentList;
import com.intellij.velocity.psi.VtlCallExpression;
import static com.intellij.velocity.psi.VtlCompositeElementTypes.REFERENCE_EXPRESSION;

import consulo.annotation.access.RequiredReadAction;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;

import com.intellij.velocity.psi.VtlExpression;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;

/**
 * @author Alexey Chmutov
 */
public class VtlMacroCall extends VtlCallExpression implements VtlDirective {
    public VtlMacroCall(ASTNode node) {
        super(node);
    }

    @Nonnull
    @RequiredReadAction
    public LocalizeValue getPresentableName() {
        PsiElement child = findChildByType(REFERENCE_EXPRESSION);
        return child != null ? LocalizeValue.of(child.getText()) : LocalizeValue.empty();
    }

    @Override
    @RequiredReadAction
    public int getFoldingStartOffset() {
        return getNode().getTextRange().getStartOffset() + getPresentableName().get().length() + 1;
    }

    @Override
    @RequiredReadAction
    public int getFoldingEndOffset() {
        return getNode().getTextRange().getEndOffset();
    }

    @Override
    public boolean needsClosing() {
        return false;
    }

    @Nonnull
    @RequiredReadAction
    public VtlExpression[] getArguments() {
        VtlArgumentList argList = findChildByClass(VtlArgumentList.class);
        if (argList == null) {
            return VtlExpression.EMPTY_ARRAY;
        }
        return argList.getArguments();
    }
}
