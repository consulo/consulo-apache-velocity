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

import com.intellij.velocity.psi.VtlCompositeStarterTokenType;
import consulo.language.ast.IElementType;
import consulo.language.parser.PsiBuilder;

import static com.intellij.velocity.psi.VtlElementTypes.*;


/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.03.2008
 */
public class ElseDirectiveBodyParser extends CompositeBodyParser {

    public static final ElseDirectiveBodyParser INSTANCE = new ElseDirectiveBodyParser();

    private ElseDirectiveBodyParser() {}

    static final CompositeEndDetector elseCompositeEndDetector = new CompositeEndDetector() {
        @Override
        public boolean isCompositeFinished(PsiBuilder builder) {
            IElementType tokenType = builder.getTokenType();
            return tokenType == SHARP_ELSE || tokenType == SHARP_ELSEIF
                    || builder.getTokenType() == SHARP_END;
        }

        @Override
        public boolean isTokenInvalid(consulo.language.ast.IElementType tokenType) {
            return !(tokenType == TEMPLATE_TEXT || tokenType == SHARP_STOP
                    || tokenType instanceof VtlCompositeStarterTokenType);
        }
    };

    public void parseBody(PsiBuilder builder, PsiBuilder.Marker bodyMarker) {
        VtlParser.parseCompositeElements(builder, elseCompositeEndDetector);
        bodyMarker.done(DIRECTIVE_ELSE);
    }
    
}
