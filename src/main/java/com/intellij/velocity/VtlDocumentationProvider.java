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

import java.util.List;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.lang.java.JavaDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.beanProperties.BeanPropertyElement;
import com.intellij.velocity.psi.VtlVariable;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 23.06.2008
 */
public class VtlDocumentationProvider implements DocumentationProvider {
    public String getQuickNavigateInfo(final PsiElement element, PsiElement element1) {
        if (element instanceof VtlVariable) {
            final VtlVariable variable = (VtlVariable) element;
            final String name = variable.getName();
            PsiType type = variable.getPsiType();
            if (type != null) {
                return type.getPresentableText() + " " + name;
            }
            return name;
        }
        if (element instanceof BeanPropertyElement) {
            return JavaDocumentationProvider.generateMethodInfo(((BeanPropertyElement) element).getMethod(), PsiSubstitutor.EMPTY);
        }
        return null;
    }

    public List<String> getUrlFor(final PsiElement element, final PsiElement originalElement) {
        return null;
    }

    public String generateDoc(final PsiElement element, final PsiElement originalElement) {
        if (element instanceof BeanPropertyElement) {
            return JavaDocumentationProvider.generateExternalJavadoc(((BeanPropertyElement) element).getMethod());
        }
        return null;
    }

    public PsiElement getDocumentationElementForLookupItem(final PsiManager psiManager, final Object object, final PsiElement element) {
        if (object instanceof VtlVariable || object instanceof BeanPropertyElement) {
            return (PsiElement) object;
        }
        return null;
    }

    public PsiElement getDocumentationElementForLink(final PsiManager psiManager, final String link, final PsiElement context) {
        return null;
    }
}

