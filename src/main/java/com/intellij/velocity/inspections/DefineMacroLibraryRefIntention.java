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

import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.directives.VtlMacroCall;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.language.editor.template.Expression;
import consulo.language.editor.template.Template;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.function.Function;

/**
 * @author Alexey Chmutov
 */
public abstract class DefineMacroLibraryRefIntention extends DefineInCommentIntention {
    public DefineMacroLibraryRefIntention(@Nonnull LocalizeValue text) {
        super(text, VelocityLocalize.addMacroLibraryRefFixName());
    }

    @Override
    protected boolean isAvailable(@Nonnull VtlReferenceExpression ref) {
        return ref.getParent() instanceof VtlMacroCall && Util.canSetVelocityProperties(ref.getContainingFile());
    }

    protected void prepareTemplate(@Nonnull Template template, @Nonnull final PsiElement element, String relativePath, @Nonnull final PsiFile fileToInsertComment) {
        assert element instanceof VtlReferenceExpression;
        final List<String> allFiles = Util.collectFilePaths(element, new Function<PsiFile, String>() {
            public String apply(@Nonnull final PsiFile psiFile) {
                PsiFile file = psiFile.getViewProvider().getPsi(VtlLanguage.INSTANCE);
                if (file instanceof VtlFile) {
                    VtlFile vtlFile = (VtlFile) file;
                    if (vtlFile.getNumberOfMacros(((VtlReferenceExpression) element).getReferenceName()) > 0) {
                        return PsiUtil.getRelativePath(fileToInsertComment, vtlFile);
                    }
                }
                return null;
            }
        });

        template.addTextSegment("#* @vtlmacrolibrary path=\"");
        final Expression pathExpression = new StringCollectionExpression(allFiles);
        template.addVariable("PATH", pathExpression, pathExpression, true);
        final String fileRef = relativePath != null ? " file=\"" + relativePath + "\"" : "";
        template.addTextSegment("\"" + fileRef + " *#\n");
        template.addEndVariable();
    }
}