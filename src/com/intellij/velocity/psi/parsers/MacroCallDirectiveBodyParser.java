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

package com.intellij.velocity.psi.parsers;

import static com.intellij.velocity.psi.VtlElementTypes.DIRECTIVE_MACROCALL;
import static com.intellij.velocity.psi.VtlElementTypes.IDENTIFIER;
import static com.intellij.velocity.psi.VtlElementTypes.LEFT_BRACE;
import static com.intellij.velocity.psi.VtlElementTypes.LEFT_PAREN;
import static com.intellij.velocity.psi.VtlElementTypes.REFERENCE_EXPRESSION;
import static com.intellij.velocity.psi.VtlElementTypes.RIGHT_BRACE;

import com.intellij.lang.PsiBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.03.2008
 */
public class MacroCallDirectiveBodyParser extends CompositeBodyParser {
    public static final MacroCallDirectiveBodyParser INSTANCE = new MacroCallDirectiveBodyParser();

    private MacroCallDirectiveBodyParser() {}

    public void parseBody(PsiBuilder builder, PsiBuilder.Marker bodyMarker) {
        boolean leftBracePresent = consumeTokenIfPresent(builder, LEFT_BRACE);
        assertToken(builder, IDENTIFIER, REFERENCE_EXPRESSION);
        if (leftBracePresent) {
            assertToken(builder, RIGHT_BRACE);
        }
        if (builder.getTokenType() == LEFT_PAREN) {
            parseArgumentList(builder, false);
        }
        bodyMarker.done(DIRECTIVE_MACROCALL);
    }
}
