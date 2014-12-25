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

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.velocity.lexer.VtlLexer;
import com.intellij.velocity.psi.VtlCompositeElementType;
import com.intellij.velocity.psi.VtlElementTypes;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.parsers.VtlParser;

/**
 * @author Alexey Chmutov
 */
public class VtlParserDefinition implements ParserDefinition {
    @NotNull
    public Lexer createLexer(final Project project, LanguageVersion languageVersion) {
        return new VtlLexer();
    }

    public PsiParser createParser(final Project project, LanguageVersion languageVersion) {
        return new VtlParser();
    }

    public IFileElementType getFileNodeType() {
        return VtlElementTypes.VTL_FILE;
    }

    @NotNull
    public TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
        return TokenSet.create(TokenType.WHITE_SPACE);
    }

    @NotNull
    public TokenSet getCommentTokens(LanguageVersion languageVersion) {
        return VtlElementTypes.COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiElement createElement(final ASTNode node) {
        final IElementType type = node.getElementType();
        if (type instanceof VtlCompositeElementType) {
            return ((VtlCompositeElementType) type).createPsiElement(node);
        }
        throw new AssertionError("Unknown type: " + type);
    }

    public PsiFile createFile(final FileViewProvider viewProvider) {
        return new VtlFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(final ASTNode left, final ASTNode right) {
        return SpaceRequirements.MAY;
    }

}
