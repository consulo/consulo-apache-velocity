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

import com.intellij.ide.structureView.StructureView;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.impl.StructureViewComposite;
import com.intellij.ide.structureView.impl.TemplateLanguageStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.psi.PsiFile;
import com.intellij.velocity.VelocityBundle;

/**
 * @author Alexey Chmutov
 */
public class VtlStructureViewBuilderProvider implements PsiStructureViewFactory
{
	@Override
	@Nullable
	public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile)
	{
		return new TemplateLanguageStructureViewBuilder(psiFile)
		{
			@Override
			protected StructureViewComposite.StructureViewDescriptor createMainView(final FileEditor fileEditor, final PsiFile mainFile)
			{
				StructureView mainView = new TreeBasedStructureViewBuilder()
				{
					@Override
					@Nonnull
					public StructureViewModel createStructureViewModel(Editor editor)
					{
						return new VtlStructureViewModel((VtlFile) mainFile);
					}
				}.createStructureView(fileEditor, mainFile.getProject());
				return new StructureViewComposite.StructureViewDescriptor(VelocityBundle.message("tab.structureview.vtl.view"), mainView, mainFile.getFileType().getIcon());
			}
		};
	}
}
