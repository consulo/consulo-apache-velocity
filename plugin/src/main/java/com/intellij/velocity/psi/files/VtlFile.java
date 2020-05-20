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

import gnu.trove.THashSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.velocity.VtlFileIndex;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlImplicitVariable;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.VtlMacro;
import com.intellij.velocity.psi.VtlVariable;
import com.intellij.velocity.psi.directives.VtlDirective;
import consulo.velocity.api.psi.VelocityFile;

/**
 * @author Alexey Chmutov
 */
public class VtlFile extends PsiFileBase implements VelocityFile {
  private static final String QUOTED_TEXT = "\"([^\"]*)\"";
  private static final String QUOTED_TEXT_NOT_EMPTY = "\"([^\"]+)\"";
  private static final String PATH_TO_FILE = "[ ]*path=" + QUOTED_TEXT_NOT_EMPTY;
  private static final String SCOPE_FILE_AND_ANY_TAIL = "([ ]+file=" + QUOTED_TEXT + ")?(.*)";
  @NonNls
  public static final String VTLVARIABLE_MARKER = "@vtlvariable ";
  @NonNls
  private static final Pattern IMPLICIT_VAR_DECL_PATTERN = Pattern
      .compile("(.*?)" + VTLVARIABLE_MARKER + "[ ]*name=" + QUOTED_TEXT_NOT_EMPTY + "[ ]+type=" + QUOTED_TEXT + SCOPE_FILE_AND_ANY_TAIL);
  @NonNls
  public static final String VTLMACROLIBRARY_MARKER = "@vtlmacrolibrary ";
  @NonNls
  private static final Pattern EXTERNAL_MACRO_LIBRARY_PATTERN =
      Pattern.compile("(.*?)" + VTLMACROLIBRARY_MARKER + PATH_TO_FILE + SCOPE_FILE_AND_ANY_TAIL);
  @NonNls
  public static final String VELOCITY_PROPERTIES_MARKER = "@velocityproperties ";
  @NonNls
  private static final Pattern VELOCITY_PROPERTIES_PATTERN = Pattern
      .compile("(.*?)" + VELOCITY_PROPERTIES_MARKER + PATH_TO_FILE + "([ ]+runtime_root=" + QUOTED_TEXT + ")?" + SCOPE_FILE_AND_ANY_TAIL);

  private final CachedValue<Map<String, VtlImplicitVariable>> myImplicitVars;
  private final CachedValue<Collection<VtlVariable>> myGlobalVars;
  private final CachedValue<Collection<VtlMacro>> myGlobalMacros;
  private final CachedValue<Collection<VtlFileProxy>> myMacroLibraries;
  private final CachedValue<Map<String, Set<VtlMacro>>> myAllMacros;
  private final CachedValue<VelocityPropertiesProvider> myVelocityProperties;

  public VtlFile(FileViewProvider viewProvider) {
    super(viewProvider, VtlLanguage.INSTANCE);
    ProviderBuilder builder = new ProviderBuilder(this);
    myImplicitVars = createCachedValue(builder.createImplicitVarsProvider());
    myGlobalVars = createCachedValue(builder.createGlobalVarsProvider());
    myGlobalMacros = createCachedValue(builder.createGlobalMacrosProvider());
    myMacroLibraries = createCachedValue(builder.createMacroLibrariesProvider());
    myAllMacros = createCachedValue(builder.createAllMacrosProvider());
    myVelocityProperties = createCachedValue(builder.createVelocityPropertiesProvider());
  }

  private <T> CachedValue<T> createCachedValue(final CachedValueProvider<T> provider) {
    return CachedValuesManager.getManager(getProject()).createCachedValue(provider, false);
  }

  @Nonnull
  public FileType getFileType() {
    return VtlFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return getPresentableName();
  }

  @Nonnull
  public VtlDirective[] getDirectiveChildren() {
    return findChildrenByClass(VtlDirective.class);
  }

  @Nonnull
  public String getPresentableName() {
    return "VtlFile:" + getName();
  }

  @Nullable
  public VtlImplicitVariable findImplicitVariable(String name) {
    return myImplicitVars.getValue().get(name);
  }

  @Override
  public boolean processDeclarations(@Nonnull final PsiScopeProcessor processor,
                                     @Nonnull final ResolveState state,
                                     @Nullable final PsiElement lastParent,
                                     @Nonnull final PsiElement place) {
    if (!processExportableDeclarations(processor, state, null)) {
      return false;
    }
    if (!PsiUtil.processDeclarations(processor, state, lastParent, null, this)) {
      return false;
    }
    for (final VtlVariable var : myGlobalVars.getValue()) {
      if (!processor.execute(var, state)) {
        return false;
      }
    }
    return processGlobalMacros(processor, state) &&
           processVelocityPropertiesLibraries(processor, state) &&
           processImplicitlyIncludedFiles(processor, state);
  }

