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

import static com.intellij.velocity.editorActions.EditorUtil.getCharAt;
import static com.intellij.velocity.editorActions.EditorUtil.getElementType;
import static com.intellij.velocity.editorActions.EditorUtil.isNotVtlFile;
import static com.intellij.velocity.editorActions.EditorUtil.typeInStringAndMoveCaret;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.velocity.psi.VtlCompositeElementTypes;

/**
 * @author Alexey Chmutov
 */
public class VelocityTypedHandler extends TypedHandlerDelegate {

    public Result beforeCharTyped(final char c, final Project project, final Editor editor, final PsiFile file, final FileType fileType) {
        if (isNotVtlFile(file, editor)) {
            return Result.CONTINUE;
        }

        final Document document = editor.getDocument();
        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);

        if (c == '(') {
            final int offset = editor.getCaretModel().getOffset();
            documentManager.commitDocument(document);
            char charAtOffset = getCharAt(document, offset);
            if (charAtOffset == '(') {
                typeInStringAndMoveCaret(editor, offset + 1, "(");
                return Result.STOP;
            }
            PsiElement token = file.findElementAt(offset - 1);
            if (token == null) {
                return Result.CONTINUE;
            }
            String tokenText = token.getText().trim();
            if ("#foreach".equals(tokenText) || "#if".equals(tokenText) || "#macro".equals(tokenText)) {
                typeInStringAndMoveCaret(editor, offset + 1, "()\n#end ");
                return Result.STOP;
            }
        } else if(c == '}') {
            final int offset = editor.getCaretModel().getOffset();
            documentManager.commitDocument(document);
            if (getCharAt(document, offset) == '}') {
                editor.getCaretModel().moveToOffset(offset + 1);
                return Result.STOP;
            }
        }

        return Result.CONTINUE;
    }

    public Result charTyped(final char c, final Project project, final Editor editor, final PsiFile file) {
        if (isNotVtlFile(file, editor)) {
            return Result.CONTINUE;
        }

        final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
        final Document document = editor.getDocument();

        if (c == '{') {
            int offset = editor.getCaretModel().getOffset();
            documentManager.commitDocument(document);
            PsiElement element = file.findElementAt(offset - 1);
            if (element != null) {
                final String text = element.getText().trim();
                if ("${".equals(text) || "$!{".equals(text) || "#{".equals(text)) {
                    char charAtOffset = getCharAt(document, offset);
                    String textToInsert = charAtOffset == '{' ? "}$" : "}";
                    PsiElement parent = element.getParent();
                    if (getElementType(parent) == VtlCompositeElementTypes.INTERPOLATION) {
                        offset = parent.getTextOffset() + parent.getTextLength();
                    }
                    document.insertString(offset, textToInsert);
                    return Result.STOP;
                }
            }
        }
        return Result.CONTINUE;
    }

}
