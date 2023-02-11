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

package com.intellij.velocity.editorActions;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.refactoring.event.RefactoringElementListener;
import consulo.language.editor.refactoring.rename.RenamePsiElementProcessor;
import consulo.language.util.IncorrectOperationException;
import com.intellij.velocity.psi.directives.VtlForeach;
import consulo.codeEditor.Editor;
import consulo.language.psi.PsiElement;
import consulo.usage.UsageInfo;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.05.2008
 */
@ExtensionImpl
public class VtlRenameProcessor extends RenamePsiElementProcessor {
    @Override
    public void renameElement(consulo.language.psi.PsiElement element, String newName, UsageInfo[] usages, RefactoringElementListener listener) throws IncorrectOperationException {
        // fixed name variable cannot be renamed
    }

    @Override
    public consulo.language.psi.PsiElement substituteElementToRename(PsiElement element, Editor editor) {
        return null;
    }

    public boolean canProcessElement(final consulo.language.psi.PsiElement element) {
        return element instanceof VtlForeach.FixedNameReferenceElement;
    }
}
