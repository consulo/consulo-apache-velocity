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

import static com.intellij.codeInspection.ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
import static com.intellij.codeInspection.ProblemHighlightType.LIKE_UNKNOWN_SYMBOL;

import javax.annotation.Nonnull;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveResult;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;

/**
 * @author Alexey Chmutov
 */
public class VtlReferencesInspection extends VtlInspectionBase {

  protected void registerProblems(PsiElement element, ProblemsHolder holder) {
    if (!(element instanceof VtlReferenceExpression)) {
      return;
    }
    final VtlReferenceExpression ref = (VtlReferenceExpression)element;
    if (!ref.isQualifierResolved()) {
      return;
    }
    final PsiFile file = ref.getContainingFile();
    if (file instanceof VtlFile && ((VtlFile)file).isIdeTemplateFile()) {
      return;
    }
    final ResolveResult[] results = ref.multiResolve(false);
    final boolean resolvedWithError = results.length > 0 && !results[0].isValidResult();

    if (resolvedWithError || ref.resolve() == null) {
      final String message = ref.getUnresolvedMessage(resolvedWithError);
      holder.registerProblem(ref, message, resolvedWithError ? GENERIC_ERROR_OR_WARNING : LIKE_UNKNOWN_SYMBOL);
    }
  }

  @Nls
  @Nonnull
  public String getDisplayName() {
    return VelocityBundle.message("vtl.references.inspection");
  }

  @NonNls
  @Nonnull
  public String getShortName() {
    return "VtlReferencesInspection";
  }
}