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

package com.intellij.velocity.psi.reference;

import com.intellij.velocity.psi.files.VelocityPropertiesProvider;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.language.psi.*;
import consulo.util.collection.ContainerUtil;
import consulo.virtualFileSystem.VirtualFile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Alexey Chmutov
 */
public class VtlFileReferenceSet extends SoftFileReferenceSet {

    public VtlFileReferenceSet(@Nonnull String text, PsiElement element, int startInElement) {
        super(text, element, startInElement);
    }

    @Nonnull
    @Override
    public Collection<PsiFileSystemItem> getDefaultContexts() {
        VtlFile file = getContainingVtlFile();
        if (file == null) {
            return super.getDefaultContexts();
        }
        final VelocityPropertiesProvider velocityProperties = file.getVelocityProperties();
        if (velocityProperties == null) {
            return super.getDefaultContexts();
        }
        return getVtlDefaultContexts(velocityProperties);
    }

    @Nonnull
    private Collection<consulo.language.psi.PsiFileSystemItem> getVtlDefaultContexts(@Nonnull VelocityPropertiesProvider velocityProperties) {
        PsiManager manager = getElement().getManager();
        Collection<consulo.language.psi.PsiFileSystemItem> vtlDefaultContexts = new ArrayList<consulo.language.psi.PsiFileSystemItem>();
        for (consulo.language.psi.PsiFileSystemItem defaultContext : super.getDefaultContexts()) {
            for (VirtualFile resourceLoaderPath : velocityProperties.getResourceLoaderPathListBasedOn(defaultContext.getVirtualFile())) {
                PsiDirectory resourceLoaderContext = manager.findDirectory(resourceLoaderPath);
                ContainerUtil.addIfNotNull(vtlDefaultContexts, resourceLoaderContext);
            }
        }
        return vtlDefaultContexts;
    }

    @Nullable
    private VtlFile getContainingVtlFile() {
        PsiFile psiFile = getElement().getContainingFile();
        return psiFile instanceof VtlFile ? (VtlFile) psiFile : null;
    }
}
