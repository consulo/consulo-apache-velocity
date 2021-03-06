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

import static com.intellij.velocity.psi.VtlElementTypes.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

/**
 * @author Alexey Chmutov
 */
public class VelocityPairedBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = new BracePair[] {
            new BracePair(SHARP_FOREACH, SHARP_END, true),
            new BracePair(SHARP_IF, SHARP_END, true),
            new BracePair(SHARP_MACRODECL, SHARP_END, true),
            new BracePair(SHARP_DEFINE, SHARP_END, true),
            new BracePair(SHARP_LITERAL, SHARP_END, true),
            new BracePair(START_REF_FORMAL, RIGHT_BRACE, false),
            new BracePair(LEFT_BRACE, RIGHT_BRACE, false),
            new BracePair(LEFT_BRACE_IN_EXPR, RIGHT_BRACE_IN_EXPR, false),
            new BracePair(LEFT_PAREN, RIGHT_PAREN, false),
            new BracePair(LEFT_BRACKET, RIGHT_BRACKET, false),
    };

    public BracePair[] getPairs() {
        return PAIRS;
    }

    public boolean isPairedBracesAllowedBeforeType(@Nonnull final IElementType lbraceType, @Nullable final IElementType type) {
        return lbraceType == LEFT_PAREN && type == null;
    }

    public int getCodeConstructStart(final PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }

}
