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

import com.intellij.velocity.psi.VtlElementTypes;
import com.intellij.velocity.psi.VtlExpression;
import com.intellij.velocity.psi.VtlMacro;
import com.intellij.velocity.psi.VtlParameterDeclaration;
import com.intellij.velocity.psi.directives.VtlMacroImpl;
import com.intellij.velocity.psi.directives.VtlParse;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.editor.annotation.AnnotationHolder;
import consulo.language.editor.annotation.Annotator;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.resolve.BaseScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import org.jetbrains.annotations.NonNls;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.velocity.VelocityBundle.message;

/**
 * @author Alexey Chmutov
 */
public class VtlMacroAnnotator implements Annotator
{
    @NonNls
    private static final String V_IDENT = "([a-zA-Z_][a-zA-Z_0-9-]*)";
    @NonNls
    private static final Pattern WOULD_BE_MACRO_CALL_PATTERN = Pattern.compile("(#" + V_IDENT + ")|(#\\{" + V_IDENT + "\\})");

    public void annotate(final PsiElement element, final AnnotationHolder holder) {
        if (element instanceof VtlMacroImpl) {
            final VtlMacroImpl macro = (VtlMacroImpl) element;
            final VtlFile file = macro.getContainingFile();
            String macroName = macro.getName();
            if (macroName != null && file.getNumberOfMacros(macroName) > 1) {
                holder.createErrorAnnotation(macro.getNameElementRange(), message("macro.is.already.defined", macroName, file.getName()));
            }
        } else if (element instanceof VtlParameterDeclaration) {
            VtlParameterDeclaration param = (VtlParameterDeclaration) element;
            String paramName = param.getName();
            if (paramName == null) {
                return;
            }
            consulo.language.psi.PsiElement sibling = param.getPrevSibling();
            while (sibling != null) {
                if (sibling instanceof VtlParameterDeclaration
                        && paramName.equals(((VtlParameterDeclaration) sibling).getName())) {
                    final String msg = message("duplicated.parameter.name", paramName);
                    holder.createErrorAnnotation(param.getTextRange(), msg);
                    holder.createErrorAnnotation(sibling.getTextRange(), msg);
                }
                sibling = sibling.getPrevSibling();
            }
        } else if (element instanceof VtlParse) {
            final VtlParse parse = (VtlParse) element;
            VtlFile parsedFile = parse.resolveFile();
            if (parsedFile == null) {
                return;
            }
            final VtlExpression parsedFileElement = parse.getArgumentList().getArguments()[0];
            final VtlFile containingFile = parse.getContainingFile();
            for (String macroName : containingFile.getDefinedMacroNames()) {
                if (parsedFile.getNumberOfMacros(macroName) > 0) {
                    final String msg = message("macro.declaration.will.be.ignored", macroName, containingFile.getName(), parsedFile.getName());
                    holder.createWarningAnnotation(parsedFileElement, msg);
                }
            }
        } else {
            final ASTNode node = element.getNode();
            if (node == null || node.getElementType() != VtlElementTypes.TEMPLATE_TEXT) {
                return;
            }
            final Matcher matcher = WOULD_BE_MACRO_CALL_PATTERN.matcher(node.getText());
            if (!matcher.find()) {
                return;
            }
            final PsiFile file = element.getContainingFile();
            if (!(file instanceof VtlFile)) {
                return;
            }
            final VtlFile vtlFile = (VtlFile) file;
            do {
                int index = matcher.start(2) != -1 ? 2 : 4;
                final String macroName = matcher.group(index);
                final consulo.language.psi.resolve.BaseScopeProcessor processor = new BaseScopeProcessor() {
                    public boolean execute(PsiElement element, ResolveState state) {
                        return !(element instanceof VtlMacro && macroName.equals(((VtlMacro) element).getName()));
                    }
                };
                if (!vtlFile.processAllMacrosInScope(processor, consulo.language.psi.resolve.ResolveState.initial())) {
                    TextRange range = new consulo.document.util.TextRange(matcher.start(), matcher.end()).shiftRight(element.getTextOffset());
                    holder.createErrorAnnotation(range, message("will.be.considered.as.macro.call"));
                }
            } while (matcher.find());
        }
    }
}