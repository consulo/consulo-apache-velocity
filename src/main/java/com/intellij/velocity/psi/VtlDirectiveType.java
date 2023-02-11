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

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import com.intellij.velocity.psi.directives.VtlDirectiveImpl;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 29.04.2008
 */
public class VtlDirectiveType extends VtlCompositeElementType {
    private final String myPresentableName;

    private final boolean myNeedsClosing;

    public VtlDirectiveType(@Nonnull final String debugName, @Nonnull final String presentableName, boolean needsClosing) {
        super(debugName);
        myPresentableName = presentableName;
        myNeedsClosing = needsClosing;
    }

    public VtlDirectiveType(@Nonnull final String debugName) {
        super(debugName);
        myPresentableName = null;
        myNeedsClosing = false;
    }

    @Override
    public PsiElement createPsiElement(ASTNode node) {
        return new VtlDirectiveImpl(node, myPresentableName, myNeedsClosing);
    }

}
