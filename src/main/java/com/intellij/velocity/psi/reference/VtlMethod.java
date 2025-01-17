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

import com.intellij.java.language.psi.PsiMethod;
import com.intellij.java.language.psi.PsiParameter;
import com.intellij.java.language.psi.PsiType;
import com.intellij.velocity.psi.VtlCallable;
import com.intellij.velocity.psi.VtlVariable;
import consulo.language.impl.psi.FakePsiElement;
import consulo.language.psi.PsiElement;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Alexey Chmutov
 *         Date: 11.06.2008
 */
class VtlMethod implements VtlCallable {
    private final PsiMethod myMethod;
    private final VtlVariable[] myParameters;

    public VtlMethod(PsiMethod method) {
        myMethod = method;
        PsiParameter[] parameters = method.getParameterList().getParameters();
        myParameters = new VtlVariable[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            myParameters[i] = new Parameter(parameters[i]);
        }
    }

    public VtlVariable[] getParameters() {
        return myParameters;
    }

    public boolean isDeprecated() {
        return myMethod.isDeprecated();
    }

    private static class Parameter extends FakePsiElement implements VtlVariable {
        private final String myName;
        private final PsiParameter myParent;

        public Parameter(@Nonnull PsiParameter parent) {
            myName = StringUtil.notNullize(parent.getName());
            myParent = parent;
        }

        @Override
        @Nonnull
        public String getName() {
            return myName;
        }

        public PsiElement getParent() {
            return myParent;
        }

        public PsiType getPsiType() {
            return myParent.getType();
        }
    }

}
