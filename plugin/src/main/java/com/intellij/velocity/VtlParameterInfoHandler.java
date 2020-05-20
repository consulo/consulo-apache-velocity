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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.codeInsight.CodeInsightBundle;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.TypeConversionUtil;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlArgumentList;
import com.intellij.velocity.psi.VtlCallExpression;
import com.intellij.velocity.psi.VtlCallable;
import com.intellij.velocity.psi.VtlExpression;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.VtlMacro;
import com.intellij.velocity.psi.VtlMethodCallExpression;
import com.intellij.velocity.psi.VtlVariable;
import consulo.velocity.api.facade.VelocityType;

/**
 * @author Alexey Chmutov
 */
public class VtlParameterInfoHandler implements ParameterInfoHandler<VtlArgumentList, VtlCallable> {
    public boolean couldShowInLookup() {
        return true;
    }

    public Object[] getParametersForLookup(final LookupElement item, final ParameterInfoContext context) {
        //todo
        return null;
    }

    public Object[] getParametersForDocumentation(final VtlCallable callable, final ParameterInfoContext context) {
        return new Object[]{callable};
    }

    public VtlArgumentList findElementForParameterInfo(final CreateParameterInfoContext context) {
        return findArgumentList(context);
    }

    @Nullable
    private static VtlArgumentList findArgumentList(final ParameterInfoContext context) {
        final PsiFile file = context.getFile();

        PsiDocumentManager.getInstance(file.getProject()).commitDocument(context.getEditor().getDocument());
        final PsiElement elementAt = file.getViewProvider().findElementAt(context.getOffset(), VtlLanguage.INSTANCE);
        if (elementAt == null) {
            return null;
        }
        final VtlCallExpression call = PsiTreeUtil.getParentOfType(elementAt, VtlCallExpression.class);
        if (call == null) {
            return null;
        }
        return call.findArgumentList();
    }

    public void showParameterInfo(@Nonnull final VtlArgumentList element, final CreateParameterInfoContext context) {
        final PsiElement parent = element.getParent();
        if (!(parent instanceof VtlCallExpression)) {
            return;
        }
        final VtlCallable[] candidates = ((VtlCallExpression) parent).getCallableCandidates();
        if (candidates.length == 0) {
            return;
        }
        context.setItemsToShow(candidates);
        context.showHint(element, element.getTextRange().getStartOffset(), this);
    }

    public VtlArgumentList findElementForUpdatingParameterInfo(final UpdateParameterInfoContext context) {
        return findArgumentList(context);
    }

    public void updateParameterInfo(@Nonnull final VtlArgumentList argumentList, final UpdateParameterInfoContext context) {
        assert argumentList.isValid();
        PsiElement prevOwner = context.getParameterOwner();
        int prevOwnerTextOffset = prevOwner == null ? 0 : prevOwner.getTextOffset();

        context.setParameterOwner(argumentList);
        if (prevOwnerTextOffset != 0 && prevOwnerTextOffset != argumentList.getTextOffset()) {
            context.removeHint();
            context.setCurrentParameter(-1);
            return;
        }
        int offset = context.getEditor().getCaretModel().getOffset() - argumentList.getTextRange().getStartOffset();
        if (offset < 0) {
            context.setCurrentParameter(-1);
            return;
        }
        int index = 0;
        for (final ASTNode child : argumentList.getNode().getChildren(null)) {
            final PsiElement psiChild = child.getPsi();
            offset -= child.getTextLength();
            if (!(psiChild instanceof VtlExpression)) {
                continue;
            }
            if (offset <= 0) {
                break;
            }
            index++;
        }
        context.setCurrentParameter(index);
    }

    public String getParameterCloseChars() {
        return ", )";
    }

    public boolean tracksParameterIndex() {
        return false;
    }

    public void updateUI(final VtlCallable callable, final ParameterInfoUIContext context) {
        PsiElement list = context.getParameterOwner();
        if (!list.isValid()) {
            return;
        }

        final int index = context.getCurrentParameterIndex();
        boolean applicable = isApplicable(callable, list, index);

        int highlightStart = -1;
        int highlightEnd = -1;

        StringBuilder sb = new StringBuilder();
        final VtlVariable[] variables = callable.getParameters();
        boolean isMacro = callable instanceof VtlMacro;
        String delimiter = isMacro ? " " : ", ";
        for (int i = 0; i < variables.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            if (i == index) {
                highlightStart = sb.length();
            }
            VtlVariable variable = variables[i];
            VelocityType type = variable.getPsiType();
            if (type != null) {
                sb.append(type.getPresentableText()).append(" ");
            }
            if (isMacro) {
                sb.append('$');
            }
            sb.append(variable.getName());
            if (i == index) {
                highlightEnd = sb.length();
            }
        }

        if (variables.length == 0) {
            sb.append(CodeInsightBundle.message("parameter.info.no.parameters"));
        }

        final boolean deprecated = callable.isDeprecated();
        context.setupUIComponentPresentation(sb.toString(), highlightStart, highlightEnd, !applicable, deprecated, false, context.getDefaultParameterColor());
    }

    private static boolean isApplicable(final VtlCallable callable, final PsiElement list, final int index) {
        boolean applicable = callable.getParameters().length > index || index == 0 && callable.getParameters().length == 0;
        final PsiElement parent = list.getParent();
        if (applicable && parent instanceof VtlMethodCallExpression) {
            VtlExpression[] arguments = ((VtlMethodCallExpression) parent).getArgumentList().getArguments();
            VtlVariable[] parameters = callable.getParameters();
            for (int i = 0; i < index; i++) {
                VelocityType paramType = parameters[i].getPsiType();
				VelocityType argType = arguments[i].getPsiType();
                if (argType == null && paramType == null) {
                    continue;
                }
                paramType = PsiUtil.getBoxedType(paramType, list);
                if (argType == null || paramType == null ||
                        !TypeConversionUtil.areTypesConvertible(argType, paramType)) {
                    return false;
                }
            }
        }
        return applicable;
    }

}