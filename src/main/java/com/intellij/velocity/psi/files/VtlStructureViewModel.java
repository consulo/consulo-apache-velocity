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

import com.intellij.velocity.psi.directives.VtlDirective;
import com.intellij.velocity.psi.directives.VtlDirectiveHolder;
import consulo.codeEditor.Editor;
import consulo.fileEditor.structureView.StructureViewTreeElement;
import consulo.language.editor.structureView.PsiTreeElementBase;
import consulo.language.editor.structureView.TextEditorBasedStructureViewModel;
import jakarta.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Chmutov
 */
public class VtlStructureViewModel extends TextEditorBasedStructureViewModel {
    private final VtlFile myFile;

    public VtlStructureViewModel(Editor editor, final VtlFile file) {
        super(editor, file);
        myFile = file;
    }

    @Override
    @Nonnull
    public StructureViewTreeElement getRoot() {
        return new VtlTreeElementWrapper(myFile);
    }

    private static class VtlTreeElementWrapper extends PsiTreeElementBase<VtlDirectiveHolder> {

        public VtlTreeElementWrapper(final VtlDirectiveHolder element) {
            super(element);
        }

        @Nonnull
        public Collection<StructureViewTreeElement> getChildrenBase() {
            final VtlDirectiveHolder element = getElement();
            if (element == null) {
                return Collections.emptyList();
            }

            List<StructureViewTreeElement> result = new ArrayList<StructureViewTreeElement>();
            for (final VtlDirective child : element.getDirectiveChildren()) {
                result.add(new VtlTreeElementWrapper(child));
            }
            return result;
        }

        public String getPresentableText() {
            final VtlDirectiveHolder element = getElement();
            return element == null ? "" : element.getPresentableName();
        }

    }
}
