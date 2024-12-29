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

import com.intellij.java.impl.psi.AbstractQualifiedReference;
import com.intellij.java.impl.psi.impl.beanProperties.BeanProperty;
import com.intellij.java.impl.psi.impl.beanProperties.BeanPropertyElement;
import com.intellij.java.language.psi.*;
import com.intellij.velocity.editorActions.VtlTailType;
import com.intellij.velocity.psi.*;
import com.intellij.velocity.psi.directives.VtlAssignment;
import com.intellij.velocity.psi.directives.VtlMacroCall;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.language.ast.ASTNode;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.editor.completion.lookup.TailTypeDecorator;
import consulo.language.impl.psi.CheckUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.ResolveResult;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.util.IncorrectOperationException;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.StringUtil;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import static com.intellij.velocity.VelocityBundle.message;
import static com.intellij.velocity.psi.PsiUtil.createVtlReferenceExpression;
import static com.intellij.velocity.psi.PsiUtil.getPresentableText;
import static com.intellij.velocity.psi.VtlElementTypes.JAVA_DOT;

/**
 * @author Alexey Chmutov
 */
public class VtlReferenceExpression extends AbstractQualifiedReference<VtlReferenceExpression> implements VtlExpression
{

	public VtlReferenceExpression(@Nonnull final ASTNode node)
	{
		super(node);
	}

	@Override
	protected boolean processVariantsInner(PsiScopeProcessor processor)
	{
		final VtlReferenceExpression qualifier = getQualifierInner();
		if(qualifier == null)
		{
			consulo.language.psi.PsiElement parent = getParent();
			if(parent instanceof VtlMacroCall)
			{
				final VtlFile containingFile = ((VtlMacroCall) parent).getContainingFile();
				return containingFile.processAllMacrosInScope(processor, consulo.language.psi.resolve.ResolveState.initial());
			}
			return processUnqualifiedVariants(processor);
		}
		final PsiType type = qualifier.getPsiType();
		if(type instanceof PsiClassType)
		{
			final PsiClass psiClass = com.intellij.java.language.psi.util.PsiUtil.resolveClassInType(type);
			if(psiClass != null && !psiClass.processDeclarations(processor, ResolveState.initial(), null, this))
			{
				return false;
			}
		}
		final consulo.language.psi.PsiElement psiElement = qualifier.resolve();
		return psiElement == null || psiElement.processDeclarations(processor, consulo.language.psi.resolve.ResolveState.initial(), null, this);
	}

	public boolean hasQualifier()
	{
		return getQualifierInner() != null;
	}

	public boolean isQualifierResolved()
	{
		VtlReferenceExpression qualifier = getQualifierInner();
		return qualifier == null || qualifier.resolve() != null;
	}

	@Nullable
	private VtlReferenceExpression getQualifierInner()
	{
		consulo.language.psi.PsiElement child = getFirstChild();
		while(child != null)
		{
			if(child instanceof VtlReferenceExpression)
			{
				return (VtlReferenceExpression) child;
			}
			if(child instanceof VtlMethodCallExpression)
			{
				return ((VtlMethodCallExpression) child).getReferenceExpression();
			}
			child = child.getNextSibling();
		}
		return null;
	}

	@Nullable
	public VtlReferenceExpression getParentReferenceExpression()
	{
		consulo.language.psi.PsiElement parent = getParent();
		if(parent instanceof VtlReferenceExpression)
		{
			return (VtlReferenceExpression) parent;
		}
		return null;
	}

