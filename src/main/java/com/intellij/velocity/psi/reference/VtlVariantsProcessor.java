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

import com.intellij.java.impl.psi.impl.beanProperties.BeanProperty;
import com.intellij.java.impl.psi.resolve.JavaMethodCandidateInfo;
import com.intellij.java.impl.psi.resolve.JavaMethodResolveHelper;
import com.intellij.java.language.psi.*;
import com.intellij.velocity.psi.VtlMacro;
import com.intellij.velocity.psi.VtlMethodCallExpression;
import com.intellij.velocity.psi.directives.VtlAssignment;
import com.intellij.velocity.psi.directives.VtlMacroCall;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.resolve.BaseScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.util.lang.StringUtil;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.intellij.java.language.psi.PsiModifier.*;
import static com.intellij.velocity.psi.reference.VelocityNamingUtil.isWaitOrNotifyOfObject;
import static consulo.util.collection.ContainerUtil.addIfNotNull;

/**
 * @author peter
 * @author Alexey Chmutov
 */
abstract class VtlVariantsProcessor<T> extends BaseScopeProcessor
{
    private final Set<T> myResult = new LinkedHashSet<T>();
    private final boolean myForCompletion;
    private final boolean myMethodCall;
    private final boolean myMacroCall;
    private final consulo.language.psi.PsiElement myParent;
    private final String myReferenceName;
    private final VelocityStylePropertyResolveHelper myPropertyMethods;
    private final JavaMethodResolveHelper myMethods;
	private final consulo.language.psi.PsiFile myPsiFile;

	protected VtlVariantsProcessor(final consulo.language.psi.PsiElement parent, consulo.language.psi.PsiFile psiFile, @Nullable String referenceName, boolean propertiesOnly) {
		myPsiFile = psiFile;
		myForCompletion = referenceName == null;
        myParent = parent;
        myReferenceName = referenceName;
        myMethodCall = myParent instanceof VtlMethodCallExpression;
        myMacroCall = myParent instanceof VtlMacroCall;
        if (myMacroCall) {
            myPropertyMethods = null;
            myMethods = null;
            return;
        }
        if (myMethodCall && !myForCompletion) {
            assert !propertiesOnly;
            final PsiType[] parameterTypes = ((VtlMethodCallExpression) myParent).getArgumentTypes();
            myMethods = new JavaMethodResolveHelper(parent,  myPsiFile, parameterTypes);
            myPropertyMethods = null;
        } else {
            myPropertyMethods = new VelocityStylePropertyResolveHelper(myReferenceName, myParent instanceof VtlAssignment);
            myMethods = myForCompletion && !propertiesOnly ? new JavaMethodResolveHelper(parent, myPsiFile, null) : null;
        }
    }

    public boolean execute(final consulo.language.psi.PsiElement element, final ResolveState state) {
        if (!(element instanceof consulo.language.psi.PsiNamedElement)) {
            return true;
        }

        final PsiNamedElement namedElement = (consulo.language.psi.PsiNamedElement) element;
        if (StringUtil.isEmpty(namedElement.getName())) {
            return true;
        }

        if (namedElement instanceof PsiField || namedElement instanceof PsiClass) {
            return true;
        }

        if (myMacroCall != namedElement instanceof VtlMacro) {
            return true;
        }

        if (namedElement instanceof PsiModifierListOwner) {
            final PsiModifierListOwner owner = (PsiModifierListOwner) namedElement;
            if (owner.hasModifierProperty(PRIVATE) || owner.hasModifierProperty(PACKAGE_LOCAL)
                    || owner.hasModifierProperty(PROTECTED)) {
                return true;
            }
        }

        final boolean isMethodCall = namedElement instanceof PsiMethod;
        if (isMethodCall) {
            final PsiMethod method = (PsiMethod) namedElement;
            if (method.isConstructor()) {
                return true;
            }
            if (myForCompletion && isWaitOrNotifyOfObject(method)) {
                return true;
            }
            if (!myMethodCall) {
                myPropertyMethods.checkAndAddMethod(method, state.get(PsiSubstitutor.KEY));
            }
            if(myMethods == null) {
              return true;
            }
        }

        if (!myForCompletion && isMethodCall != myMethodCall) {
            return true;
        }

        if (!myForCompletion && !myReferenceName.equals(namedElement.getName())) {
            return true;
        }

        if (isMethodCall) {
            myMethods.addMethod((PsiMethod) namedElement, state.get(PsiSubstitutor.KEY), false);
            return true;
        }

        boolean resolvedWithError = false;
        if (!myForCompletion && myMacroCall) {
            int parametersLength = ((VtlMacro) namedElement).getParameters().length;
            int argumentsLength = ((VtlMacroCall) myParent).getArguments().length;
            resolvedWithError = parametersLength != argumentsLength;
        }
        addIfNotNull(myResult, execute(namedElement, resolvedWithError));
        return myForCompletion || myResult.size() != 1;
    }

    @Nullable
    protected abstract T execute(final consulo.language.psi.PsiNamedElement element, final boolean error);

    public T[] getVariants(T[] array, boolean isFirstCharInLowerCase) {
        if (myPropertyMethods != null) {
            for (final PsiMethod method : myPropertyMethods.getMethods()) {
                String propName = myReferenceName != null ? myReferenceName : VelocityNamingUtil.getPropertyNameFromAccessor(method, isFirstCharInLowerCase);
                final BeanProperty property = VelocityStyleBeanProperty.createVelocityStyleBeanProperty(method, propName);
                if (property != null) {
                    addIfNotNull(myResult, execute(property.getPsiElement(), false));
                }
            }
        }
        if (myMethods != null) {
            for (final JavaMethodCandidateInfo method : myMethods.getMethods()) {
                addIfNotNull(myResult, execute(method.getMethod(), myMethods.getResolveError() == JavaMethodResolveHelper.ErrorType.RESOLVE));
            }
        }
        return myResult.toArray(array);
    }
}
