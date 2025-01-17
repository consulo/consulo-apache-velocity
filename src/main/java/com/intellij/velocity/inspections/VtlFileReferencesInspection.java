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

import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.VtlArgumentList;
import com.intellij.velocity.psi.VtlExpression;
import com.intellij.velocity.psi.VtlLiteralExpressionType;
import com.intellij.velocity.psi.directives.VtlParse;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.EmptyResolveMessageProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiReference;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlFileReferencesInspection extends VtlInspectionBase {

    @Override
    @RequiredReadAction
    protected void registerProblems(PsiElement element, ProblemsHolder holder) {
        if (element instanceof VtlParse) {
            VtlArgumentList argumentList = ((VtlParse) element).getArgumentList();
            if (argumentList == null) {
                return;
            }
            VtlExpression[] arguments = argumentList.getArguments();
            final String message = VelocityLocalize.vtlOnlyFirstParseArgumentWillBeParsed().get();
            for (int i = 1; i < arguments.length; i++) {
                holder.registerProblem(arguments[i], message, ProblemHighlightType.LIKE_UNUSED_SYMBOL);
            }
        }
        else {
            if (element instanceof VtlLiteralExpressionType.VtlStringLiteral) {
                final PsiFile file = element.getContainingFile();
                if (file instanceof VtlFile && ((VtlFile) file).isIdeTemplateFile()) {
                    return;
                }
                for (final PsiReference reference : element.getReferences()) {
                    if (reference.resolve() != null) {
                        continue;
                    }
                    final LocalizeValue message = ((EmptyResolveMessageProvider) reference).buildUnresolvedMessage(reference.getCanonicalText());
                    holder.registerProblem(reference, message.get(), ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
                }
            }
        }
    }

    @Override
    @Nls
    @Nonnull
    public String getDisplayName() {
        return VelocityBundle.message("vtl.file.references.inspection");
    }

    @Override
    @NonNls
    @Nonnull
    public String getShortName() {
        return "VtlFileReferencesInspection";
    }

    @Nonnull
    @Override
    public LocalizeValue getDescription() {
        return VelocityLocalize.inspectiondescriptionsVtlfilereferencesinspection();
    }
}