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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import consulo.language.psi.PsiElement;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlArgumentList;
import com.intellij.velocity.psi.VtlLiteralExpressionType;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.language.ast.ASTNode;

/**
 * @author Alexey Chmutov
 */
public class VtlParse extends VtlFileReferenceDirective {
    public VtlParse(@Nonnull final ASTNode node) {
        super(node, "parse");
    }

    @Nullable
    public VtlFile resolveFile() {
        VtlArgumentList argumentList = getArgumentList();
        if (argumentList == null) {
            return null;
        }
        final PsiElement literal = argumentList.getFirstChild();
        if (literal == null) {
            return null;
        }
        if (!(literal instanceof VtlLiteralExpressionType.VtlStringLiteral)) {
            return null;
        }
        return PsiUtil.findFile(literal.getReferences(), VtlFile.class);
    }
}
