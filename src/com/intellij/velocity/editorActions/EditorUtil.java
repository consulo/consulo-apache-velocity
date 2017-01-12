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

import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.velocity.psi.files.VtlFileViewProvider;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 25.04.2008
 */
class EditorUtil {

    private EditorUtil() {
    }

    static char getCharAt(Document document, int offset) {
        if (offset >= document.getTextLength()) {
            return 0;
        }
        return document.getCharsSequence().charAt(offset);
    }

    static void typeInStringAndMoveCaret(Editor editor, int offset, String str) {
        EditorModificationUtil.insertStringAtCaret(editor, str, true);
        editor.getCaretModel().moveToOffset(offset);
    }

    static IElementType getElementType(PsiElement element) {
        if (element == null) {
            return null;
        }
        ASTNode node = element.getNode();
        if (node == null) {
            return null;
        }
        return node.getElementType();
    }

    static boolean isNotVtlFile(final PsiFile file, final Editor editor) {
        return !(file.getViewProvider() instanceof VtlFileViewProvider)
                || editor.getCaretModel().getOffset() == 0;
    }


}
