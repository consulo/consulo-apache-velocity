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
package com.intellij.velocity.inspections.wellformedness;

import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.inspections.VtlInspectionBase;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlParameterDeclaration;
import com.intellij.velocity.psi.directives.VtlAssignment;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlInterpolationsInspection extends VtlInspectionBase {

    protected void registerProblems(consulo.language.psi.PsiElement element, ProblemsHolder holder) {
        if (element instanceof VtlParameterDeclaration) {
            PsiElement wouldBeFormalNotationStart = element.getFirstChild();
            if(PsiUtil.isFormalNotationStart(wouldBeFormalNotationStart)) {
                registerFormalNotationProblem(wouldBeFormalNotationStart.getNextSibling(), holder);
            }
        } else if (element instanceof VtlAssignment) {
            PsiElement wouldBeVariableDeclared = ((VtlAssignment) element).getAssignedVariableElement();
            if (wouldBeVariableDeclared != null
                    && PsiUtil.isFormalNotationStart(wouldBeVariableDeclared.getPrevSibling())) {
                registerFormalNotationProblem(wouldBeVariableDeclared, holder);
            }
        }
    }

    private static void registerFormalNotationProblem(PsiElement element, ProblemsHolder holder) {
        holder.registerProblem(element, VelocityBundle.message("vtl.formal.notation.is.not.allowed"),
                ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
    }

    @Nonnull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }

    @Nls
    @Nonnull
    public String getDisplayName() {
        return VelocityBundle.message("vtl.welformedness.inspection");
    }

    @NonNls
    @Nonnull
    public String getShortName() {
        return "VtlInterpolationsInspection";
    }
}