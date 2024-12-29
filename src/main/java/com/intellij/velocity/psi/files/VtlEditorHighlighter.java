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

import static com.intellij.velocity.psi.VtlElementTypes.TEMPLATE_TEXT;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import consulo.colorScheme.EditorColorsScheme;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.editor.highlight.LayeredLexerEditorHighlighter;
import consulo.language.lexer.Lexer;
import consulo.language.editor.highlight.LayerDescriptor;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.language.plain.PlainTextFileType;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.language.editor.highlight.SyntaxHighlighterFactory;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.ast.IElementType;

/**
 * @author Alexey Chmutov
 */
public class VtlEditorHighlighter extends LayeredLexerEditorHighlighter
{
	public VtlEditorHighlighter(@Nullable final Project project, @Nullable final VirtualFile virtualFile, @Nonnull final EditorColorsScheme colors)
	{
		super(new VtlSyntaxHighlighter(), colors);
		final SyntaxHighlighter highlighter = getTemplateDataLanguageHighlighter(project, virtualFile);
		registerLayer(TEMPLATE_TEXT, new LayerDescriptor(new SyntaxHighlighter()
		{
			@Override
			@Nonnull
			public Lexer getHighlightingLexer()
			{
				return highlighter.getHighlightingLexer();
			}

			@Override
			@Nonnull
			public TextAttributesKey[] getTokenHighlights(final IElementType tokenType)
			{
				return highlighter.getTokenHighlights(tokenType);
			}
		}, ""));
	}

	@Nonnull
	private static SyntaxHighlighter getTemplateDataLanguageHighlighter(final Project project, final VirtualFile virtualFile)
	{
		final FileType type = project == null || virtualFile == null ? null : VtlFileViewProvider.getTemplateDataLanguage(virtualFile,
				project).getAssociatedFileType();
		final FileType fileType = type == null ? PlainTextFileType.INSTANCE : type;
		final consulo.language.editor.highlight.SyntaxHighlighter highlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(fileType, project, virtualFile);
		assert highlighter != null;
		return highlighter;
	}

}

