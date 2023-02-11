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

package com.intellij.velocity.inspections;

import com.intellij.java.language.psi.PsiType;
import com.intellij.velocity.psi.directives.VtlSet;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nonnull;

import static com.intellij.velocity.VelocityBundle.message;
import static consulo.language.editor.inspection.ProblemHighlightType.LIKE_UNUSED_SYMBOL;

@ExtensionImpl
public class VtlDirectiveArgsInspection extends VtlInspectionBase {
  protected void registerProblems(PsiElement element, ProblemsHolder holder) {
    if (element instanceof VtlSet) {
      VtlSet vtlSet = (VtlSet)element;
      String msg = null;
      if (vtlSet.getAssignedMethodCallExpression() != null) {
        holder.registerProblem(vtlSet.getFirstChild(), message("assignment.to.method.call"), LIKE_UNUSED_SYMBOL);
      }
      else {
        PsiType assignedType = vtlSet.getAssignedVariableElementType();
        if (PsiType.VOID.equals(assignedType)) {
          holder.registerProblem(vtlSet.getFirstChild(), message("assignment.of.void"), LIKE_UNUSED_SYMBOL);
        }
      }
    }
  }

  @Nls
  @Nonnull
  public String getDisplayName() {
    return message("vtl.directive.args.inspection");
  }

  @NonNls
  @Nonnull
  public String getShortName() {
    return "VtlDirectiveArgsInspection";
  }
}