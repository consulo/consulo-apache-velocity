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

import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiPrimitiveType;
import com.intellij.java.language.psi.PsiType;
import com.intellij.lang.properties.references.PropertyReference;
import com.intellij.velocity.psi.directives.VtlDirective;
import com.intellij.velocity.psi.directives.VtlFileReferenceDirective;
import com.intellij.velocity.psi.directives.VtlMacroCall;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiReference;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 07.06.2008
 */
public class VtlLiteralExpressionType extends VtlCompositeElementType {
    private final String myTypeName;
    private final PsiPrimitiveType myPrimitiveType;

    public VtlLiteralExpressionType(@Nonnull @NonNls String debugName, @Nonnull String typeName) {
        super(debugName);
        myTypeName = typeName;
        myPrimitiveType = null;
    }

    public VtlLiteralExpressionType(@Nonnull @NonNls String debugName, @Nonnull PsiType primitiveType) {
        super(debugName);
        myTypeName = null;
        assert primitiveType instanceof PsiPrimitiveType;
        myPrimitiveType = (PsiPrimitiveType) primitiveType;
    }

    @Override
    public consulo.language.psi.PsiElement createPsiElement(ASTNode node) {
        return VtlCompositeElementTypes.STRING_LITERALS.contains(this)
                ? new VtlStringLiteral(node)
                : new VtlLiteralExpression(node);
    }

    public class VtlLiteralExpression extends VtlCompositeElement implements VtlExpression {
        public VtlLiteralExpression(@Nonnull final ASTNode node) {
            super(node);
        }

        public PsiType getPsiType() {
            if (myPrimitiveType != null) {
                return myPrimitiveType;
            }
            return JavaPsiFacade.getInstance(getProject()).getElementFactory().createTypeByFQClassName(myTypeName, getResolveScope());
        }
    }

    public class VtlStringLiteral extends VtlLiteralExpression {

        public VtlStringLiteral(@Nonnull final ASTNode node) {
            super(node);
        }

        @Nonnull
        public consulo.language.psi.PsiReference[] getReferences() {
            if (isFileReference()) {
                return PsiUtil.getFileReferences(getValueText(), this, getFirstChild().getTextLength(), true);
            }
            if (isPropertyReference()) {
                return new consulo.language.psi.PsiReference[]{new PropertyReference(getValueText(), this, null, true)};
            }
            return PsiReference.EMPTY_ARRAY;
        }

        private boolean isFileReference() {
            return isStringLiteralAndArgumentOf(VtlFileReferenceDirective.class);
        }

        private boolean isPropertyReference() {
            if (!isStringLiteralAndArgumentOf(VtlMacroCall.class) || getPrevSibling() != null) {
                return false;
            }
            VtlMacroCall macroCall = (VtlMacroCall) getParent().getParent();
            String macroName = macroCall.getReferenceExpression().getReferenceName();
            return "springMessageText".equals(macroName) || "springMessage".equals(macroName)
                   || "springThemeText".equals(macroName) || "springTheme".equals(macroName);
        }

        private String getValueText() {
            String text = getText();
            return text.substring(1, text.length() - 1);
        }

        private boolean isStringLiteralAndArgumentOf(Class<? extends VtlDirective> directiveClass) {
            final consulo.language.psi.PsiElement parent = getParent();
            return parent instanceof VtlArgumentList && directiveClass.isInstance(parent.getParent());
        }

        public TextRange getValueRange() {
          return new TextRange(1, getText().length() - 1);
        }

        public VtlStringLiteral setStringValue(final TextRange range, final String newContent) {
          String oldText = getText();
          String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
          final consulo.language.psi.PsiElement newElement = PsiUtil.createStringLiteral(getProject(), newText);
          final ASTNode newNode = newElement.getNode();
          getParent().getNode().replaceChild(getNode(), newNode);
          return (VtlStringLiteral) newNode.getPsi();
        }
    }
}
