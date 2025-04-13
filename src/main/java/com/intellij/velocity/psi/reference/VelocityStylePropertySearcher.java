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

import com.intellij.java.indexing.search.searches.MethodReferencesSearch;
import com.intellij.java.indexing.search.searches.MethodReferencesSearchExecutor;
import com.intellij.java.language.psi.PsiMethod;
import com.intellij.velocity.psi.files.VtlFileType;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.ApplicationManager;
import consulo.content.scope.SearchScope;
import consulo.language.psi.PsiReference;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.search.PsiSearchHelper;
import consulo.language.psi.search.UsageSearchContext;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.util.lang.ref.SimpleReference;
import jakarta.annotation.Nonnull;

import java.util.function.Predicate;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VelocityStylePropertySearcher implements MethodReferencesSearchExecutor {
    @Override
    public boolean execute(@Nonnull final MethodReferencesSearch.SearchParameters parameters, @Nonnull final Predicate<? super PsiReference> consumer) {
        final PsiMethod method = parameters.getMethod();
        final SimpleReference<String> name = SimpleReference.create(null);
        final SimpleReference<Project> project = SimpleReference.create(null);
        ApplicationManager.getApplication().runReadAction(() -> {
            if (!method.isValid()) {
                return;
            }
            project.set(method.getProject());
            name.set(VelocityNamingUtil.getPropertyNameFromAccessor(method));
        });
        String nameRefValue = name.get();
        if (StringUtil.isEmpty(nameRefValue)) {
            return true;
        }
        SearchScope searchScope = parameters.getScope();
        if (searchScope instanceof consulo.language.psi.scope.GlobalSearchScope) {
            searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes((GlobalSearchScope) searchScope, VtlFileType.INSTANCE);
        }

        final consulo.language.psi.search.TextOccurenceProcessor processor = (element, offsetInElement) -> {
            final PsiReference[] refs = element.getReferences();
            for (PsiReference ref : refs) {
                if (ref.getRangeInElement().contains(offsetInElement) && ref.isReferenceTo(method)) {
                    return consumer.test(ref);
                }
            }
            return true;
        };
        final PsiSearchHelper helper = PsiSearchHelper.getInstance(project.get());
        return helper.processElementsWithWord(processor, searchScope, nameRefValue, UsageSearchContext.IN_FOREIGN_LANGUAGES, false);
    }
}
