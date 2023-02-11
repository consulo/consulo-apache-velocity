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

import com.intellij.java.indexing.impl.search.CustomPropertyScopeProvider;
import com.intellij.velocity.psi.files.VtlFileType;
import consulo.annotation.component.ExtensionImpl;
import consulo.content.scope.SearchScope;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;

/**
 * @author peter
 */
@ExtensionImpl
public class VtlPropertyScopeProvider implements CustomPropertyScopeProvider
{
	public SearchScope getScope(final Project project)
	{
		return consulo.language.psi.scope.GlobalSearchScope.getScopeRestrictedByFileTypes(GlobalSearchScope.allScope(project), VtlFileType.INSTANCE);
	}
}
