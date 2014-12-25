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

package com.intellij.velocity;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.directives.VtlDirective;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.xml.breadcrumbs.BreadcrumbsInfoProvider;

/**
 * @author Alexey Chmutov
 */
public class VtlBreadcrumbsInfoProvider extends BreadcrumbsInfoProvider {
    public Language[] getLanguages() {
        return new Language[]{VtlLanguage.INSTANCE};
    }

    public boolean acceptElement(@NotNull final PsiElement e) {
        return e instanceof VtlDirective;
    }

    public PsiElement getParent(@NotNull final PsiElement e) {
        VtlDirective directive = PsiTreeUtil.getParentOfType(e, VtlDirective.class);
        return directive instanceof VtlFile ? null : directive;
    }

    @NotNull
    public String getElementInfo(@NotNull final PsiElement e) {
        return ((VtlDirective) e).getPresentableName();
    }

    public String getElementTooltip(@NotNull final PsiElement e) {
        return null;
    }
}
