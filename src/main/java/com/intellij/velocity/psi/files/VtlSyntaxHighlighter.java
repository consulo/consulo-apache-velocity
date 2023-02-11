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

package com.intellij.velocity.psi.files;

import com.intellij.velocity.lexer.VtlLexer;
import com.intellij.velocity.psi.VtlElementTypes;
import consulo.codeEditor.DefaultLanguageHighlighterColors;
import consulo.codeEditor.HighlighterColors;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.ast.TokenType;
import consulo.language.editor.highlight.SyntaxHighlighterBase;
import consulo.language.lexer.Lexer;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Chmutov
 */
public class VtlSyntaxHighlighter extends consulo.language.editor.highlight.SyntaxHighlighterBase
{
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_DOT = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_DOT", consulo.codeEditor
			.DefaultLanguageHighlighterColors.DOT);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_PARENTHS = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_PARENTHS", DefaultLanguageHighlighterColors
			.PARENTHESES);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_BRACKETS = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_BRACKETS", DefaultLanguageHighlighterColors
			.BRACKETS);
	public static final TextAttributesKey VELOCITY_BRACES = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_BRACES", DefaultLanguageHighlighterColors.BRACES);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_OPERATION_SIGN = TextAttributesKey.createTextAttributesKey("VELOCITY_OPERATION_SIGN", consulo.codeEditor
			.DefaultLanguageHighlighterColors.OPERATION_SIGN);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_STRING = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_STRING", DefaultLanguageHighlighterColors
			.STRING);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_ESCAPE = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_ESCAPE", DefaultLanguageHighlighterColors
			.VALID_STRING_ESCAPE);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_NUMBER = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_NUMBER", DefaultLanguageHighlighterColors
			.NUMBER);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_KEYWORD = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_KEYWORD", DefaultLanguageHighlighterColors
			.KEYWORD);
	public static final TextAttributesKey VELOCITY_COMMA = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_COMMA", consulo.codeEditor.DefaultLanguageHighlighterColors.COMMA);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_SEMICOLON = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_SEMICOLON", consulo.codeEditor
			.DefaultLanguageHighlighterColors.SEMICOLON);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_DIRECTIVE = TextAttributesKey.createTextAttributesKey("VELOCITY_DIRECTIVE", DefaultLanguageHighlighterColors.MARKUP_TAG);
	public static final consulo.colorScheme.TextAttributesKey VELOCITY_REFERENCE = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_REFERENCE", consulo.codeEditor
			.DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
	public static final TextAttributesKey VELOCITY_COMMENT = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_COMMENT", consulo.codeEditor.DefaultLanguageHighlighterColors
			.BLOCK_COMMENT);
	public static final TextAttributesKey VELOCITY_BAD_CHARACTER = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
	public static final TextAttributesKey VELOCITY_SCRIPTING_BACKGROUND = consulo.colorScheme.TextAttributesKey.createTextAttributesKey("VELOCITY_SCRIPTING_BACKGROUND", consulo.codeEditor
			.DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);

	private static final Map<IElementType, consulo.colorScheme.TextAttributesKey> ourMap;

	static
	{
		ourMap = new HashMap<consulo.language.ast.IElementType, consulo.colorScheme.TextAttributesKey>();
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, consulo.language.ast.TokenSet.create(VtlElementTypes.DOT, VtlElementTypes.JAVA_DOT), VELOCITY_DOT);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, consulo.language.ast.TokenSet.create(VtlElementTypes.LEFT_PAREN, VtlElementTypes.RIGHT_PAREN), VELOCITY_PARENTHS);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, consulo.language.ast.TokenSet.create(VtlElementTypes.LEFT_BRACKET, VtlElementTypes.RIGHT_BRACKET), VELOCITY_BRACKETS);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, consulo.language.ast.TokenSet.create(VtlElementTypes.LEFT_BRACE_IN_EXPR, VtlElementTypes.RIGHT_BRACE_IN_EXPR),
				VELOCITY_BRACES);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, consulo.language.ast.TokenSet.create(VtlElementTypes.PLUS, VtlElementTypes.MINUS, VtlElementTypes.ASTERISK,
				VtlElementTypes.DIVIDE, VtlElementTypes.PERCENT, VtlElementTypes.AND, VtlElementTypes.OR, VtlElementTypes.EXCLAIM, VtlElementTypes.QUESTION, VtlElementTypes.RANGE),
				VELOCITY_OPERATION_SIGN);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, consulo.language.ast.TokenSet.create(VtlElementTypes.STRING_TEXT, VtlElementTypes.DOUBLE_QUOTE, VtlElementTypes
				.SINGLE_QUOTE), VELOCITY_STRING);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.CHAR_ESCAPE, VELOCITY_ESCAPE);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, consulo.language.ast.TokenSet.create(VtlElementTypes.INTEGER, VtlElementTypes.DOUBLE), VELOCITY_NUMBER);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, consulo.language.ast.TokenSet.create(VtlElementTypes.BOOLEAN, VtlElementTypes.IN), VELOCITY_KEYWORD);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.COMMA, VELOCITY_COMMA);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.SEMICOLON, VELOCITY_SEMICOLON);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.SHARP_ELSE, VtlElementTypes.SHARP_END, VtlElementTypes.SHARP_STOP, VtlElementTypes
						.SHARP_BREAK, VtlElementTypes.SHARP_MACROCALL, VtlElementTypes.START_REFERENCE, VtlElementTypes.START_REF_FORMAL, VtlElementTypes.LEFT_BRACE, VtlElementTypes.RIGHT_BRACE),
				VELOCITY_DIRECTIVE);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.DIR_STARTERS, VELOCITY_DIRECTIVE);
		SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.IDENTIFIER, VELOCITY_REFERENCE);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.COMMENTS, VELOCITY_COMMENT);
		consulo.language.editor.highlight.SyntaxHighlighterBase.safeMap(ourMap, TokenType.BAD_CHARACTER, VELOCITY_BAD_CHARACTER);
	}

	@Nonnull
	public Lexer getHighlightingLexer()
	{
		return new VtlLexer();
	}

	@Nonnull
	public consulo.colorScheme.TextAttributesKey[] getTokenHighlights(final IElementType tokenType)
	{
		return consulo.language.editor.highlight.SyntaxHighlighterBase.pack(ourMap.get(tokenType), VELOCITY_SCRIPTING_BACKGROUND);
	}
}
