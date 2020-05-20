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

import org.jetbrains.annotations.NonNls;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.NotNullLazyValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.IncorrectOperationException;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.VtlElementTypes;
import com.intellij.velocity.psi.VtlVariable;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.ide.IconDescriptorUpdaters;
import consulo.ui.image.Image;
import consulo.velocity.api.facade.VelocityType;

/**
 * @author Alexey Chmutov
 */
public abstract class VtlAssignment extends VtlDirectiveImpl {

    private final NotNullLazyValue<CachedValue<AssignedVariable>> myAssignedVariable;

    protected VtlAssignment(final ASTNode node, @Nonnull String presentableName, boolean needsClosing) {
        super(node, presentableName, needsClosing);
        myAssignedVariable = new NotNullLazyValue<CachedValue<AssignedVariable>>() {
            @Nonnull
            protected CachedValue<AssignedVariable> compute() {
                return CachedValuesManager.getManager(getProject()).createCachedValue(new CachedValueProvider<AssignedVariable>()
				{
					public Result<AssignedVariable> compute()
					{
						return Result.create(createAssignedVariable(), PsiModificationTracker.MODIFICATION_COUNT);
					}
				}, false);
            }
        };
    }

    @Nullable
    private AssignedVariable createAssignedVariable() {
        VtlReferenceExpression varElement = getAssignedVariableElement();
        if (varElement == null) {
            return null;
        }
        if (getContainingFile().findImplicitVariable(varElement.getReferenceName()) != null) {
            return null;
        }
        return new AssignedVariable();
    }

    @Nullable
    public VtlReferenceExpression getAssignedVariableElement() {
        PsiElement element = findChildByType(VtlElementTypes.REFERENCE_EXPRESSION);
        if (element == null) {
            return null;
        }
        VtlReferenceExpression expression = (VtlReferenceExpression) element;
        return expression.hasQualifier() ? null : expression;
    }

    @Nullable
    public PsiElement getAssignedMethodCallExpression() {
        return findChildByType(VtlElementTypes.METHOD_CALL_EXPRESSION);
    }

    @Nullable
    public abstract VelocityType getAssignedVariableElementType();

    @Nonnull
    public String getPresentableName() {
        String dirName = super.getPresentableName();
        PsiElement nameElement = findChildByType(VtlElementTypes.REFERENCE_EXPRESSION);
        return nameElement == null ? dirName : dirName + " '" + nameElement.getText() + "'";
    }

    public VtlVariable getAssignedVariable() {
        return myAssignedVariable.getValue().getValue();
    }

    private class AssignedVariable extends RenameableFakePsiElement implements VtlVariable {

        public AssignedVariable() {
            super(VtlAssignment.this.getContainingFile());
        }

        @Nonnull
        public String getName() {
            final VtlReferenceExpression expression = getAssignedVariableElement();
            assert expression != null;
            return expression.getReferenceName();
        }

        public PsiElement setName(@Nonnull @NonNls String name) throws IncorrectOperationException {
            VtlReferenceExpression nameElement = getAssignedVariableElement();
            assert nameElement != null;
            nameElement.handleElementRename(name);
            return this;
        }

        @Nullable
        @Override
        public PsiElement getNavigationElement() {
            final VtlReferenceExpression expression = getAssignedVariableElement();
            assert expression != null;
            return expression.getReferenceNameElement();
        }

        public PsiElement getParent() {
            return getAssignedVariableElement();
        }

        public String getTypeName() {
            return VelocityBundle.message("type.name.variable");
        }

        public Image getIcon() {
            return IconDescriptorUpdaters.getIcon(this, 0);
        }

        public VelocityType getPsiType() {
            return getAssignedVariableElementType();
        }

        public boolean isEquivalentTo(final PsiElement another) {
            if (!getClass().isInstance(another)) {
                return false;
            }
            AssignedVariable other = (AssignedVariable) another;
            return getName().equals(other.getName())
                    && getContainingFile().equals(other.getContainingFile());
        }

        public String toString() {
            return "AssignedVariable " + getName();
        }
    }
}