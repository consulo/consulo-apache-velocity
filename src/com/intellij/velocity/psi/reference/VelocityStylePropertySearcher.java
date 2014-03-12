package com.intellij.velocity.psi.reference;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.TextOccurenceProcessor;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.MethodReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;
import com.intellij.velocity.psi.files.VtlFileType;

/**
 * @author Alexey Chmutov
 */
public class VelocityStylePropertySearcher implements QueryExecutor<PsiReference, MethodReferencesSearch.SearchParameters>
{
	@Override
	public boolean execute(@NotNull final MethodReferencesSearch.SearchParameters parameters, @NotNull final Processor<PsiReference> consumer)
	{
		final PsiMethod method = parameters.getMethod();
		final Ref<String> name = Ref.create(null);
		final Ref<Project> project = Ref.create(null);
		ApplicationManager.getApplication().runReadAction(new Runnable()
		{
			@Override
			public void run()
			{
				if(!method.isValid())
				{
					return;
				}
				project.set(method.getProject());
				name.set(VelocityNamingUtil.getPropertyNameFromAccessor(method));
			}
		});
		String nameRefValue = name.get();
		if(StringUtil.isEmpty(nameRefValue))
		{
			return true;
		}
		SearchScope searchScope = parameters.getScope();
		if(searchScope instanceof GlobalSearchScope)
		{
			searchScope = GlobalSearchScope.getScopeRestrictedByFileTypes((GlobalSearchScope) searchScope, VtlFileType.INSTANCE);
		}

		final TextOccurenceProcessor processor = new TextOccurenceProcessor()
		{
			@Override
			public boolean execute(PsiElement element, int offsetInElement)
			{
				final PsiReference[] refs = element.getReferences();
				for(PsiReference ref : refs)
				{
					if(ref.getRangeInElement().contains(offsetInElement) && ref.isReferenceTo(method))
					{
						return consumer.process(ref);
					}
				}
				return true;
			}
		};
		final PsiSearchHelper helper = PsiSearchHelper.SERVICE.getInstance(project.get());
		return helper.processElementsWithWord(processor, searchScope, nameRefValue, UsageSearchContext.IN_FOREIGN_LANGUAGES, false);
	}
}
