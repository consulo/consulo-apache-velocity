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

import static com.intellij.velocity.psi.VtlElementTypes.CHAR_ESCAPE;
import static com.intellij.velocity.psi.VtlElementTypes.DOUBLE_QUOTE;
import static com.intellij.velocity.psi.VtlElementTypes.SINGLE_QUOTE;
import static com.intellij.velocity.psi.VtlElementTypes.STRING_TEXT;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.tree.IElementType;


/**
 * @author Alexey Chmutov
 */
public class VelocityQuoteHandler extends SimpleTokenSetQuoteHandler {
    public VelocityQuoteHandler() {
        super(SINGLE_QUOTE, DOUBLE_QUOTE);
    }

    public boolean isClosingQuote(final HighlighterIterator iterator, final int offset) {
        if (!myLiteralTokenSet.contains(iterator.getTokenType()) || iterator.getEnd() - iterator.getStart() != 1) {
            return false;
        }
        return !isOpeningQuoteInternal(iterator);
    }

    public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
        if (!myLiteralTokenSet.contains(iterator.getTokenType()) || offset != iterator.getStart()) {
            return false;
        }
        return isOpeningQuoteInternal(iterator);
    }

    private boolean isOpeningQuoteInternal(final HighlighterIterator iterator) {
        iterator.retreat();
        try {
            if (iterator.atEnd()) {
                return true;
            }
            final IElementType type = iterator.getTokenType();
            return !(myLiteralTokenSet.contains(type) || STRING_TEXT.equals(type) || CHAR_ESCAPE.equals(type));
        } finally {
            iterator.advance();
        }
    }

    public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset) {
        int start = iterator.getStart();
        try {
            Document doc = editor.getDocument();
            CharSequence chars = doc.getCharsSequence();
            int lineEnd = doc.getLineEndOffset(doc.getLineNumber(offset));

            while (!iterator.atEnd() && iterator.getStart() < lineEnd) {
                IElementType tokenType = iterator.getTokenType();

                if (myLiteralTokenSet.contains(tokenType) &&
                        (iterator.getStart() >= iterator.getEnd() - 1
                         || chars.charAt(iterator.getEnd() - 1) != '\"' && chars.charAt(iterator.getEnd() - 1) != '\'')) {
                    return true;
                }

                iterator.advance();
            }
        } finally {
            while (iterator.atEnd() || iterator.getStart() != start) {
                iterator.retreat();
            }
        }

        return false;
    }

}
