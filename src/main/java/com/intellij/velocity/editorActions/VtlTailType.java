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

import consulo.language.editor.completion.lookup.TailType;
import consulo.document.Document;
import consulo.codeEditor.Editor;

/**
 * @author Alexey Chmutov
 */
public class VtlTailType extends TailType
{

    public static final TailType METHOD_CALL_TAIL_TYPE = new VtlTailType(false);

    private final boolean myClosingBraceNeeded;

    public VtlTailType(boolean closingBraceNeeded) {
        myClosingBraceNeeded = closingBraceNeeded;
    }

    public int processTail(final Editor editor, final int tailOffset) {
        Document document = editor.getDocument();
        int textLength = document.getTextLength();
        CharSequence chars = document.getCharsSequence();
        int offsetToInsertOpeningParen = tailOffset;
        if (myClosingBraceNeeded) {
            if (offsetToInsertOpeningParen == textLength || chars.charAt(offsetToInsertOpeningParen) != '}') {
                document.insertString(offsetToInsertOpeningParen, "}");
                textLength++;
            }
            offsetToInsertOpeningParen++;
        }
        int existingOpeningParenOffset = offsetToInsertOpeningParen;
        if (openingParenNeeded()) {
            while (existingOpeningParenOffset < textLength && chars.charAt(existingOpeningParenOffset) == ' ') {
                existingOpeningParenOffset++;
            }
            if (existingOpeningParenOffset == textLength || chars.charAt(existingOpeningParenOffset) != '(') {
                document.insertString(offsetToInsertOpeningParen, "()");
                existingOpeningParenOffset = offsetToInsertOpeningParen;
            }
            existingOpeningParenOffset++;
        }
        return moveCaret(editor, tailOffset, existingOpeningParenOffset - tailOffset);
    }

    protected boolean openingParenNeeded() {
        return true;
    }
}
