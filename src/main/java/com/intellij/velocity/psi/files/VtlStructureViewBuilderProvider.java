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
package com.intellij.velocity.psi.files;

import com.intellij.velocity.psi.VtlLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.fileEditor.structureView.StructureViewBuilder;
import consulo.language.Language;
import consulo.language.editor.structureView.PsiStructureViewFactory;
import consulo.language.editor.structureView.TemplateLanguageStructureViewBuilder;
import consulo.language.psi.PsiFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlStructureViewBuilderProvider implements PsiStructureViewFactory {
    @Override
    @Nullable
    public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return TemplateLanguageStructureViewBuilder.create(psiFile, (it, editor) -> new VtlStructureViewModel(editor, (VtlFile) psiFile));
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return VtlLanguage.INSTANCE;
    }
}
