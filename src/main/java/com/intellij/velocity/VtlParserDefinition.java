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

import com.intellij.velocity.lexer.VtlLexer;
import com.intellij.velocity.psi.VtlCompositeElementType;
import com.intellij.velocity.psi.VtlElementTypes;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.parsers.VtlParser;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.*;
import consulo.language.file.FileViewProvider;
import consulo.language.lexer.Lexer;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.version.LanguageVersion;

import jakarta.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlParserDefinition implements ParserDefinition
{
	@Nonnull
	@Override
	public Language getLanguage()
	{
		return VtlLanguage.INSTANCE;
	}

	@Nonnull
	public Lexer createLexer(@Nonnull consulo.language.version.LanguageVersion languageVersion)
	{
		return new VtlLexer();
	}

	@Nonnull
	public PsiParser createParser(@Nonnull consulo.language.version.LanguageVersion languageVersion)
	{
		return new VtlParser();
	}

	@Nonnull
	public IFileElementType getFileNodeType()
	{
		return VtlElementTypes.VTL_FILE;
	}

	@Nonnull
	public consulo.language.ast.TokenSet getWhitespaceTokens(@Nonnull consulo.language.version.LanguageVersion languageVersion)
	{
		return consulo.language.ast.TokenSet.create(TokenType.WHITE_SPACE);
	}

	@Nonnull
	public consulo.language.ast.TokenSet getCommentTokens(@Nonnull LanguageVersion languageVersion)
	{
		return VtlElementTypes.COMMENTS;
	}

	@Nonnull
	public TokenSet getStringLiteralElements(@Nonnull consulo.language.version.LanguageVersion languageVersion)
	{
		return consulo.language.ast.TokenSet.EMPTY;
	}

	@Nonnull
	public PsiElement createElement(final ASTNode node)
	{
		final IElementType type = node.getElementType();
		if(type instanceof VtlCompositeElementType)
		{
			return ((VtlCompositeElementType) type).createPsiElement(node);
		}
		throw new AssertionError("Unknown type: " + type);
	}

	@Nonnull
	public PsiFile createFile(@Nonnull final FileViewProvider viewProvider)
	{
		return new VtlFile(viewProvider);
	}
}
