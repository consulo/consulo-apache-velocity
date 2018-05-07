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

import gnu.trove.THashMap;

import java.util.Map;

import javax.annotation.Nonnull;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.velocity.lexer.VtlLexer;
import com.intellij.velocity.psi.VtlElementTypes;

/**
 * @author Alexey Chmutov
 */
public class VtlSyntaxHighlighter extends SyntaxHighlighterBase
{
	public static final TextAttributesKey VELOCITY_DOT = TextAttributesKey.createTextAttributesKey("VELOCITY_DOT", DefaultLanguageHighlighterColors.DOT);
	public static final TextAttributesKey VELOCITY_PARENTHS = TextAttributesKey.createTextAttributesKey("VELOCITY_PARENTHS", DefaultLanguageHighlighterColors.PARENTHESES);
	public static final TextAttributesKey VELOCITY_BRACKETS = TextAttributesKey.createTextAttributesKey("VELOCITY_BRACKETS", DefaultLanguageHighlighterColors.BRACKETS);
	public static final TextAttributesKey VELOCITY_BRACES = TextAttributesKey.createTextAttributesKey("VELOCITY_BRACES", DefaultLanguageHighlighterColors.BRACES);
	public static final TextAttributesKey VELOCITY_OPERATION_SIGN = TextAttributesKey.createTextAttributesKey("VELOCITY_OPERATION_SIGN", DefaultLanguageHighlighterColors.OPERATION_SIGN);
	public static final TextAttributesKey VELOCITY_STRING = TextAttributesKey.createTextAttributesKey("VELOCITY_STRING", DefaultLanguageHighlighterColors.STRING);
	public static final TextAttributesKey VELOCITY_ESCAPE = TextAttributesKey.createTextAttributesKey("VELOCITY_ESCAPE", DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE);
	public static final TextAttributesKey VELOCITY_NUMBER = TextAttributesKey.createTextAttributesKey("VELOCITY_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
	public static final TextAttributesKey VELOCITY_KEYWORD = TextAttributesKey.createTextAttributesKey("VELOCITY_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
	public static final TextAttributesKey VELOCITY_COMMA = TextAttributesKey.createTextAttributesKey("VELOCITY_COMMA", DefaultLanguageHighlighterColors.COMMA);
	public static final TextAttributesKey VELOCITY_SEMICOLON = TextAttributesKey.createTextAttributesKey("VELOCITY_SEMICOLON", DefaultLanguageHighlighterColors.SEMICOLON);
	public static final TextAttributesKey VELOCITY_DIRECTIVE = TextAttributesKey.createTextAttributesKey("VELOCITY_DIRECTIVE", DefaultLanguageHighlighterColors.MARKUP_TAG);
	public static final TextAttributesKey VELOCITY_REFERENCE = TextAttributesKey.createTextAttributesKey("VELOCITY_REFERENCE", DefaultLanguageHighlighterColors.MARKUP_ATTRIBUTE);
	public static final TextAttributesKey VELOCITY_COMMENT = TextAttributesKey.createTextAttributesKey("VELOCITY_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
	public static final TextAttributesKey VELOCITY_BAD_CHARACTER = TextAttributesKey.createTextAttributesKey("VELOCITY_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
	public static final TextAttributesKey VELOCITY_SCRIPTING_BACKGROUND = TextAttributesKey.createTextAttributesKey("VELOCITY_SCRIPTING_BACKGROUND", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);

	private static final Map<IElementType, TextAttributesKey> ourMap;

	static
	{
		ourMap = new THashMap<IElementType, TextAttributesKey>();
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.DOT, VtlElementTypes.JAVA_DOT), VELOCITY_DOT);
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.LEFT_PAREN, VtlElementTypes.RIGHT_PAREN), VELOCITY_PARENTHS);
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.LEFT_BRACKET, VtlElementTypes.RIGHT_BRACKET), VELOCITY_BRACKETS);
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.LEFT_BRACE_IN_EXPR, VtlElementTypes.RIGHT_BRACE_IN_EXPR), VELOCITY_BRACES);
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.PLUS, VtlElementTypes.MINUS, VtlElementTypes.ASTERISK, VtlElementTypes.DIVIDE, VtlElementTypes.PERCENT, VtlElementTypes.AND, VtlElementTypes.OR, VtlElementTypes.EXCLAIM, VtlElementTypes.QUESTION, VtlElementTypes.RANGE), VELOCITY_OPERATION_SIGN);
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.STRING_TEXT, VtlElementTypes.DOUBLE_QUOTE, VtlElementTypes.SINGLE_QUOTE), VELOCITY_STRING);
		SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.CHAR_ESCAPE, VELOCITY_ESCAPE);
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.INTEGER, VtlElementTypes.DOUBLE), VELOCITY_NUMBER);
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.BOOLEAN, VtlElementTypes.IN), VELOCITY_KEYWORD);
		SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.COMMA, VELOCITY_COMMA);
		SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.SEMICOLON, VELOCITY_SEMICOLON);
		SyntaxHighlighterBase.safeMap(ourMap, TokenSet.create(VtlElementTypes.SHARP_ELSE, VtlElementTypes.SHARP_END, VtlElementTypes.SHARP_STOP, VtlElementTypes.SHARP_BREAK, VtlElementTypes.SHARP_MACROCALL, VtlElementTypes.START_REFERENCE, VtlElementTypes.START_REF_FORMAL, VtlElementTypes.LEFT_BRACE, VtlElementTypes.RIGHT_BRACE), VELOCITY_DIRECTIVE);
		SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.DIR_STARTERS, VELOCITY_DIRECTIVE);
		SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.IDENTIFIER, VELOCITY_REFERENCE);
		SyntaxHighlighterBase.safeMap(ourMap, VtlElementTypes.COMMENTS, VELOCITY_COMMENT);
		SyntaxHighlighterBase.safeMap(ourMap, TokenType.BAD_CHARACTER, VELOCITY_BAD_CHARACTER);
	}

	@Nonnull
	public Lexer getHighlightingLexer()
	{
		return new VtlLexer();
	}

	@Nonnull
	public TextAttributesKey[] getTokenHighlights(final IElementType tokenType)
	{
		return SyntaxHighlighterBase.pack(ourMap.get(tokenType), VELOCITY_SCRIPTING_BACKGROUND);
	}
}
