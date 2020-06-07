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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.HashMap;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.BooleanDataDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.files.VtlFileType;

/**
 * @author Alexey Chmutov
 */
public class VtlFileIndex extends ScalarIndexExtension<Boolean> {
    private static final ID<Boolean, Void> NAME = ID.create("VtlFileIndex");
    @NonNls
    public static final String IMPLICIT_INCLUDE_MARKER = "#* @implicitly included *#\n";

    public ID<Boolean, Void> getName() {
        return NAME;
    }

    @Nonnull
    public static Collection<VtlFile> getImplicitlyIncludedFiles(final PsiFile targetFile) {
        final Module module = ModuleUtil.findModuleForPsiElement(targetFile);
        if (module == null || DumbService.getInstance(module.getProject()).isDumb()) {
            return Collections.emptyList();
        }
        final Collection<VirtualFile> files =
                FileBasedIndex.getInstance().getContainingFiles(NAME, Boolean.TRUE, GlobalSearchScope.moduleScope(module));
        List<VtlFile> result = new ArrayList<VtlFile>(files.size());
        for (final VirtualFile virtualFile : files) {
            final PsiFile psiFile = targetFile.getManager().findFile(virtualFile);
            if (psiFile instanceof VtlFile) {
                if(psiFile.equals(targetFile)) {
                    return Collections.emptyList();
                }
                result.add((VtlFile) psiFile);
            }
        }
        return result;
    }


    public DataIndexer<Boolean, Void, FileContent> getIndexer() {
        return new DataIndexer<Boolean, Void, FileContent>() {
            @Nonnull
            public Map<Boolean, Void> map(final FileContent inputData) {
                final CharSequence text = inputData.getContentAsText();
                final int markerLength = IMPLICIT_INCLUDE_MARKER.length();
                if (markerLength > text.length()
                        || !IMPLICIT_INCLUDE_MARKER.equals(text.subSequence(0, markerLength).toString())) {
                    return Collections.emptyMap();
                }
                final HashMap<Boolean, Void> map = new HashMap<Boolean, Void>();
                map.put(Boolean.TRUE, null);
                return map;
            }
        };
    }

    public KeyDescriptor<Boolean> getKeyDescriptor() {
        return BooleanDataDescriptor.INSTANCE;
    }

    public FileBasedIndex.InputFilter getInputFilter() {
        return new FileBasedIndex.InputFilter() {
            public boolean acceptInput(Project project, final VirtualFile file) {
                return VtlFileType.INSTANCE == file.getFileType();
            }
        };
    }

    public boolean dependsOnFileContent() {
        return true;
    }

    public int getVersion() {
        return 0;
    }

    private static class ModuleSourceVirtualFileFilter implements VirtualFileFilter {
        private final ModuleFileIndex myIndex;

        public ModuleSourceVirtualFileFilter(final Module module) {
            myIndex = ModuleRootManager.getInstance(module).getFileIndex();
        }

        public boolean accept(final VirtualFile file) {
            return myIndex.isInSourceContent(file);
        }
    }
}