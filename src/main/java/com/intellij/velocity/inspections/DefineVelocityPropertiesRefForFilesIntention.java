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

import com.intellij.lang.properties.PropertiesLanguage;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.velocity.psi.files.VelocityPropertiesProvider;
import com.intellij.velocity.psi.reference.VtlFileReferenceSet;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.language.editor.template.Expression;
import consulo.language.editor.template.Template;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.path.FileReference;
import consulo.language.util.ModuleUtilCore;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import static com.intellij.velocity.inspections.Util.*;

/**
 * @author Alexey Chmutov
 */
public abstract class DefineVelocityPropertiesRefForFilesIntention extends DefineInCommentIntention {
    public DefineVelocityPropertiesRefForFilesIntention(@Nonnull LocalizeValue text) {
        super(text, VelocityLocalize.addVelocityPropertiesRefFixName());
    }

    @Override
    @Nullable
    protected consulo.language.psi.PsiElement getReferenceElement(@Nonnull final consulo.codeEditor.Editor editor, @Nonnull final PsiFile file) {
        FileReference ref = findReferenceExpression(editor, file, consulo.language.psi.path.FileReference.class);
        return ref != null && ref.resolve() == null && canSetVelocityProperties(file) ? ref.getElement() : null;
    }

    protected void prepareTemplate(@Nonnull Template template,
                                   @Nonnull final PsiElement element,
                                   String relativePath,
                                   @Nonnull final PsiFile fileToInsertComment) {
        final List<String> allFiles = computeFilePaths(element, fileToInsertComment);
        template.addTextSegment("#* @velocityproperties path=");
        final Expression pathExpression = new StringCollectionExpression(allFiles);
        template.addVariable("PATH", pathExpression, pathExpression, true);
        final String fileRef = relativePath != null ? " file=\"" + relativePath + "\"" : "";
        template.addTextSegment(fileRef + " *#\n");
        template.addEndVariable();
    }

    private static List<String> computeFilePaths(final consulo.language.psi.PsiElement element, final PsiFile fileToInsertComment) {
        final VtlFileReferenceSet refSet = findVtlFileReferenceSet(element);
        if (refSet == null) {
            return Collections.emptyList();
        }
        final PsiFile[] referencedFiles =
            findReferencedFiles(ModuleUtilCore.findModuleForPsiElement(element), refSet.getLastReference().getCanonicalText());

        if (referencedFiles.length == 0) {
            return Collections.emptyList();
        }

        return collectFilePaths(element, psiFile ->
        {
            PsiFile file = psiFile.getViewProvider().getPsi(PropertiesLanguage.INSTANCE);
            if (file instanceof PropertiesFile) {
                PropertiesFile propFile = (PropertiesFile) file;
                VelocityPropertiesProvider velocityProperties = new VelocityPropertiesProvider(propFile);
                for (PsiFile referencedFile : referencedFiles) {
                    String referencedFilePath = referencedFile.getViewProvider().getVirtualFile().getPath();
                    String filePath = computeFilePath(velocityProperties, referencedFilePath, refSet.getPathString(), fileToInsertComment);
                    if (filePath != null) {
                        return filePath;
                    }
                }
            }
            return null;
        });
    }

}