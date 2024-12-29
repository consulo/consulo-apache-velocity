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
import com.intellij.velocity.psi.directives.VtlMacroCall;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.language.editor.template.Expression;
import consulo.language.editor.template.Template;
import consulo.language.editor.template.macro.CompleteSmartMacro;
import consulo.language.editor.template.macro.MacroCallNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;

import jakarta.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
public abstract class DefineImplicitVariableIntention extends DefineInCommentIntention {
    public DefineImplicitVariableIntention(@Nonnull String text) {
        super(text, VelocityBundle.message("add.implicit.variable.fix.name"));
    }

    protected void prepareTemplate(@Nonnull Template template, @Nonnull final PsiElement element, String relativePath, @Nonnull PsiFile fileToInsertComment) {
        final String varName = element.getText();
        assert varName != null;

        final Expression typeExpression = new MacroCallNode(new CompleteSmartMacro());

        template.addTextSegment("#* @vtlvariable name=\"" + varName + "\" type=\"");
        template.addVariable("PATH", typeExpression, typeExpression, true);

        final String fileRef = relativePath != null ? " file=\"" + relativePath + "\"" : "";
        template.addTextSegment("\"" + fileRef + " *#\n");
        template.addEndVariable();
    }

    @Override
    protected boolean isAvailable(@Nonnull VtlReferenceExpression ref) {
        return !(ref.getParent() instanceof VtlMacroCall) && !ref.hasQualifier();
    }
}