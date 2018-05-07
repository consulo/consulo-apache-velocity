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

import com.intellij.codeInsight.template.Expression;
import com.intellij.codeInsight.template.Template;
import com.intellij.lang.properties.PropertiesLanguage;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Function;
import com.intellij.util.IncorrectOperationException;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.directives.VtlMacroCall;
import com.intellij.velocity.psi.files.VelocityPropertiesProvider;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Alexey Chmutov
 */
public abstract class DefineVelocityPropertiesRefIntention extends DefineInCommentIntention {
  public DefineVelocityPropertiesRefIntention(@Nonnull String text) {
    super(text, VelocityBundle.message("add.velocity.properties.ref.fix.name"));
  }

  @Override
  protected boolean isAvailable(@Nonnull VtlReferenceExpression ref) {
    return ref.getParent() instanceof VtlMacroCall;
  }

  protected void prepareTemplate(@Nonnull Template template,
                                 @Nonnull final PsiElement element,
                                 String relativePath,
                                 @Nonnull final PsiFile fileToInsertComment) {
    final List<String> allFiles = Util.collectFilePaths(element, new Function<PsiFile, String>() {
      public String fun(@Nonnull final PsiFile psiFile) {
        PsiFile file = psiFile.getViewProvider().getPsi(PropertiesLanguage.INSTANCE);
        if (!(file instanceof PropertiesFile)) {
          return null;
        }
        final VelocityPropertiesProvider propertiesProvider = new VelocityPropertiesProvider((PropertiesFile)file);
        List<PsiFile> macroLibs = collectReferencedLibFiles(ModuleUtil.findModuleForPsiElement(element), propertiesProvider);

        for (PsiFile macroLib : macroLibs) {
          if (!(macroLib instanceof VtlFile) ||
              ((VtlFile)macroLib).getNumberOfMacros(((VtlReferenceExpression)element).getReferenceName()) <= 0) {
            continue;
          }
          VirtualFile vFile = macroLib.getViewProvider().getVirtualFile();
          String res = Util.computeFilePath(propertiesProvider, vFile.getPath(), vFile.getName(), fileToInsertComment);
          if(res != null) return res;
        }
        return null;
      }
    });

    template.addTextSegment("#* @velocityproperties path=");
    final Expression pathExpression = new StringCollectionExpression(allFiles);
    template.addVariable("PATH", pathExpression, pathExpression, true);
    final String fileRef = relativePath != null ? " file=\"" + relativePath + "\"" : "";
    template.addTextSegment(fileRef + " *#\n");
    template.addEndVariable();
  }

  private static List<PsiFile> collectReferencedLibFiles(Module module, VelocityPropertiesProvider propertiesProvider) {
    String[] libNames = propertiesProvider.getVelocimacroLibraryNames();
    if (libNames.length == 0) return Collections.emptyList();
    List<PsiFile> files = new ArrayList<PsiFile>(libNames.length);
    for (String libName : libNames) {
      files.addAll(Arrays.asList(Util.findReferencedFiles(module, libName)));
    }
    return files;
  }

  public static class Local extends DefineVelocityPropertiesRefIntention {
    public Local() {
      super(VelocityBundle.message("add.velocity.properties.ref.fix.name.local"));
    }

    public void invoke(@Nonnull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
      defineInComment(editor, file, file, false);
    }
  }

  public static class LocalExternal extends DefineVelocityPropertiesRefIntention {
    public LocalExternal() {
      super(VelocityBundle.message("add.velocity.properties.ref.fix.name.external"));
    }

    public void invoke(@Nonnull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
      chooseTargetFile(file, editor, true);
    }
  }

  public static class ModuleWide extends DefineVelocityPropertiesRefIntention {
    public ModuleWide() {
      super(VelocityBundle.message("add.velocity.properties.ref.fix.name.module.wide"));
    }

    public void invoke(@Nonnull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
      chooseTargetFile(file, editor, false);
    }
  }
}