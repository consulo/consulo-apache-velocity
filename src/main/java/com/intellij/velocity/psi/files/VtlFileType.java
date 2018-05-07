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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.EditorHighlighterProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeEditorHighlighterProviders;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.TemplateLanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.velocity.VtlIcons;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.VtlLanguage;

/**
 * @author Alexey Chmutov
 */
public class VtlFileType extends LanguageFileType implements TemplateLanguageFileType
{
	public static final VtlFileType INSTANCE = new VtlFileType();

	private VtlFileType()
	{
		super(VtlLanguage.INSTANCE);

		FileTypeEditorHighlighterProviders.INSTANCE.addExplicitExtension(this, new EditorHighlighterProvider()
		{
			@Override
			public EditorHighlighter getEditorHighlighter(@Nullable Project project, @Nonnull FileType fileType, @Nullable VirtualFile virtualFile, @Nonnull EditorColorsScheme colors)
			{
				return new VtlEditorHighlighter(project, virtualFile, colors);
			}
		});
	}

	@Override
	@Nonnull
	@NonNls
	public String getId()
	{
		return "VTL";
	}

	@Override
	@Nonnull
	public String getDescription()
	{
		return VelocityBundle.message("file.type.description");
	}

	@Override
	@Nonnull
	@NonNls
	public String getDefaultExtension()
	{
		return "vm";
	}

	@Override
	@Nullable
	public Icon getIcon()
	{
		return VtlIcons.VTL_ICON;
	}

	@Nonnull
	@NonNls
	public String[] getExtensions()
	{
		return new String[]{
				getDefaultExtension(),
				"ft",
				"vsl"
		};
	}
}
