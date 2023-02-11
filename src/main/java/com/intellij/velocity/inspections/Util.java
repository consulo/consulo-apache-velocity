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

import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.files.VelocityPropertiesProvider;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.reference.VtlFileReferenceSet;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.codeEditor.Editor;
import consulo.content.ContentIterator;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.language.psi.PsiReference;
import consulo.language.psi.path.FileReference;
import consulo.language.psi.path.FileReferenceSet;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.search.FilenameIndex;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleFileIndex;
import consulo.module.content.ModuleRootManager;
import consulo.util.collection.ContainerUtil;
import consulo.util.collection.SmartList;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

import static com.intellij.velocity.psi.PsiUtil.getRelativePath;

/**
 * @author Alexey Chmutov
 */
public class Util {
  @Nullable
  static VtlReferenceExpression findReferenceExpression(@Nonnull Editor editor, @Nonnull PsiFile file) {
    return findReferenceExpression(editor, file, VtlReferenceExpression.class);
  }

  @Nullable
  static <T extends PsiReference> T findReferenceExpression(@Nonnull consulo.codeEditor.Editor editor, @Nonnull PsiFile file, @Nonnull Class<T> refClass) {
    int offset = editor.getCaretModel().getOffset();
    final CharSequence charSequence = editor.getDocument().getCharsSequence();
    if (charSequence.length() == offset || charSequence.length() > offset && !Character.isJavaIdentifierPart(charSequence.charAt(offset))) {
      offset--;
    }
    final PsiReference reference = file.findReferenceAt(offset);
    return refClass.isInstance(reference) ? (T)reference : null;
  }

  @Nonnull
  static <T> List<T> collectFilePaths(@Nonnull consulo.language.psi.PsiElement element, @Nonnull final Function<consulo.language.psi.PsiFile, T> converter) {
    final List<T> allFiles = new SmartList<T>();
    final PsiManager psiManager = element.getManager();
    ModuleFileIndex fileIndex = ModuleRootManager.getInstance(ModuleUtilCore.findModuleForPsiElement(element)).getFileIndex();
    fileIndex.iterateContent(new ContentIterator() {
      public boolean processFile(consulo.virtualFileSystem.VirtualFile fileOrDir) {
        PsiFile psiFile = psiManager.findFile(fileOrDir);
        if (psiFile != null) {
          ContainerUtil.addIfNotNull(allFiles, converter.apply(psiFile));
        }
        return true;
      }
    });
    return allFiles;
  }

  static boolean canSetVelocityProperties(@Nullable PsiFile file) {
    if (file == null) {
      return false;
    }
    VtlFile vtlFile = (VtlFile)file.getViewProvider().getPsi(VtlLanguage.INSTANCE);
    return vtlFile != null && vtlFile.getVelocityProperties() == null;

  }

  @Nullable
  static VtlFileReferenceSet findVtlFileReferenceSet(@Nonnull PsiElement element) {
    for (PsiReference ref : element.getReferences()) {
      if (!(ref instanceof FileReference)) {
        continue;
      }
      FileReferenceSet refSet = ((consulo.language.psi.path.FileReference)ref).getFileReferenceSet();
      if (refSet instanceof VtlFileReferenceSet && refSet.getLastReference() != null) {
        return (VtlFileReferenceSet)refSet;
      }
    }
    return null;
  }

  @Nonnull
  static consulo.language.psi.PsiFile[] findReferencedFiles(@Nullable final Module module, @Nonnull String nameFile) {
    if (module == null) return PsiFile.EMPTY_ARRAY;
    return FilenameIndex.getFilesByName(module.getProject(), nameFile, new GlobalSearchScope(module.getProject()) {
      private final consulo.virtualFileSystem.VirtualFile[] myContentRoots = ModuleRootManager.getInstance(module).getContentRoots();

      public boolean contains(VirtualFile file) {
        for (consulo.virtualFileSystem.VirtualFile contentRoot : myContentRoots) {
          if (VirtualFileUtil.isAncestor(contentRoot, file, false)) {
            return true;
          }
        }
        return false;
      }

      public int compare(consulo.virtualFileSystem.VirtualFile file1, consulo.virtualFileSystem.VirtualFile file2) {
        return 0;
      }

      public boolean isSearchInModuleContent(@Nonnull consulo.module.Module aModule) {
        return aModule == module;
      }

      public boolean isSearchInLibraries() {
        return false;
      }
    });
  }

  static String computeFilePath(VelocityPropertiesProvider velocityProperties,
                                String referencedFilePath,
                                String pathString,
                                consulo.language.psi.PsiFile fileToInsertComment) {
    for (String path : velocityProperties.getResourceLoaderPathList()) {
      String relativePath = ".".equals(path) ? pathString : path + "/" + pathString;
      if (!referencedFilePath.endsWith(relativePath)) {
        continue;
      }
      int len = referencedFilePath.length() - relativePath.length();
      String vtlRootPath = getRelativePath(PsiUtil.getPath(fileToInsertComment), referencedFilePath.substring(0, len));
      if (vtlRootPath != null) {
        String propFilePath = getRelativePath(fileToInsertComment, velocityProperties.getPropertiesFile().getContainingFile());
        if (propFilePath != null) {
          return "\"" + propFilePath + "\" runtime_root=\"" + vtlRootPath + "\"";
        }
      }
    }
    return null;
  }

}