	protected consulo.language.psi.ResolveResult[] resolveInner()
	{
		final String referenceName = getReferenceName();
		if(referenceName == null)
		{
			return consulo.language.psi.ResolveResult.EMPTY_ARRAY;
		}

		final consulo.language.psi.PsiElement parent = getParent();
		if(parent instanceof VtlAssignment)
		{
			VtlAssignment assignment = (VtlAssignment) parent;
			VtlVariable var = assignment.getAssignedVariable();
			if(var != null && assignment.getAssignedVariableElement() == this)
			{
				return new consulo.language.psi.ResolveResult[]{new consulo.language.psi.PsiElementResolveResult(var)};
			}
		}

		final VtlVariantsProcessor<ResolveResult> processor = new VtlVariantsProcessor<consulo.language.psi.ResolveResult>(parent, getContainingFile(), referenceName, false)
		{
			protected consulo.language.psi.ResolveResult execute(final consulo.language.psi.PsiNamedElement element, final boolean error)
			{
				if(element instanceof BeanPropertyElement)
				{
					return new consulo.language.psi.PsiElementResolveResult(((BeanPropertyElement) element).getMethod());
				}
				return new consulo.language.psi.PsiElementResolveResult(element, !error);
			}
		};
		processVariantsInner(processor);
		return processor.getVariants(consulo.language.psi.ResolveResult.EMPTY_ARRAY, Character.isLowerCase(referenceName.charAt(0)));
	}

	@Nonnull
	public String getUnresolvedMessage(boolean resolvedWithError)
	{
		final String referenceName = getReferenceName();
		consulo.language.psi.PsiElement parent = getParent();
		if(parent instanceof VtlMacroCall)
		{
			String msgKey = resolvedWithError ? "error.wrong.number.of.arguments.for.macro" : "error.cannot.resolve.macro";
			return message(msgKey, referenceName);
		}
		final VtlReferenceExpression qualifier = getQualifierInner();
		if(qualifier == null)
		{
			return message("error.cannot.resolve.variable", referenceName);
		}
		String typeName = getPresentableText(qualifier.getPsiType());
		if(!(parent instanceof VtlMethodCallExpression))
		{
			return message("error.cannot.resolve.property", referenceName, typeName);
		}
		if(!resolvedWithError)
		{
			return message("error.cannot.resolve.method", referenceName, typeName);
		}
		String argumentTypes = StringUtil.join(((VtlMethodCallExpression) parent).getArgumentTypes(), psiType -> getPresentableText(psiType), ", ");
		return message("error.no.applicable.method", referenceName, typeName, "(" + argumentTypes + ")");
	}

	@Nonnull
	protected VtlReferenceExpression parseReference(String newText)
	{
		if(!(getParent() instanceof VtlMethodCallExpression))
		{
			final String propertyName = VelocityNamingUtil.getPropertyName(newText, isFirstCharInLowerCase());
			if(propertyName != null)
			{
				newText = propertyName;
			}
		}
		return createVtlReferenceExpression(newText, getProject());
	}


	protected consulo.language.psi.PsiElement getSeparator()
	{
		return findChildByType(JAVA_DOT);
	}

	@Override
	public String toString()
	{
		return getNode().getElementType().toString();
	}

	public consulo.language.psi.PsiElement getReferenceNameElement()
	{
		return findChildByType(VtlElementTypes.IDENTIFIER);
	}

	@Override
	public boolean isReferenceTo(final consulo.language.psi.PsiElement element)
	{
		final consulo.language.psi.PsiManager manager = getManager();
		for(final consulo.language.psi.ResolveResult result : multiResolve(false))
		{
			final consulo.language.psi.PsiElement target = result.getElement();
			if(manager.areElementsEquivalent(element, target))
			{
				return true;
			}
			if(target instanceof BeanPropertyElement
					&& manager.areElementsEquivalent(element, ((BeanPropertyElement) target).getMethod()))
			{
				return true;
			}
		}
		return false;
	}

	public Object[] getVariants()
	{
		return getVariants(false);
	}

