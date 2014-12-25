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
package com.intellij.velocity.editorActions;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.VtlVariable;

/**
 * @author Alexey Chmutov
 */
public class VtlFindUsagesProvider implements FindUsagesProvider {
    public WordsScanner getWordsScanner() {
        return null;
    }

    public boolean canFindUsagesFor(@NotNull final PsiElement psiElement) {
        return psiElement instanceof VtlVariable;
    }

    public String getHelpId(@NotNull final PsiElement psiElement) {
        return null;
    }

    @NotNull
    public String getType(@NotNull final PsiElement element) {
        return VelocityBundle.message("type.name.variable");
    }

    @NotNull
    public String getDescriptiveName(@NotNull final PsiElement element) {
        return VelocityBundle.message("type.name.variable");
    }

    @NotNull
    public String getNodeText(@NotNull final PsiElement element, final boolean useFullName) {
        if (element instanceof VtlVariable) {
            return ((VtlVariable) element).getName();
        }
        return element.getText();
    }
}
