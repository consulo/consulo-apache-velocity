package consulo.velocity.psi.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiQualifiedReference;
import com.intellij.psi.PsiQualifiedReferenceElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.meta.PsiMetaData;
import com.intellij.psi.meta.PsiMetaOwner;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.access.RequiredWriteAction;
import consulo.psi.PsiPackage;

/**
 * @author VISTALL
 * @since 2020-05-25
 */
public abstract class AbstractQualifiedReference<T extends AbstractQualifiedReference<T>> extends ASTWrapperPsiElement implements PsiPolyVariantReference, PsiQualifiedReferenceElement
{
	private static final ResolveCache.PolyVariantResolver<AbstractQualifiedReference> MY_RESOLVER = new ResolveCache.PolyVariantResolver<AbstractQualifiedReference>()
	{
		@Nonnull
		@Override
		public ResolveResult[] resolve(@Nonnull final AbstractQualifiedReference expression, final boolean incompleteCode)
		{
			return expression.resolveInner();
		}
	};

	protected AbstractQualifiedReference(@Nonnull final ASTNode node)
	{
		super(node);
	}

	@Override
	public final PsiReference getReference()
	{
		return this;
	}

	@RequiredReadAction
	@Override
	public final PsiElement getElement()
	{
		return this;
	}

	protected abstract ResolveResult[] resolveInner();

	@RequiredReadAction
	@Override
	@Nonnull
	public final ResolveResult[] multiResolve(final boolean incompleteCode)
	{
		PsiFile file = getContainingFile();
		return ResolveCache.getInstance(file.getProject()).resolveWithCaching(this, MY_RESOLVER, true, false, file);
	}

	@RequiredReadAction
	@Override
	@javax.annotation.Nullable
	public final PsiElement resolve()
	{
		final ResolveResult[] results = multiResolve(false);
		return results.length == 1 ? results[0].getElement() : null;
	}

	protected boolean processVariantsInner(PsiScopeProcessor processor)
	{
		final T qualifier = getQualifier();
		if(qualifier == null)
		{
			return processUnqualifiedVariants(processor);
		}

		final PsiElement psiElement = qualifier.resolve();
		return psiElement == null || psiElement.processDeclarations(processor, ResolveState.initial(), null, this);
	}

	protected boolean processUnqualifiedVariants(final PsiScopeProcessor processor)
	{
		return PsiScopesUtil.treeWalkUp(processor, this, null);
	}

	@RequiredReadAction
	@Override
	@Nonnull
	public String getCanonicalText()
	{
		return getText();
	}

	@Override
	@SuppressWarnings({"unchecked"})
	@Nullable
	public T getQualifier()
	{
		return (T) findChildByClass(getClass());
	}

	@RequiredWriteAction
	@Override
	public PsiElement handleElementRename(final String newElementName) throws IncorrectOperationException
	{
		CheckUtil.checkWritable(this);
		final PsiElement firstChildNode = ObjectUtil.assertNotNull(getFirstChild());
		final PsiElement firstInIdentifier = getClass().isInstance(firstChildNode) ? ObjectUtil.assertNotNull(firstChildNode.getNextSibling()).getNextSibling() : firstChildNode;
		getNode().removeRange(firstInIdentifier.getNode(), null);
		final PsiElement referenceName = ObjectUtil.assertNotNull(parseReference(newElementName).getReferenceNameElement());
		getNode().addChild(referenceName.getNode());
		return this;
	}

	@RequiredWriteAction
	@Override
	public PsiElement bindToElement(@Nonnull PsiElement element) throws IncorrectOperationException
	{
		CheckUtil.checkWritable(this);
		if(isReferenceTo(element))
			return this;

		if(element instanceof PsiMethod)
		{
			final PsiMethod method = (PsiMethod) element;
			final String methodName = method.getName();
			if(isDirectlyVisible(method))
				return replaceReference(methodName);

			final AbstractQualifiedReference result = replaceReference(method.getContainingClass().getQualifiedName() + "." + methodName);
			final AbstractQualifiedReference qualifier = result.getQualifier();
			assert qualifier != null;
			qualifier.shortenReferences();
			return result;
		}
		if(element instanceof PsiClass)
		{
			return replaceReference(((PsiClass) element).getQualifiedName()).shortenReferences();
		}
		if(element instanceof PsiPackage)
		{
			return replaceReference(((PsiPackage) element).getQualifiedName());
		}
		if(element instanceof PsiMetaOwner)
		{
			final PsiMetaData metaData = ((PsiMetaOwner) element).getMetaData();
			if(metaData != null)
			{
				final String name = metaData.getName(this);
				if(name != null)
				{
					return replaceReference(name);
				}
			}
		}
		return this;
	}

