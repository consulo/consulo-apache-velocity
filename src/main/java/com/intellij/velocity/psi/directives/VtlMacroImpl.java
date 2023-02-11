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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.VtlIcons;
import com.intellij.velocity.psi.VtlElementTypes;
import com.intellij.velocity.psi.VtlMacro;
import com.intellij.velocity.psi.VtlParameterDeclaration;
import com.intellij.velocity.psi.VtlPresentableNamedElement;
import com.intellij.velocity.psi.VtlVariable;
import consulo.language.ast.ASTNode;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.ui.image.Image;

/**
 * @author Alexey Chmutov
 */
public class VtlMacroImpl extends VtlPresentableNamedElement implements VtlDirective, VtlMacro {

    public VtlMacroImpl(@Nonnull final ASTNode node) {
        super(node);
    }

    public boolean processDeclarations(@Nonnull final PsiScopeProcessor processor, @Nonnull final ResolveState state, final PsiElement lastParent,
									   @Nonnull final PsiElement place) {
        if (!super.processDeclarations(processor, state, lastParent, place)) {
            return false;
        }
        for (final VtlVariable declaration : getParameters()) {
            if (!processor.execute(declaration, state)) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    protected PsiElement getNameElement() {
        return findHeaderOfDirective().findChildByType(VtlElementTypes.IDENTIFIER);
    }

    @Nullable
    public TextRange getNameElementRange() {
        PsiElement nameElement = getNameElement();
        return nameElement == null ? null : nameElement.getTextRange();
    }

    @Nonnull
    public String getPresentableName() {
        return "macro '" + getName() + "'";
    }

    @Nonnull
    public VtlParameterDeclaration[] getParameters() {
        return findHeaderOfDirective().findChildrenByClass(VtlParameterDeclaration.class);
    }

    public boolean isDeprecated() {
        return false;
    }

    public int getFoldingStartOffset() {
        return getNode().getTextRange().getStartOffset() + "#macro".length();
    }

    public int getFoldingEndOffset() {
        return getNode().getTextRange().getEndOffset() - "#end".length();
    }

    public boolean needsClosing() {
        return true;
    }

    public String getTypeName() {
        return VelocityBundle.message("type.name.macro");
    }

    public Image getIcon() {
        return VtlIcons.SHARP_ICON;
    }
}