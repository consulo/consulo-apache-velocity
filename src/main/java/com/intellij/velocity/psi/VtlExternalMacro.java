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
package com.intellij.velocity.psi;

import com.intellij.java.language.psi.PsiType;
import com.intellij.velocity.VtlIcons;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.language.Language;
import consulo.language.impl.ast.Factory;
import consulo.language.impl.psi.FakePsiElement;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import consulo.ui.image.Image;
import consulo.util.collection.ContainerUtil;
import consulo.util.collection.Maps;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Map;

/**
 * @author Alexey Chmutov
 */
public class VtlExternalMacro extends FakePsiElement implements VtlMacro {
    @Nonnull
    private final PsiComment myComment;
    private final String myName;
    private VtlVariable[] myParameters;

    private VtlExternalMacro(@Nonnull final consulo.language.psi.PsiComment comment, @Nonnull final String name) {
        myComment = comment;
        myName = name;
    }

    public PsiFile getContainingFile() {
        return myComment.getContainingFile();
    }

    @Nonnull
    public Language getLanguage() {
        return myComment.getLanguage();
    }

    @Nonnull
    public Project getProject() {
        return myComment.getProject();
    }

    @Nonnull
    public String getName() {
        return myName;
    }

    @Nonnull
    public consulo.language.psi.PsiElement getNavigationElement() {
        return myComment;
    }

    @Nonnull
    public PsiElement getParent() {
        return myComment;
    }

    @Nonnull
    public VtlVariable[] getParameters() {
        return myParameters;
    }

    public void setParameters(String[] parameterNames) {
        myParameters = new Parameter[parameterNames.length];
        for (int i = 0; i < myParameters.length; i++) {
            myParameters[i] = new Parameter(parameterNames[i]);
        }
    }

    public boolean isDeprecated() {
        return false;
    }

    @Nullable
    @Override
    public final Image getIcon() {
        return VtlIcons.SHARP_ICON;
    }

    public String toString() {
        return "ExternalMacro " + myName;
    }

	public static VtlExternalMacro getOrCreate(final Map<String, VtlExternalMacro> mapToAddTo, @Nonnull final consulo.language.psi.PsiComment comment, final String name)
	{
		return mapToAddTo.computeIfAbsent(name, s -> new VtlExternalMacro(comment, name));
	}

    public boolean isVisibleIn(VtlFile placeFile) {
        // todo implement
        return true;
    }
    
    private class Parameter extends consulo.language.impl.psi.FakePsiElement implements VtlVariable {
        private String myName;

        public Parameter(@Nonnull String name) {
            myName = name;
        }

        @Override
        @Nonnull
        public String getName() {
            return myName;
        }

        @Override
        public consulo.language.psi.PsiElement setName(@Nonnull String name) throws IncorrectOperationException {
            final consulo.language.psi.PsiElement res = super.setName(name);
            myName = name;
            return res;
        }

        public consulo.language.psi.PsiElement getParent() {
            return myComment;
        }

        public PsiType getPsiType() {
            return null;
        }
    }

}