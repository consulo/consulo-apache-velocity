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

import consulo.language.parser.PsiBuilder;
import com.intellij.velocity.VelocityBundle;
import static com.intellij.velocity.psi.VtlElementTypes.*;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.03.2008
 */
public class ForeachDirectiveBodyParser extends CompositeBodyParser {

    public static final ForeachDirectiveBodyParser INSTANCE = new ForeachDirectiveBodyParser();

    private ForeachDirectiveBodyParser() {}

    public void parseBody(PsiBuilder builder, PsiBuilder.Marker bodyMarker) {
        PsiBuilder.Marker directiveHeader = builder.mark();
        incrementForeachCounter(builder);
        String errorMsg = VelocityBundle.message("loop.variable.expected");
        if(assertToken(builder, LEFT_PAREN)) {
            assertVariable(builder, LOOP_VARIABLE, errorMsg);
            assertToken(builder, IN);
            VtlParser.parseOperand(builder, false);
            assertToken(builder, RIGHT_PAREN);
        } else {
            builder.error(errorMsg);
        }
        directiveHeader.done(DIR_HEADER);
        VtlParser.parseCompositeElements(builder, COMMON_END_DETECTOR);
        finishCompositeWithEnd(builder, bodyMarker, DIRECTIVE_FOREACH);
        decrementForeachCounter(builder);
    }

}
