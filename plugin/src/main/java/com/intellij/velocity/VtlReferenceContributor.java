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

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.util.ProcessingContext;
import com.intellij.util.SmartList;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlImplicitVariable;
import com.intellij.velocity.psi.files.VtlFile;

/**
 * @author Alexey Chmutov
 */
public class VtlReferenceContributor extends PsiReferenceContributor {
    public static final PsiElementPattern.Capture<PsiComment> VTLVARIABLE_COMMENT = psiElement(PsiComment.class).inFile(psiElement(VtlFile.class)).withText(string().contains(VtlFile.VTLVARIABLE_MARKER));
    public static final PsiElementPattern.Capture<PsiComment> VTLMACROLIBRARY_COMMENT = psiElement(PsiComment.class).inFile(psiElement(VtlFile.class)).withText(string().contains(VtlFile.VTLMACROLIBRARY_MARKER));
    public static final PsiElementPattern.Capture<PsiComment> VELOCITY_PROPERTIES_COMMENT = psiElement(PsiComment.class).inFile(psiElement(VtlFile.class)).withText(string().contains(VtlFile.VELOCITY_PROPERTIES_MARKER));

    public void registerReferenceProviders(final PsiReferenceRegistrar registrar) {
        registerImplicitVariableProvider(registrar);
        registerExternalMacroLibraryProvider(registrar);
        registerVelocityPropertiesProvider(registrar);
    }

    private void registerImplicitVariableProvider(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(VTLVARIABLE_COMMENT, new PsiReferenceProvider() {
            @Nonnull
            public PsiReference[] getReferencesByElement(@Nonnull final PsiElement element, @Nonnull final ProcessingContext context) {
                return getReferencesToJavaTypes(element);
            }
        });

        registrar.registerReferenceProvider(VTLVARIABLE_COMMENT, new PsiReferenceProvider() {
            @Nonnull
            public PsiReference[] getReferencesByElement(@Nonnull final PsiElement element, @Nonnull final ProcessingContext context) {
                final String text = element.getText();
                TextRange range = PsiUtil.findRange(text, "name=\"", "\"");
                if (range == null) {
                    return PsiReference.EMPTY_ARRAY;
                }

                final String name = range.substring(text);
                final VtlImplicitVariable variable = ((VtlFile) element.getContainingFile()).findImplicitVariable(name);
                if (variable == null) {
                    return PsiReference.EMPTY_ARRAY;
                }

                PsiReferenceBase<PsiComment> ref = new PsiReferenceBase<PsiComment>((PsiComment) element, TextRange.from(range.getStartOffset(), name.length())) {
                    public PsiElement resolve() {
                        return variable;
                    }

                    public Object[] getVariants() {
                        return EMPTY_ARRAY;
                    }
                };
                final List<PsiReference> result = new SmartList<PsiReference>();
                result.add(ref);
                findAndAddReferencesByElement(element, "file=\"", "\"", result);
                return result.toArray(PsiReference.EMPTY_ARRAY);
            }
        });
    }

    private void registerExternalMacroLibraryProvider(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(VTLMACROLIBRARY_COMMENT, new PsiReferenceProvider() {
            @Nonnull
            public PsiReference[] getReferencesByElement(@Nonnull final PsiElement element, @Nonnull final ProcessingContext context) {
                final List<PsiReference> result = new SmartList<PsiReference>();
                if (findAndAddReferencesByElement(element, "path=\"", "\"", result)) {
                    findAndAddReferencesByElement(element, "file=\"", "\"", result);
                }
                return result.toArray(PsiReference.EMPTY_ARRAY);
            }
        });
    }

    private void registerVelocityPropertiesProvider(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(VELOCITY_PROPERTIES_COMMENT, new PsiReferenceProvider() {
            @Nonnull
            public PsiReference[] getReferencesByElement(@Nonnull final PsiElement element, @Nonnull final ProcessingContext context) {
                final List<PsiReference> result = new SmartList<PsiReference>();
                if (findAndAddReferencesByElement(element, "path=\"", "\"", result)) {
                    findAndAddReferencesByElement(element, "runtime_root=\"", "\"", result);
                    findAndAddReferencesByElement(element, "file=\"", "\"", result);
                }
                return result.toArray(PsiReference.EMPTY_ARRAY);
            }
        });
    }

    private static boolean findAndAddReferencesByElement(@Nonnull final PsiElement element, @Nonnull String startMarker, @Nonnull String endMarker, @Nonnull Collection<PsiReference> collection) {
        final String text = element.getText();
        TextRange range = PsiUtil.findRange(text, startMarker, endMarker);
        if (range == null) {
            return false;
        }
        final String filePath = range.substring(text);
        FileReference[] fileReferences = PsiUtil.getFileReferences(filePath, element, range.getStartOffset(), false);
        return collection.addAll(Arrays.asList(fileReferences));
    }

    public static PsiReference[] getReferencesToJavaTypes(PsiElement element) {
        final String text = element.getText();
        TextRange range = findTypeNameRange(text);
        if (range == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        final JavaClassReferenceProvider provider = new JavaClassReferenceProvider();
        return provider.getReferencesByString(range.substring(text), element, range.getStartOffset());
    }

    @Nullable
    public static TextRange findTypeNameRange(@Nonnull String text) {
        return PsiUtil.findRange(text, "type=\"", "\"");
    }
}
