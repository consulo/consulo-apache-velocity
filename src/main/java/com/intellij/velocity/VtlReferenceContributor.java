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

package com.intellij.velocity;

import com.intellij.java.impl.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlImplicitVariable;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.document.util.TextRange;
import consulo.language.Language;
import consulo.language.pattern.PsiElementPattern;
import consulo.language.psi.*;
import consulo.language.psi.path.FileReference;
import consulo.language.util.ProcessingContext;
import consulo.util.collection.SmartList;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static consulo.language.pattern.PlatformPatterns.psiElement;
import static consulo.language.pattern.StandardPatterns.string;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlReferenceContributor extends PsiReferenceContributor
{
	public static final consulo.language.pattern.PsiElementPattern.Capture<PsiComment> VTLVARIABLE_COMMENT = psiElement(PsiComment.class).inFile(psiElement(VtlFile.class)).withText(string().contains
			(VtlFile.VTLVARIABLE_MARKER));
	public static final PsiElementPattern.Capture<PsiComment> VTLMACROLIBRARY_COMMENT = psiElement(PsiComment.class).inFile(psiElement(VtlFile.class)).withText(string().contains(VtlFile
			.VTLMACROLIBRARY_MARKER));
	public static final PsiElementPattern.Capture<consulo.language.psi.PsiComment> VELOCITY_PROPERTIES_COMMENT = psiElement(PsiComment.class).inFile(psiElement(VtlFile.class)).withText(string()
			.contains(VtlFile.VELOCITY_PROPERTIES_MARKER));

	public void registerReferenceProviders(final consulo.language.psi.PsiReferenceRegistrar registrar)
	{
		registerImplicitVariableProvider(registrar);
		registerExternalMacroLibraryProvider(registrar);
		registerVelocityPropertiesProvider(registrar);
	}

	private void registerImplicitVariableProvider(consulo.language.psi.PsiReferenceRegistrar registrar)
	{
		registrar.registerReferenceProvider(VTLVARIABLE_COMMENT, new consulo.language.psi.PsiReferenceProvider()
		{
			@Nonnull
			public PsiReference[] getReferencesByElement(@Nonnull final PsiElement element, @Nonnull final consulo.language.util.ProcessingContext context)
			{
				return getReferencesToJavaTypes(element);
			}
		});

		registrar.registerReferenceProvider(VTLVARIABLE_COMMENT, new consulo.language.psi.PsiReferenceProvider()
		{
			@Nonnull
			public consulo.language.psi.PsiReference[] getReferencesByElement(@Nonnull final consulo.language.psi.PsiElement element, @Nonnull final ProcessingContext context)
			{
				final String text = element.getText();
				TextRange range = PsiUtil.findRange(text, "name=\"", "\"");
				if(range == null)
				{
					return consulo.language.psi.PsiReference.EMPTY_ARRAY;
				}

				final String name = range.substring(text);
				final VtlImplicitVariable variable = ((VtlFile) element.getContainingFile()).findImplicitVariable(name);
				if(variable == null)
				{
					return consulo.language.psi.PsiReference.EMPTY_ARRAY;
				}

				PsiReferenceBase<PsiComment> ref = new consulo.language.psi.PsiReferenceBase<PsiComment>((consulo.language.psi.PsiComment) element, consulo.document.util.TextRange.from(range
						.getStartOffset(), name.length()))
				{
					public PsiElement resolve()
					{
						return variable;
					}

					public Object[] getVariants()
					{
						return EMPTY_ARRAY;
					}
				};
				final List<PsiReference> result = new SmartList<consulo.language.psi.PsiReference>();
				result.add(ref);
				findAndAddReferencesByElement(element, "file=\"", "\"", result);
				return result.toArray(consulo.language.psi.PsiReference.EMPTY_ARRAY);
			}
		});
	}

	private void registerExternalMacroLibraryProvider(PsiReferenceRegistrar registrar)
	{
		registrar.registerReferenceProvider(VTLMACROLIBRARY_COMMENT, new consulo.language.psi.PsiReferenceProvider()
		{
			@Nonnull
			public consulo.language.psi.PsiReference[] getReferencesByElement(@Nonnull final PsiElement element, @Nonnull final consulo.language.util.ProcessingContext context)
			{
				final List<consulo.language.psi.PsiReference> result = new SmartList<consulo.language.psi.PsiReference>();
				if(findAndAddReferencesByElement(element, "path=\"", "\"", result))
				{
					findAndAddReferencesByElement(element, "file=\"", "\"", result);
				}
				return result.toArray(consulo.language.psi.PsiReference.EMPTY_ARRAY);
			}
		});
	}

	private void registerVelocityPropertiesProvider(consulo.language.psi.PsiReferenceRegistrar registrar)
	{
		registrar.registerReferenceProvider(VELOCITY_PROPERTIES_COMMENT, new PsiReferenceProvider()
		{
			@Nonnull
			public consulo.language.psi.PsiReference[] getReferencesByElement(@Nonnull final PsiElement element, @Nonnull final consulo.language.util.ProcessingContext context)
			{
				final List<consulo.language.psi.PsiReference> result = new SmartList<consulo.language.psi.PsiReference>();
				if(findAndAddReferencesByElement(element, "path=\"", "\"", result))
				{
					findAndAddReferencesByElement(element, "runtime_root=\"", "\"", result);
					findAndAddReferencesByElement(element, "file=\"", "\"", result);
				}
				return result.toArray(consulo.language.psi.PsiReference.EMPTY_ARRAY);
			}
		});
	}

	private static boolean findAndAddReferencesByElement(@Nonnull final PsiElement element,
														 @Nonnull String startMarker,
														 @Nonnull String endMarker,
														 @Nonnull Collection<consulo.language.psi.PsiReference> collection)
	{
		final String text = element.getText();
		consulo.document.util.TextRange range = PsiUtil.findRange(text, startMarker, endMarker);
		if(range == null)
		{
			return false;
		}
		final String filePath = range.substring(text);
		FileReference[] fileReferences = PsiUtil.getFileReferences(filePath, element, range.getStartOffset(), false);
		return collection.addAll(Arrays.asList(fileReferences));
	}

	public static consulo.language.psi.PsiReference[] getReferencesToJavaTypes(PsiElement element)
	{
		final String text = element.getText();
		consulo.document.util.TextRange range = findTypeNameRange(text);
		if(range == null)
		{
			return consulo.language.psi.PsiReference.EMPTY_ARRAY;
		}
		final JavaClassReferenceProvider provider = new JavaClassReferenceProvider();
		return provider.getReferencesByString(range.substring(text), element, range.getStartOffset());
	}

	@Nullable
	public static consulo.document.util.TextRange findTypeNameRange(@Nonnull String text)
	{
		return PsiUtil.findRange(text, "type=\"", "\"");
	}

	@Nonnull
	@Override
	public Language getLanguage()
	{
		return VtlLanguage.INSTANCE;
	}
}
