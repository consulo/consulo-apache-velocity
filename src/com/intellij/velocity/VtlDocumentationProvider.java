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

