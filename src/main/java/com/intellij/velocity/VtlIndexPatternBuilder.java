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

import com.intellij.lexer.Lexer;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.search.IndexPatternBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.velocity.lexer.VtlLexer;
import com.intellij.velocity.psi.VtlCommentType;
import com.intellij.velocity.psi.VtlElementTypes;
import com.intellij.velocity.psi.files.VtlFile;

/**
 * @author Alexey Chmutov
 */
public class VtlIndexPatternBuilder implements IndexPatternBuilder {
    public Lexer getIndexingLexer(final PsiFile file) {
        return (file instanceof VtlFile) ? new VtlLexer() : null;
    }

    public TokenSet getCommentTokenSet(final PsiFile file) {
        return (file instanceof VtlFile) ? VtlElementTypes.COMMENTS : null;
    }

    public int getCommentStartDelta(final IElementType tokenType) {
        assert tokenType instanceof VtlCommentType;
        return ((VtlCommentType)tokenType).getStartDelta();
    }

    public int getCommentEndDelta(final IElementType tokenType) {
        assert tokenType instanceof VtlCommentType;
        return ((VtlCommentType)tokenType).getEndDelta();
    }
}
