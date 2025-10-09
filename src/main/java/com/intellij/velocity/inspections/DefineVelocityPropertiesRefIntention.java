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
import com.intellij.velocity.psi.directives.VtlMacroCall;
import com.intellij.velocity.psi.files.VelocityPropertiesProvider;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.language.editor.template.Expression;
import consulo.language.editor.template.Template;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.util.ModuleUtilCore;
import consulo.localize.LocalizeValue;
import consulo.module.Module;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Chmutov
 */
public abstract class DefineVelocityPropertiesRefIntention extends DefineInCommentIntention {
    public DefineVelocityPropertiesRefIntention(@Nonnull LocalizeValue text) {
        super(text, VelocityLocalize.addVelocityPropertiesRefFixName());
    }

    @Override
    protected boolean isAvailable(@Nonnull VtlReferenceExpression ref) {
        return ref.getParent() instanceof VtlMacroCall;
    }

    protected void prepareTemplate(@Nonnull Template template,
                                   @Nonnull final PsiElement element,
                                   String relativePath,
                                   @Nonnull final consulo.language.psi.PsiFile fileToInsertComment) {
        final List<String> allFiles = Util.collectFilePaths(element, psiFile ->
        {
            PsiFile file = psiFile.getViewProvider().getPsi(PropertiesLanguage.INSTANCE);
            if (!(file instanceof PropertiesFile)) {
                return null;
            }
            final VelocityPropertiesProvider propertiesProvider = new VelocityPropertiesProvider((PropertiesFile) file);
            List<PsiFile> macroLibs = collectReferencedLibFiles(ModuleUtilCore.findModuleForPsiElement(element), propertiesProvider);

            for (PsiFile macroLib : macroLibs) {
                if (!(macroLib instanceof VtlFile) ||
                    ((VtlFile) macroLib).getNumberOfMacros(((VtlReferenceExpression) element).getReferenceName()) <= 0) {
                    continue;
                }
                VirtualFile vFile = macroLib.getViewProvider().getVirtualFile();
                String res = Util.computeFilePath(propertiesProvider, vFile.getPath(), vFile.getName(), fileToInsertComment);
                if (res != null) {
                    return res;
                }
            }
            return null;
        });

        template.addTextSegment("#* @velocityproperties path=");
        final Expression pathExpression = new StringCollectionExpression(allFiles);
        template.addVariable("PATH", pathExpression, pathExpression, true);
        final String fileRef = relativePath != null ? " file=\"" + relativePath + "\"" : "";
        template.addTextSegment(fileRef + " *#\n");
        template.addEndVariable();
    }

    private static List<PsiFile> collectReferencedLibFiles(Module module, VelocityPropertiesProvider propertiesProvider) {
        String[] libNames = propertiesProvider.getVelocimacroLibraryNames();
        if (libNames.length == 0) {
            return Collections.emptyList();
        }
        List<consulo.language.psi.PsiFile> files = new ArrayList<PsiFile>(libNames.length);
        for (String libName : libNames) {
            files.addAll(Arrays.asList(Util.findReferencedFiles(module, libName)));
        }
        return files;
    }
}