	private boolean isDirectlyVisible(final PsiMethod method)
	{
		final AbstractQualifiedReferenceResolvingProcessor processor = new AbstractQualifiedReferenceResolvingProcessor()
		{
			@Override
			protected void process(final PsiElement element)
			{
				if(getManager().areElementsEquivalent(element, method) && isAccessible(element))
				{
					setFound();
				}
			}
		};
		processUnqualifiedVariants(processor);
		return processor.isFound();
	}

	protected final AbstractQualifiedReference replaceReference(final String newText)
	{
		final ASTNode newNode = parseReference(newText).getNode();
		getNode().getTreeParent().replaceChild(getNode(), newNode);
		return (AbstractQualifiedReference) newNode.getPsi();
	}

	@Nonnull
	protected abstract T parseReference(String newText);

	protected boolean isAccessible(final PsiElement element)
	{
		if(element instanceof PsiMember)
		{
			final PsiMember member = (PsiMember) element;
			return JavaResolveUtil.isAccessible(member, member.getContainingClass(), member.getModifierList(), this, null, null);
		}
		return true;
	}

	@Nonnull
	protected AbstractQualifiedReference shortenReferences()
	{
		final PsiElement refElement = resolve();
		if(refElement instanceof PsiClass)
		{
			final PsiQualifiedReference reference = JavaReferenceAdjuster.getClassReferenceToShorten((PsiClass) refElement, false, this);
			if(reference instanceof AbstractQualifiedReference)
			{
				((AbstractQualifiedReference) reference).dequalify();
			}
		}
		return this;
	}

	private void dequalify()
	{
		final AbstractQualifiedReference qualifier = getQualifier();
		if(qualifier != null)
		{
			getNode().removeChild(qualifier.getNode());
			final PsiElement separator = getSeparator();
			if(separator != null)
			{
				final ASTNode separatorNode = separator.getNode();
				if(separatorNode != null)
				{
					getNode().removeChild(separatorNode);
				}
			}
		}
	}

	@RequiredReadAction
	@Override
	public boolean isReferenceTo(final PsiElement element)
	{
		final PsiManager manager = getManager();
		for(final ResolveResult result : multiResolve(false))
		{
			if(manager.areElementsEquivalent(result.getElement(), element))
				return true;
		}
		return false;
	}

	@Nullable
	protected abstract PsiElement getSeparator();

	@javax.annotation.Nullable
	protected abstract PsiElement getReferenceNameElement();

	@RequiredReadAction
	@Override
	public TextRange getRangeInElement()
	{
		final PsiElement element = getSeparator();
		final int length = getTextLength();
		return element == null ? TextRange.from(0, length) : new TextRange(element.getStartOffsetInParent() + element.getTextLength(), length);
	}

	@Override
	@Nullable
	public String getReferenceName()
	{
		final PsiElement element = getReferenceNameElement();
		return element == null ? null : element.getText().trim();
	}

	@RequiredReadAction
	@Override
	public final boolean isSoft()
	{
		return false;
	}

	protected abstract static class AbstractQualifiedReferenceResolvingProcessor extends BaseScopeProcessor
	{
		private boolean myFound = false;
		private final Set<ResolveResult> myResults = new LinkedHashSet<ResolveResult>();

		@Override
		public boolean execute(@Nonnull final PsiElement element, final ResolveState state)
		{
			if(isFound())
				return false;
			process(element);
			return true;
		}

		protected final void addResult(ResolveResult resolveResult)
		{
			myResults.add(resolveResult);
		}

		private boolean isFound()
		{
			return myFound;
		}

		@Override
		public void handleEvent(final Event event, final Object associated)
		{
			if((event == JavaScopeProcessorEvent.SET_CURRENT_FILE_CONTEXT || event == Event.SET_DECLARATION_HOLDER) && !myResults.isEmpty())
			{
				setFound();
			}
			super.handleEvent(event, associated);
		}

		protected final void setFound()
		{
			myFound = true;
		}

		protected abstract void process(PsiElement element);

		public Set<ResolveResult> getResults()
		{
			return myResults;
		}
	}

}
