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

import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.files.VtlFileViewProvider;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.HighlightErrorFilter;
import consulo.language.psi.PsiErrorElement;
import consulo.language.psi.PsiFile;

import javax.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VtlErrorFilter extends HighlightErrorFilter {

  public boolean shouldHighlightErrorElement(@Nonnull final consulo.language.psi.PsiErrorElement element) {
    return !value(element);
  }

  public static boolean value(final PsiErrorElement psiErrorElement) {
    final PsiFile file = psiErrorElement.getContainingFile();
    return file.getViewProvider() instanceof VtlFileViewProvider && !(file instanceof VtlFile);
  }
}
