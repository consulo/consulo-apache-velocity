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
package com.intellij.velocity.inspections;

import javax.annotation.Nonnull;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.VtlReferenceContributor;
import com.intellij.velocity.psi.VtlImplicitVariable;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.annotation.access.RequiredReadAction;
import consulo.velocity.api.facade.VelocityFacade;

/**
 * @author Alexey Chmutov
 */
public class VtlVariableTypeAnnotator implements Annotator
{
	@RequiredReadAction
	public void annotate(@Nonnull final PsiElement element, @Nonnull final AnnotationHolder holder)
	{
		if(!VtlReferenceContributor.VTLVARIABLE_COMMENT.accepts(element))
		{
			return;
		}

		final String text = element.getText();
		final String[] nameAndType = VtlFile.findVariableNameAndTypeAndScopeFilePath(text);
		if(nameAndType == null)
		{
			return;
		}

		VelocityFacade facade = VelocityFacade.getFacade(element);

		final VtlImplicitVariable variable = ((VtlFile) element.getContainingFile()).findImplicitVariable(nameAndType[0]);
		if(variable == null || facade.isPrimitiveType(variable.getPsiType()))
		{
			return;
		}
		for(PsiReference javaRef : VtlReferenceContributor.getReferencesToJavaTypes(element))
		{
			if(javaRef.resolve() == null)
			{
				TextRange range = javaRef.getRangeInElement().shiftRight(element.getTextRange().getStartOffset());
				holder.createErrorAnnotation(range, VelocityBundle.message("invalid.java.type"));
			}
		}
	}
}
