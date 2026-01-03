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

import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.velocity.VtlFileIndex;
import com.intellij.velocity.VtlGlobalMacroProvider;
import com.intellij.velocity.VtlGlobalVariableProvider;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlImplicitVariable;
import com.intellij.velocity.psi.VtlMacro;
import com.intellij.velocity.psi.VtlVariable;
import consulo.application.util.CachedValueProvider;
import consulo.component.extension.Extensions;
import consulo.fileTemplate.FileTemplateManager;
import consulo.language.psi.PsiModificationTracker;
import consulo.language.psi.PsiRecursiveElementVisitor;
import consulo.language.psi.path.FileReference;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.util.lang.ref.Ref;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.*;

/**
 * @author Alexey Chmutov
 */
class ProviderBuilder {
    private final VtlFile myFile;

    public ProviderBuilder(@Nonnull VtlFile file) {
        myFile = file;
    }

    CachedValueProvider<Collection<VtlVariable>> createGlobalVarsProvider() {
        return new CachedValueProvider<Collection<VtlVariable>>() {
            public Result<Collection<VtlVariable>> compute() {
                final Collection<VtlVariable> result = new ArrayList<VtlVariable>();
                for (final VtlGlobalVariableProvider provider : VtlGlobalVariableProvider.EP_NAME.getExtensionList()) {
                    result.addAll(provider.getGlobalVariables(myFile));
                }
                return Result.create(result, myFile);
            }
        };
    }

    CachedValueProvider<Map<String, Set<VtlMacro>>> createAllMacrosProvider() {
        return new CachedValueProvider<Map<String, Set<VtlMacro>>>() {
            public CachedValueProvider.Result<Map<String, Set<VtlMacro>>> compute() {
                final Map<String, Set<VtlMacro>> result = new HashMap<String, Set<VtlMacro>>();
                myFile.accept(new consulo.language.psi.PsiRecursiveElementVisitor() {
                    @Override
                    public void visitElement(consulo.language.psi.PsiElement element) {
                        super.visitElement(element);
                        if (element instanceof VtlMacro) {
                            registerMacro((VtlMacro) element, result);
                        }
                    }
                });
                return CachedValueProvider.Result.create(result, myFile);
            }
        };
    }

    private static void registerMacro(VtlMacro macro, Map<String, Set<VtlMacro>> result) {
        String macroName = macro.getName();
        if (macroName == null) {
            return;
        }
        Set<VtlMacro> sameNameMacros = result.get(macroName);
        if (sameNameMacros == null) {
            sameNameMacros = new HashSet<VtlMacro>();
            result.put(macroName, sameNameMacros);
        }
        sameNameMacros.add(macro);
    }

    CachedValueProvider<Map<String, VtlImplicitVariable>> createImplicitVarsProvider() {
        return new CachedValueProvider<Map<String, VtlImplicitVariable>>() {
            public CachedValueProvider.Result<Map<String, VtlImplicitVariable>> compute() {
                final Map<String, VtlImplicitVariable> result = new HashMap<String, VtlImplicitVariable>();
                Properties defaultVariables = myFile.getViewProvider().getUserData(FileTemplateManager.DEFAULT_TEMPLATE_PROPERTIES);
                if (defaultVariables != null) {
                    for (Map.Entry<Object, Object> entry : defaultVariables.entrySet()) {
                        VtlImplicitVariable var = VtlImplicitVariable.getOrCreate(result, myFile, null, (String) entry.getKey(), null);
                        var.setType((String) entry.getValue());
                    }
                }
                myFile.accept(new PsiRecursiveElementVisitor() {
                    @Override
                    public void visitComment(consulo.language.psi.PsiComment comment) {
                        final String text = comment.getText();
                        String[] nameAndTypeAndScopeFilePath = VtlFile.findVariableNameAndTypeAndScopeFilePath(text);
                        if (nameAndTypeAndScopeFilePath == null) {
                            return;
                        }
                        VtlFile scopeFile = findVtlFile(comment, text, nameAndTypeAndScopeFilePath[2]);
                        if (nameAndTypeAndScopeFilePath[2] != null && scopeFile == null) {
                            return;
                        }
                        VtlImplicitVariable var = VtlImplicitVariable.getOrCreate(result, myFile, comment, nameAndTypeAndScopeFilePath[0], scopeFile);
                        var.setType(nameAndTypeAndScopeFilePath[1]);
                    }
                });
                return CachedValueProvider.Result.create(result, myFile);
            }
        };
    }

    public CachedValueProvider<Collection<VtlMacro>> createGlobalMacrosProvider() {
        return new CachedValueProvider<Collection<VtlMacro>>() {
            public Result<Collection<VtlMacro>> compute() {
                final Collection<VtlMacro> result = new ArrayList<VtlMacro>();
                for (final VtlGlobalMacroProvider provider : Extensions.getExtensions(VtlGlobalMacroProvider.EP_NAME)) {
                    result.addAll(provider.getGlobalMacros(myFile));
                }
                return Result.create(result, myFile);
            }
        };
    }

