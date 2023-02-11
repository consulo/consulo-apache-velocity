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

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import com.intellij.velocity.VtlIcons;
import com.intellij.velocity.psi.directives.VtlDirective;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.ui.image.Image;

/**
 * @author Alexey Chmutov
 */
public class VtlCompositeElement extends ASTWrapperPsiElement
{

    public VtlCompositeElement(@Nonnull final ASTNode node) {
        super(node);
    }

    public boolean processDeclarations(@Nonnull final PsiScopeProcessor processor, @Nonnull final ResolveState state, @Nullable final consulo.language.psi.PsiElement lastParent,
									   @Nonnull final PsiElement place) {
        return PsiUtil.processDeclarations(processor, state, lastParent, null, this);
    }

    @Nonnull
    public VtlDirective[] getDirectiveChildren() {
        return findChildrenByClass(VtlDirective.class);
    }

    public VtlCompositeElement[] getCompositeChildren() {
        return findChildrenByClass(VtlCompositeElement.class);
    }

    public VtlFile getContainingFile() {
        return (VtlFile) super.getContainingFile();
    }

    public Image getIcon(final int flags) {
        return VtlIcons.SHARP_ICON;
    }

    public String toString() {
        return getNode().getElementType().toString();
    }

    @Nullable
    protected VtlDirectiveHeader findHeaderOfDirective() {
        return super.findChildByClass(VtlDirectiveHeader.class);
    }
}
