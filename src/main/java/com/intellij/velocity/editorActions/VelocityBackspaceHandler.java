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

import com.intellij.velocity.psi.VtlCompositeElementTypes;
import com.intellij.velocity.psi.files.VtlFileViewProvider;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.document.Document;
import consulo.language.editor.action.BackspaceHandlerDelegate;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;

import static com.intellij.velocity.editorActions.EditorUtil.getCharAt;
import static com.intellij.velocity.editorActions.EditorUtil.getElementType;


/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VelocityBackspaceHandler extends BackspaceHandlerDelegate {
    private int pairedBraceOffset;

    public void beforeCharDeleted(final char c, final consulo.language.psi.PsiFile file, final Editor editor) {
        pairedBraceOffset = -1;
        if (c != '{') {
            return;
        }

        final int offset = editor.getCaretModel().getOffset() - 1;
        if (!(file.getViewProvider() instanceof VtlFileViewProvider) || offset == 0) {
            return;
        }

        final consulo.document.Document document = editor.getDocument();
        PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
        final PsiElement element = file.findElementAt(offset);
        if (element == null) {
            return;
        }
        PsiElement parent = element.getParent();
        if (getElementType(parent) == VtlCompositeElementTypes.INTERPOLATION) {
            pairedBraceOffset = parent.getTextOffset() + parent.getTextLength() - 1;
        } else {
            final String text = element.getText().trim();
            if ("${".startsWith(text) || "#{".startsWith(text)
                    || "${}".startsWith(text) || "#{}".startsWith(text)) {
                pairedBraceOffset = offset + 1;
            }
        }
        if(pairedBraceOffset != -1 && '}' != getCharAt(document, pairedBraceOffset)) {
            pairedBraceOffset = -1;
        }

    }

    public boolean charDeleted(final char c, final PsiFile file, final Editor editor) {
        if (pairedBraceOffset != -1) {
            final int offset = editor.getCaretModel().getOffset();
            final Document doc = editor.getDocument();
            if (pairedBraceOffset - offset == 1) {
                doc.deleteString(pairedBraceOffset - 2, pairedBraceOffset);
            } else {
                doc.deleteString(pairedBraceOffset - 1, pairedBraceOffset);
            }
            pairedBraceOffset = -1;
            return true;
        }
        return false;
    }
}