	public Object[] getVariants(boolean propertiesOnly)
	{
		final VtlVariantsProcessor<consulo.language.psi.PsiNamedElement> processor = new VtlVariantsProcessor<consulo.language.psi.PsiNamedElement>(getParent(), getContainingFile(), null,
				propertiesOnly)
		{
			protected consulo.language.psi.PsiNamedElement execute(final consulo.language.psi.PsiNamedElement element, final boolean error)
			{
				return element;
			}
		};
		processVariantsInner(processor);
		consulo.language.psi.PsiNamedElement[] variants = processor.getVariants(consulo.language.psi.PsiNamedElement.EMPTY_ARRAY, isFirstCharInLowerCase());

		return ContainerUtil.map2Array(variants, Object.class, element -> {
			LookupElementBuilder lookupElement = LookupElementBuilder.create(element);
			if(element instanceof VtlVariable)
			{
				PsiType type = ((VtlVariable) element).getPsiType();
				if(type != null)
				{
					lookupElement = lookupElement.withTypeText(type.getPresentableText());
				}
			}
			else if(element instanceof BeanPropertyElement)
			{
				final PsiType type = ((BeanPropertyElement) element).getPropertyType();
				if(type != null)
				{
					lookupElement = lookupElement.withTypeText(type.getPresentableText());
				}
			}
			else if(element instanceof PsiMethod)
			{
				return TailTypeDecorator.withTail(lookupElement, VtlTailType.METHOD_CALL_TAIL_TYPE);
			}
			else if(element instanceof VtlMacro)
			{
				PsiElement sibling = getPrevSibling();
				boolean closingBraceNeeded = sibling != null && "{".equals(sibling.getText());
				return TailTypeDecorator.withTail(lookupElement, new VtlTailType(closingBraceNeeded));
			}
			return lookupElement;
		});
	}

	protected boolean isFirstCharInLowerCase()
	{
		final String referenceName = getReferenceName();
		return referenceName == null || Character.isLowerCase(referenceName.charAt(0));
	}

	@Nonnull
	public VtlCallable[] getCallableCandidates()
	{
		final String referenceName = getReferenceName();
		if(referenceName == null)
		{
			return VtlCallable.EMPTY_ARRAY;
		}
		VtlVariantsProcessor<VtlCallable> processor = new VtlVariantsProcessor<VtlCallable>(getParent(), getContainingFile(), null, false)
		{
			protected VtlCallable execute(final consulo.language.psi.PsiNamedElement element, final boolean error)
			{
				if(!referenceName.equals(element.getName()))
				{
					return null;
				}
				if(element instanceof VtlMacro)
				{
					return (VtlMacro) element;
				}
				if(element instanceof PsiMethod)
				{
					return new VtlMethod((PsiMethod) element);
				}
				return null;
			}
		};
		processVariantsInner(processor);
		return processor.getVariants(VtlCallable.EMPTY_ARRAY, Character.isLowerCase(referenceName.charAt(0)));
	}

	@Nullable
	public PsiType getPsiType()
	{
		final consulo.language.psi.PsiElement element = resolve();
		if(element instanceof VtlVariable)
		{
			return ((VtlVariable) element).getPsiType();
		}
		if(element instanceof PsiMethod)
		{
			PsiMethod method = (PsiMethod) element;
			return getSubstitutedType(method, method.getReturnType());
		}
		if(element instanceof BeanProperty)
		{
			final BeanProperty beanProperty = (BeanProperty) element;
			return getSubstitutedType(beanProperty.getMethod(), beanProperty.getPropertyType());
		}
		return null;
	}

	private PsiType getSubstitutedType(PsiMethod method, PsiType result)
	{
		if(!(result instanceof PsiClassType))
		{
			return result;
		}
		PsiClassType resultClassType = (PsiClassType) result;
		//        if (!resultClassType.hasParameters()) { disabled due to parameter type in itself returns false from hasParameters()
		//            return result;
		//        }
		PsiClassType qualifierClassType = (PsiClassType) getQualifierInner().getPsiType();
		assert qualifierClassType != null;
		final PsiSubstitutor substitutor = PsiUtil.getSuperClassSubstitutor(method.getContainingClass(), qualifierClassType);
		return substitutor.substitute(resultClassType);
	}

	@Override
	public consulo.language.psi.PsiElement handleElementRename(final String newElementName) throws IncorrectOperationException
	{
		CheckUtil.checkWritable(this);
		final consulo.language.psi.PsiElement newReferenceName = parseReference(newElementName).getReferenceNameElement();
		getNode().replaceChild(getReferenceNameElement().getNode(), newReferenceName.getNode());
		return this;
	}

}

