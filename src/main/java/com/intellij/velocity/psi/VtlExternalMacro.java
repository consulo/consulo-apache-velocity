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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

/**
 * @author Alexey Chmutov
 */
public class VtlExternalMacro extends FakePsiElement implements VtlMacro {
    @NotNull
    private final PsiComment myComment;
    private final String myName;
    private VtlVariable[] myParameters;

    private VtlExternalMacro(@NotNull final PsiComment comment, @NotNull final String name) {
        myComment = comment;
        myName = name;
    }

    public PsiFile getContainingFile() {
        return myComment.getContainingFile();
    }

    @NotNull
    public Language getLanguage() {
        return myComment.getLanguage();
    }

    @NotNull
    public Project getProject() {
        return myComment.getProject();
    }

    @NotNull
    public String getName() {
        return myName;
    }

    @NotNull
    public PsiElement getNavigationElement() {
        return myComment;
    }

    @NotNull
    public PsiElement getParent() {
        return myComment;
    }

    @NotNull
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
    public final Icon getIcon(boolean open) {
        return VtlIcons.SHARP_ICON;
    }

    public String toString() {
        return "ExternalMacro " + myName;
    }

    public static VtlExternalMacro getOrCreate(final Map<String, VtlExternalMacro> mapToAddTo, @NotNull final PsiComment comment, final String name) {
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

        public Parameter(@NotNull String name) {
            myName = name;
        }

        @Override
        @NotNull
        public String getName() {
            return myName;
        }

        @Override
        public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
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