    public CachedValueProvider<Collection<VtlFileProxy>> createMacroLibrariesProvider() {
        return new CachedValueProvider<Collection<VtlFileProxy>>() {
            public CachedValueProvider.Result<Collection<VtlFileProxy>> compute() {
                final Collection<VtlFileProxy> result = new HashSet<VtlFileProxy>();
                myFile.accept(new consulo.language.psi.PsiRecursiveElementVisitor() {
                    @Override
                    public void visitComment(consulo.language.psi.PsiComment comment) {
                        final String text = comment.getText();
                        String[] pathAndScopeFilePath = VtlFile.findMacroLibraryPathAndScopeFilePath(text);
                        if (pathAndScopeFilePath == null) {
                            return;
                        }
                        VtlFile libraryFile = findVtlFile(comment, text, pathAndScopeFilePath[0]);
                        if (libraryFile == null) {
                            return;
                        }
                        VtlFile scopeFile = findVtlFile(comment, text, pathAndScopeFilePath[1]);
                        if (pathAndScopeFilePath[1] == null || scopeFile != null) {
                            result.add(new VtlFileProxy(libraryFile, scopeFile));
                        }
                    }
                });
                return CachedValueProvider.Result.create(result, myFile);
            }
        };
    }

    @Nullable
    private static VtlFile findVtlFile(consulo.language.psi.PsiComment comment, String text, @Nullable String filePath) {
        return findFile(comment, text, filePath, VtlFile.class);
    }

    @Nullable
    private static <T extends consulo.language.psi.PsiFile> T findFile(consulo.language.psi.PsiComment comment, String text, @Nullable String filePath, Class<T> fileClass) {
        if (filePath == null) {
            return null;
        }
        final FileReference[] fileReferences = PsiUtil.getFileReferences(filePath, comment, text.indexOf(filePath), false);
        return PsiUtil.findFile(fileReferences, fileClass);
    }

    public CachedValueProvider<VelocityPropertiesProvider> createVelocityPropertiesProvider() {
        return new CachedValueProvider<VelocityPropertiesProvider>() {
            public CachedValueProvider.Result<VelocityPropertiesProvider> compute() {
                final Set dependencies = new HashSet(3);
                final consulo.util.lang.ref.Ref<VelocityPropertiesProvider> result = new Ref<VelocityPropertiesProvider>();

                consulo.language.psi.PsiRecursiveElementVisitor visitor = new consulo.language.psi.PsiRecursiveElementVisitor() {

                    @Override
                    public void visitFile(consulo.language.psi.PsiFile file) {
                        dependencies.add(file);
                        super.visitFile(file);
                    }

                    @Override
                    public void visitComment(consulo.language.psi.PsiComment comment) {
                        if (result.get() != null) {
                            return;
                        }
                        final String text = comment.getText();
                        String[] velocityPropertiesPathAndScopeFilePath = VtlFile.findVelocityPropertiesPathAndScopeFilePath(text);
                        if (velocityPropertiesPathAndScopeFilePath == null) {
                            return;
                        }
                        VtlFile scopeFile = findVtlFile(comment, text, velocityPropertiesPathAndScopeFilePath[2]);
                        if (velocityPropertiesPathAndScopeFilePath[2] != null && (scopeFile == null || !isOriginalEquivalent(myFile, scopeFile))) {
                            return;
                        }
                        consulo.language.psi.PsiFile psiFile = findFile(comment, text, velocityPropertiesPathAndScopeFilePath[0], consulo.language.psi.PsiFile.class);
                        if (psiFile instanceof PropertiesFile) {
                            dependencies.add(psiFile);
                            consulo.virtualFileSystem.VirtualFile runtimeRoot = findRuntimeRoot(comment.getContainingFile(), velocityPropertiesPathAndScopeFilePath[1]);
                            result.set(new VelocityPropertiesProvider((PropertiesFile) psiFile, runtimeRoot));
                        }
                    }
                };
                myFile.accept(visitor);
                if (result.get() == null) {
                    final Collection<VtlFile> implicitlyIncludedFiles = VtlFileIndex.getImplicitlyIncludedFiles(myFile);
                    if (implicitlyIncludedFiles.size() == 0) {
                        dependencies.add(PsiModificationTracker.MODIFICATION_COUNT);
                    }
                    for (VtlFile implicitlyIncludedFile : implicitlyIncludedFiles) {
                        implicitlyIncludedFile.accept(visitor);
                        if (result.get() != null) {
                            break;
                        }
                    }
                }
                return CachedValueProvider.Result.create(result.get(), dependencies.toArray());
            }
        };
    }

    @Nullable
    private VirtualFile findRuntimeRoot(@Nonnull consulo.language.psi.PsiFile contextFile, @Nullable String path) {
        final consulo.language.psi.PsiDirectory parent = contextFile.getParent();
        if (path == null || path.length() == 0 || parent == null) {
            return null;
        }
        final consulo.virtualFileSystem.VirtualFile context = parent.getVirtualFile();
        if (path.charAt(0) != '/') {
            return context.findFileByRelativePath(path);
        }
        final Module module = ModuleUtilCore.findModuleForPsiElement(contextFile);
        if (module == null) {
            return null;
        }
        consulo.module.content.ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        if (moduleRootManager == null) {
            return null;
        }
        for (consulo.virtualFileSystem.VirtualFile root : moduleRootManager.getContentRoots()) {
            if (VirtualFileUtil.isAncestor(root, context, false)) {
                return path.length() == 1 ? root : root.findFileByRelativePath(path.substring(1));
            }
        }
        return null;
    }

    private boolean isOriginalEquivalent(@Nonnull VtlFile file1, @Nonnull VtlFile file2) {
        return file1.getOriginalFile().isEquivalentTo(file2.getOriginalFile());
    }
}