  private boolean processGlobalMacros(PsiScopeProcessor processor, ResolveState state) {
    for (final VtlMacro macro : myGlobalMacros.getValue()) {
      if (!processor.execute(macro, state)) {
        return false;
      }
    }
    return true;
  }

  private boolean processExportableDeclarations(PsiScopeProcessor processor, ResolveState state, @Nullable VtlFile placeFile) {
    for (final VtlImplicitVariable var : myImplicitVars.getValue().values()) {
      if (var.isVisibleIn(placeFile) && !processor.execute(var, state)) {
        return false;
      }
    }
    return processMacroLibraries(processor, state, placeFile);
  }


  private boolean processVelocityPropertiesLibraries(PsiScopeProcessor processor, ResolveState state) {
    VelocityPropertiesProvider velocityProperties = myVelocityProperties.getValue();
    if (velocityProperties == null) {
      return true;
    }
    final VirtualFile virtual = getOriginalFile().getVirtualFile();
    if (virtual == null) {
      return true;
    }
    List<VtlFile> velocimacros = velocityProperties.getVelocimacroLibraryListBasedOn(virtual.getParent());
    for (VtlFile lib : velocimacros) {
      if (!lib.processMacrosInFile(processor, state)) {
        return false;
      }
    }
    return true;
  }

  private boolean processImplicitlyIncludedFiles(PsiScopeProcessor processor, ResolveState state) {
    final Collection<VtlFile> implicitlyIncludedFiles = VtlFileIndex.getImplicitlyIncludedFiles(this);
    if (implicitlyIncludedFiles.isEmpty()) {
      return true;
    }

    VtlFile placeFile = (VtlFile)getOriginalFile();

    for (VtlFile implicitlyIncludedFile : implicitlyIncludedFiles) {
      if (!implicitlyIncludedFile.processExportableDeclarations(processor, state, placeFile)) {
        return false;
      }
    }
    return true;
  }

  private boolean processMacroLibraries(@Nonnull final PsiScopeProcessor processor, @Nonnull final ResolveState state, VtlFile placeFile) {
    for (VtlFileProxy lib : myMacroLibraries.getValue()) {
      if (!lib.isVisibleIn(placeFile)) {
        continue;
      }
      if (!lib.getFile().processMacrosInFile(processor, state)) {
        return false;
      }
    }
    return true;
  }

  private boolean processMacrosInFile(PsiScopeProcessor processor, ResolveState state) {
    for (Set<VtlMacro> macros : myAllMacros.getValue().values()) {
      assert macros.size() > 0;
      if (!processor.execute(macros.iterator().next(), state)) {
        return false;
      }
    }
    return true;
  }

  public boolean processAllMacrosInScope(@Nonnull final PsiScopeProcessor processor, @Nonnull final ResolveState state) {
    return processGlobalMacros(processor, state) &&
           processMacroLibraries(processor, state, null) &&
           processVelocityPropertiesLibraries(processor, state) &&
           processImplicitlyIncludedFiles(processor, state) &&
           processMacrosInFile(processor, state);
  }

  public int getNumberOfMacros(@Nullable final String macroName) {
    final Set<VtlMacro> macroSet = myAllMacros.getValue().get(macroName);
    return macroSet == null ? 0 : macroSet.size();
  }

  @Nonnull
  public Set<String> getDefinedMacroNames() {
    return myAllMacros.getValue().keySet();
  }

  @Nonnull
  public Set<VtlMacro> getDefinedMacros() {
    Map<String, Set<VtlMacro>> macros = myAllMacros.getValue();
    Set<VtlMacro> res = new THashSet<VtlMacro>(macros.size());
    for (Set<VtlMacro> sameNameMacros : macros.values()) {
      res.addAll(sameNameMacros);
    }
    return res;
  }

  @Nullable
  public VelocityPropertiesProvider getVelocityProperties() {
    return myVelocityProperties.getValue();
  }

  public boolean isIdeTemplateFile() {
    return getViewProvider().getUserData(FileTemplateManager.DEFAULT_TEMPLATE_PROPERTIES) != null;
  }

  @Nullable
  public static String[] findVariableNameAndTypeAndScopeFilePath(@Nonnull String commentText) {
    Matcher matcher = IMPLICIT_VAR_DECL_PATTERN.matcher(commentText);
    if (!matcher.matches()) {
      return null;
    }
    return new String[]{matcher.group(2), matcher.group(3), matcher.group(5)};
  }

  @Nullable
  public static String[] findMacroLibraryPathAndScopeFilePath(@Nonnull String commentText) {
    Matcher matcher = EXTERNAL_MACRO_LIBRARY_PATTERN.matcher(commentText);
    if (!matcher.matches()) {
      return null;
    }
    return new String[]{matcher.group(2), matcher.group(4)};
  }

  public static String[] findVelocityPropertiesPathAndScopeFilePath(String commentText) {
    Matcher matcher = VELOCITY_PROPERTIES_PATTERN.matcher(commentText);
    if (!matcher.matches()) {
      return null;
    }
    return new String[]{matcher.group(2), matcher.group(4), matcher.group(6)};
  }
}
