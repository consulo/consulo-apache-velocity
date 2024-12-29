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

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.file.VirtualFileViewProviderFactory;
import consulo.language.psi.PsiManager;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;

import jakarta.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlFileViewProviderFactory implements VirtualFileViewProviderFactory
{
	public VtlFileViewProvider createFileViewProvider(final VirtualFile file, final Language language, final PsiManager manager, final boolean physical)
	{
		return new VtlFileViewProvider(manager, file, physical);
	}

	@Nonnull
	@Override
	public FileType getFileType()
	{
		return VtlFileType.INSTANCE;
	}
}
