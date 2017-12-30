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

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.impl.MacroCallNode;
import com.intellij.codeInsight.template.macro.CompleteSmartMacro;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.directives.VtlMacroCall;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import org.jetbrains.annotations.NotNull;

/**
 * @author Alexey Chmutov
 */
public abstract class DefineImplicitVariableIntention extends DefineInCommentIntention {
    public DefineImplicitVariableIntention(@NotNull String text) {
        super(text, VelocityBundle.message("add.implicit.variable.fix.name"));
    }

    protected void prepareTemplate(@NotNull Template template, @NotNull final PsiElement element, String relativePath, @NotNull PsiFile fileToInsertComment) {
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
    protected boolean isAvailable(@NotNull VtlReferenceExpression ref) {
        return !(ref.getParent() instanceof VtlMacroCall) && !ref.hasQualifier();
    }

    public static class Local extends DefineImplicitVariableIntention {
        public Local() {
            super(VelocityBundle.message("add.implicit.variable.fix.name.local"));
        }

        public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
            defineInComment(editor, file, file, false);
        }
    }

    public static class LocalExternal extends DefineImplicitVariableIntention {
        public LocalExternal() {
            super(VelocityBundle.message("add.implicit.variable.fix.name.external"));
        }

        public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
            chooseTargetFile(file, editor, true);
        }
    }

    public static class ModuleWide extends DefineImplicitVariableIntention {
        public ModuleWide() {
            super(VelocityBundle.message("add.implicit.variable.fix.name.module.wide"));
        }

        public void invoke(@NotNull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
            chooseTargetFile(file, editor, false);
        }
    }
}