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
package com.intellij.velocity.editorActions;

import com.intellij.velocity.psi.VtlLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.BracePair;
import consulo.language.Language;
import consulo.language.PairedBraceMatcher;
import consulo.language.ast.IElementType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.intellij.velocity.psi.VtlElementTypes.*;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VelocityPairedBraceMatcher implements PairedBraceMatcher
{
	private static final BracePair[] PAIRS = new BracePair[]{
			new BracePair(SHARP_FOREACH, SHARP_END, true),
			new BracePair(SHARP_IF, SHARP_END, true),
			new BracePair(SHARP_MACRODECL, SHARP_END, true),
			new BracePair(SHARP_DEFINE, SHARP_END, true),
			new BracePair(SHARP_LITERAL, SHARP_END, true),
			new BracePair(START_REF_FORMAL, RIGHT_BRACE, false),
			new BracePair(LEFT_BRACE, RIGHT_BRACE, false),
			new BracePair(LEFT_BRACE_IN_EXPR, RIGHT_BRACE_IN_EXPR, false),
			new BracePair(LEFT_PAREN, RIGHT_PAREN, false),
			new BracePair(LEFT_BRACKET, RIGHT_BRACKET, false),
	};

	public BracePair[] getPairs()
	{
		return PAIRS;
	}

	public boolean isPairedBracesAllowedBeforeType(@Nonnull final consulo.language.ast.IElementType lbraceType, @Nullable final IElementType type)
	{
		return lbraceType == LEFT_PAREN && type == null;
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return VtlLanguage.INSTANCE;
	}
}
