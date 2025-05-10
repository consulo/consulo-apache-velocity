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
package com.intellij.velocity.psi.directives;

import com.intellij.java.language.psi.CommonClassNames;
import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiType;
import consulo.language.ast.ASTNode;

/**
 * @author Alexey Chmutov
 */
public class VtlDefine extends VtlAssignment
{
	public VtlDefine(final ASTNode node)
	{
		super(node, "define", true);
	}

	@Override
	public PsiType getAssignedVariableElementType()
	{
		return JavaPsiFacade.getInstance(getProject()).getElementFactory().createTypeByFQClassName(CommonClassNames.JAVA_LANG_STRING,
				getResolveScope());
	}
}