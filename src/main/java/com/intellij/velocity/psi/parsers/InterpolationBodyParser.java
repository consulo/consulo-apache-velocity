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

import static com.intellij.velocity.psi.VtlElementTypes.IDENTIFIER;
import static com.intellij.velocity.psi.VtlElementTypes.INTERPOLATION;
import static com.intellij.velocity.psi.VtlElementTypes.JAVA_DOT;
import static com.intellij.velocity.psi.VtlElementTypes.LEFT_PAREN;
import static com.intellij.velocity.psi.VtlElementTypes.METHOD_CALL_EXPRESSION;
import static com.intellij.velocity.psi.VtlElementTypes.REFERENCE_EXPRESSION;

import consulo.language.parser.PsiBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.03.2008
 */
public class InterpolationBodyParser extends CompositeBodyParser {

    public static final InterpolationBodyParser INSTANCE = new InterpolationBodyParser();

    private InterpolationBodyParser() {}

    public void parseBody(PsiBuilder builder, PsiBuilder.Marker bodyMarker) {
        parseBodyInternal(builder);
        if(bodyMarker != null) {
            bodyMarker.done(INTERPOLATION);
        }
    }

    static void parseBodyInternal(PsiBuilder builder) {
        PsiBuilder.Marker referenceExpression = builder.mark();
        if(assertToken(builder, IDENTIFIER, REFERENCE_EXPRESSION)) {
          while(builder.getTokenType() == JAVA_DOT) {
              builder.advanceLexer();
              if(!assertToken(builder, IDENTIFIER)) {
                break;
              }
              referenceExpression.done(REFERENCE_EXPRESSION);
              if(builder.getTokenType() == LEFT_PAREN) {
                  referenceExpression = referenceExpression.precede();
                  parseArgumentList(builder, true);
                  referenceExpression.done(METHOD_CALL_EXPRESSION);
              }
              referenceExpression = referenceExpression.precede();
          }
        }
        referenceExpression.drop();
    }

}