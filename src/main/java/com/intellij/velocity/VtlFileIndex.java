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

import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.files.VtlFileType;
import consulo.annotation.component.ExtensionImpl;
import consulo.index.io.BooleanDataDescriptor;
import consulo.index.io.ID;
import consulo.index.io.KeyDescriptor;
import consulo.language.psi.PsiFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.ScalarIndexExtension;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleFileIndex;
import consulo.module.content.ModuleRootManager;
import consulo.project.DumbService;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileFilter;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;
import java.util.*;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlFileIndex extends ScalarIndexExtension<Boolean>
{
	private static final consulo.index.io.ID<Boolean, Void> NAME = consulo.index.io.ID.create("VtlFileIndex");
	@NonNls
	public static final String IMPLICIT_INCLUDE_MARKER = "#* @implicitly included *#\n";

	@Nonnull
	public ID<Boolean, Void> getName()
	{
		return NAME;
	}

	@Nonnull
	public static Collection<VtlFile> getImplicitlyIncludedFiles(final PsiFile targetFile)
	{
		final Module module = ModuleUtilCore.findModuleForPsiElement(targetFile);
		if(module == null || DumbService.getInstance(module.getProject()).isDumb())
		{
			return Collections.emptyList();
		}
		final Collection<consulo.virtualFileSystem.VirtualFile> files =
				consulo.language.psi.stub.FileBasedIndex.getInstance().getContainingFiles(NAME, Boolean.TRUE, GlobalSearchScope.moduleScope(module));
		List<VtlFile> result = new ArrayList<VtlFile>(files.size());
		for(final consulo.virtualFileSystem.VirtualFile virtualFile : files)
		{
			final PsiFile psiFile = targetFile.getManager().findFile(virtualFile);
			if(psiFile instanceof VtlFile)
			{
				if(psiFile.equals(targetFile))
				{
					return Collections.emptyList();
				}
				result.add((VtlFile) psiFile);
			}
		}
		return result;
	}


	@Nonnull
	public consulo.index.io.DataIndexer<Boolean, Void, consulo.language.psi.stub.FileContent> getIndexer()
	{
		return new consulo.index.io.DataIndexer<Boolean, Void, consulo.language.psi.stub.FileContent>()
		{
			@Nonnull
			public Map<Boolean, Void> map(final consulo.language.psi.stub.FileContent inputData)
			{
				final CharSequence text = inputData.getContentAsText();
				final int markerLength = IMPLICIT_INCLUDE_MARKER.length();
				if(markerLength > text.length()
						|| !IMPLICIT_INCLUDE_MARKER.equals(text.subSequence(0, markerLength).toString()))
				{
					return Collections.emptyMap();
				}
				final HashMap<Boolean, Void> map = new HashMap<Boolean, Void>(1);
				map.put(Boolean.TRUE, null);
				return map;
			}
		};
	}

	@Nonnull
	public KeyDescriptor<Boolean> getKeyDescriptor()
	{
		return BooleanDataDescriptor.INSTANCE;
	}

	@Nonnull
	public consulo.language.psi.stub.FileBasedIndex.InputFilter getInputFilter()
	{
		return new consulo.language.psi.stub.FileBasedIndex.InputFilter()
		{
			public boolean acceptInput(Project project, @Nonnull final consulo.virtualFileSystem.VirtualFile file)
			{
				return VtlFileType.INSTANCE == file.getFileType();
			}
		};
	}

	public boolean dependsOnFileContent()
	{
		return true;
	}

	public int getVersion()
	{
		return 0;
	}

	private static class ModuleSourceVirtualFileFilter implements VirtualFileFilter
	{
		private final ModuleFileIndex myIndex;

		public ModuleSourceVirtualFileFilter(final Module module)
		{
			myIndex = ModuleRootManager.getInstance(module).getFileIndex();
		}

		public boolean accept(final VirtualFile file)
		{
			return myIndex.isInSourceContent(file);
		}
	}
}
