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

import com.intellij.java.language.psi.PsiPrimitiveType;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.VtlReferenceContributor;
import com.intellij.velocity.psi.VtlImplicitVariable;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.document.util.TextRange;
import consulo.language.editor.annotation.AnnotationHolder;
import consulo.language.editor.annotation.Annotator;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;

/**
 * @author Alexey Chmutov
 */
public class VtlVariableTypeAnnotator implements Annotator
{
    public void annotate(final PsiElement element, final AnnotationHolder holder) {

        if (!VtlReferenceContributor.VTLVARIABLE_COMMENT.accepts(element)) {
            return;
        }
        final String text = element.getText();
        final String[] nameAndType = VtlFile.findVariableNameAndTypeAndScopeFilePath(text);
        if (nameAndType == null) {
            return;
        }

        final VtlImplicitVariable variable = ((VtlFile) element.getContainingFile()).findImplicitVariable(nameAndType[0]);
        if (variable == null || variable.getPsiType() instanceof PsiPrimitiveType) {
            return;
        }
        for (PsiReference javaRef : VtlReferenceContributor.getReferencesToJavaTypes(element)) {
            if(javaRef.resolve() == null) {
                TextRange range = javaRef.getRangeInElement().shiftRight(element.getTextRange().getStartOffset());
                holder.createErrorAnnotation(range, VelocityBundle.message("invalid.java.type"));
            }
        }
    }
}
