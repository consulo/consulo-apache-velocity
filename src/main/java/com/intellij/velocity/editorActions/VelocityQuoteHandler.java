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

import com.intellij.velocity.psi.files.VtlFileType;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.codeEditor.HighlighterIterator;
import consulo.document.Document;
import consulo.language.ast.IElementType;
import consulo.language.editor.action.FileQuoteHandler;
import consulo.language.editor.action.SimpleTokenSetQuoteHandler;
import consulo.virtualFileSystem.fileType.FileType;

import javax.annotation.Nonnull;

import static com.intellij.velocity.psi.VtlElementTypes.*;


/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VelocityQuoteHandler extends SimpleTokenSetQuoteHandler implements FileQuoteHandler
{
	public VelocityQuoteHandler()
	{
		super(SINGLE_QUOTE, DOUBLE_QUOTE);
	}

	public boolean isClosingQuote(final consulo.codeEditor.HighlighterIterator iterator, final int offset)
	{
		if(!myLiteralTokenSet.contains((IElementType) iterator.getTokenType()) || iterator.getEnd() - iterator.getStart() != 1)
		{
			return false;
		}
		return !isOpeningQuoteInternal(iterator);
	}

	public boolean isOpeningQuote(HighlighterIterator iterator, int offset)
	{
		if(!myLiteralTokenSet.contains((IElementType) iterator.getTokenType()) || offset != iterator.getStart())
		{
			return false;
		}
		return isOpeningQuoteInternal(iterator);
	}

	private boolean isOpeningQuoteInternal(final HighlighterIterator iterator)
	{
		iterator.retreat();
		try
		{
			if(iterator.atEnd())
			{
				return true;
			}
			final IElementType type = (IElementType) iterator.getTokenType();
			return !(myLiteralTokenSet.contains(type) || STRING_TEXT.equals(type) || CHAR_ESCAPE.equals(type));
		}
		finally
		{
			iterator.advance();
		}
	}

	public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset)
	{
		int start = iterator.getStart();
		try
		{
			Document doc = editor.getDocument();
			CharSequence chars = doc.getCharsSequence();
			int lineEnd = doc.getLineEndOffset(doc.getLineNumber(offset));

			while(!iterator.atEnd() && iterator.getStart() < lineEnd)
			{
				IElementType tokenType = (IElementType) iterator.getTokenType();

				if(myLiteralTokenSet.contains(tokenType) &&
						(iterator.getStart() >= iterator.getEnd() - 1
								|| chars.charAt(iterator.getEnd() - 1) != '\"' && chars.charAt(iterator.getEnd() - 1) != '\''))
				{
					return true;
				}

				iterator.advance();
			}
		}
		finally
		{
			while(iterator.atEnd() || iterator.getStart() != start)
			{
				iterator.retreat();
			}
		}

		return false;
	}

	@Nonnull
	@Override
	public FileType getFileType()
	{
		return VtlFileType.INSTANCE;
	}
}
