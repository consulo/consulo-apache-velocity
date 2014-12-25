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

package com.intellij.velocity.psi.directives;

import static com.intellij.velocity.psi.VtlElementTypes.SHARP_DEFINE;
import static com.intellij.velocity.psi.VtlElementTypes.SHARP_ELSE;
import static com.intellij.velocity.psi.VtlElementTypes.SHARP_ELSEIF;
import static com.intellij.velocity.psi.VtlElementTypes.SHARP_FOREACH;
import static com.intellij.velocity.psi.VtlElementTypes.SHARP_IF;
import static com.intellij.velocity.psi.VtlElementTypes.SHARP_LITERAL;
import static com.intellij.velocity.psi.VtlElementTypes.SHARP_MACRODECL;

import com.intellij.psi.PsiElement;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.files.VtlFile;

/**
 * @author Alexey Chmutov
 */
public interface VtlDirective extends VtlDirectiveHolder {
    String[] DIRECTIVE_NAMES = {
            "break", "define", "else", "elseif", "end", "evaluate", "foreach", "if", "include", "literal", "macro", "parse", "set", "stop",
    };

    int getFoldingStartOffset();

    int getFoldingEndOffset();

    boolean needsClosing();

    class Validator {
        private Validator() {
        }

        public static boolean areParenthesesNeeded(final VtlDirective child, final String childName) {
            return !("break".equals(childName) || "else".equals(childName) || "end".equals(childName) || "stop".equals(childName));
        }

        public static boolean isAllowed(final VtlDirective child, final String childName) {
            PsiElement parent = child.getParent();
            if ("macro".equals(childName)) {
                return parent instanceof VtlFile;
            }
            if ("end".equals(childName)) {
                return PsiUtil.isTypeOf(parent, SHARP_FOREACH, SHARP_IF, SHARP_LITERAL, SHARP_MACRODECL, SHARP_DEFINE)
                        || PsiUtil.isTypeOf(parent.getParent(), SHARP_IF)
                        && PsiUtil.isTypeOf(parent, SHARP_ELSE, SHARP_ELSEIF);
            }
            if ("else".equals(childName) || "elseif".equals(childName)) {
                return PsiUtil.isTypeOf(parent, SHARP_IF)
                        || PsiUtil.isTypeOf(parent.getParent(), SHARP_IF)
                        && PsiUtil.isTypeOf(parent, SHARP_ELSEIF);
            }
            if ("break".equals(childName)) {
                return PsiUtil.isTypeOf(parent, SHARP_FOREACH);
            }
            return true;
        }
    }
}
