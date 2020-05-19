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

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Factory;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.FakePsiElement;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.velocity.VtlIcons;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.ui.image.Image;

/**
 * @author Alexey Chmutov
 */
public class VtlExternalMacro extends FakePsiElement implements VtlMacro {
    @Nonnull
    private final PsiComment myComment;
    private final String myName;
    private VtlVariable[] myParameters;

    private VtlExternalMacro(@Nonnull final PsiComment comment, @Nonnull final String name) {
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
    public PsiElement getNavigationElement() {
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

    public static VtlExternalMacro getOrCreate(final Map<String, VtlExternalMacro> mapToAddTo, @Nonnull final PsiComment comment, final String name) {
        return ContainerUtil.getOrCreate(mapToAddTo, name, new Factory<VtlExternalMacro>() {
            public VtlExternalMacro create() {
                return new VtlExternalMacro(comment, name);
            }
        });
    }

    public boolean isVisibleIn(VtlFile placeFile) {
        // todo implement
        return true;
    }
    
    private class Parameter extends FakePsiElement implements VtlVariable {
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
        public PsiElement setName(@Nonnull String name) throws IncorrectOperationException {
            final PsiElement res = super.setName(name);
            myName = name;
            return res;
        }

        public PsiElement getParent() {
            return myComment;
        }

        public PsiType getPsiType() {
            return null;
        }
    }

}