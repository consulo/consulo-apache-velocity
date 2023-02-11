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
import com.intellij.velocity.psi.VtlCompositeElement;
import consulo.language.ast.ASTNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
public class VtlDirectiveImpl extends VtlCompositeElement implements VtlDirective {

    @Nonnull
    private final String myPresentableName;

    private final boolean myNeedsClosing;

    public VtlDirectiveImpl(@Nonnull final ASTNode node, @Nonnull String presentableName, boolean needsClosing) {
        super(node);
        myPresentableName = presentableName;
        myNeedsClosing = needsClosing;
    }

    @Nonnull
    public String getPresentableName() {
        return myPresentableName;
    }

    public int getFoldingStartOffset() {
        return getNode().getTextRange().getStartOffset() + myPresentableName.length() + 1;
    }

    public int getFoldingEndOffset() {
        return getNode().getTextRange().getEndOffset() - (needsClosing() ? "#end".length() : 0);
    }

    public boolean needsClosing() {
        return myNeedsClosing;
    }

    @Nullable
    public VtlArgumentList getArgumentList() {
        return findChildByClass(VtlArgumentList.class);
    }
}
