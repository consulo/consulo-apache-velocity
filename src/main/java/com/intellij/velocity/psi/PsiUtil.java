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

package com.intellij.velocity.psi;

import com.intellij.java.impl.codeInsight.completion.util.PsiTypeCanonicalLookupElement;
import com.intellij.java.language.psi.*;
import com.intellij.java.language.psi.util.TypeConversionUtil;
import com.intellij.velocity.psi.directives.VtlAssignment;
import com.intellij.velocity.psi.directives.VtlParse;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.files.VtlFileType;
import com.intellij.velocity.psi.reference.SoftFileReferenceSet;
import com.intellij.velocity.psi.reference.VtlFileReferenceSet;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.application.ApplicationManager;
import consulo.application.util.function.Computable;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.path.FileReference;
import consulo.language.psi.path.FileReferenceSet;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import consulo.util.io.FileUtil;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.NonNls;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Alexey Chmutov
 */
public class PsiUtil {
    @NonNls
    private static final String NULL_TYPE_NAME = "???";
    private static final Pattern FULLY_QUALIFIED_NAME_PATTERN = Pattern.compile("(([a-zA-Z_$][a-zA-Z_0-9$]*\\.)*)([a-zA-Z_$][a-zA-Z_0-9$]*)");

    public static VtlReferenceExpression createVtlReferenceExpression(final String text, final consulo.project.Project project) {
        final VtlFile dummyFile = createDummyFile(project, "$" + text);
        final consulo.language.psi.PsiElement interpolation = dummyFile.getFirstChild();
        final consulo.language.psi.PsiElement refStart = interpolation.getFirstChild();
        consulo.language.psi.PsiElement ref = refStart.getNextSibling();
        assert ref instanceof VtlReferenceExpression;
        return (VtlReferenceExpression) ref;
    }

    public static consulo.language.psi.PsiElement createIdentifierElement(consulo.project.Project project, String name) {
        final VtlFile dummyFile = createDummyFile(project, "$" + name);
        final consulo.language.psi.PsiElement interpolation = dummyFile.getFirstChild();
        final consulo.language.psi.PsiElement reference = interpolation.getLastChild();
        return assertElementType(reference.getFirstChild(), VtlElementTypes.IDENTIFIER);
    }

    private static consulo.language.psi.PsiElement assertElementType(consulo.language.psi.PsiElement element, VtlTokenType type) {
        assert element != null;
        ASTNode node = element.getNode();
        assert node != null && node.getElementType() == type;
        return element;
    }

    public static VtlLiteralExpressionType.VtlStringLiteral createStringLiteral(consulo.project.Project project, String text) {
        final VtlFile dummyFile = createDummyFile(project, "#m(" + text + ")");
        final consulo.language.psi.PsiElement macroCall = dummyFile.getFirstChild();
        final consulo.language.psi.PsiElement rightParen = macroCall.getLastChild();
        final consulo.language.psi.PsiElement argList = rightParen.getPrevSibling();
        final consulo.language.psi.PsiElement stringLiteral = argList.getFirstChild();
        assert stringLiteral instanceof VtlLiteralExpressionType.VtlStringLiteral;
        return (VtlLiteralExpressionType.VtlStringLiteral) stringLiteral;
    }

    public static VtlFile createDummyFile(Project project, String text) {
        final String fileName = "dummy." + VtlFileType.INSTANCE.getDefaultExtension();
        return (VtlFile) consulo.language.psi.PsiFileFactory.getInstance(project).createFileFromText(fileName, VtlLanguage.INSTANCE, text);
    }

    public static boolean processDeclarations(@Nonnull final PsiScopeProcessor processor, @Nonnull final ResolveState state, @Nullable final consulo.language.psi.PsiElement lastParent,
											  @Nullable Set<consulo.language.psi.PsiFile> filesVisited, @Nonnull final consulo.language.psi.PsiElement elementToProcess) {
        consulo.language.psi.PsiElement child = lastParent == null ? elementToProcess.getLastChild() : lastParent.getPrevSibling();

        if (child == null || child.getParent() instanceof VtlDirectiveHeader) {
            return true;
        }
        do {
            if (child instanceof VtlNamedElement && !processor.execute(child, state)) {
                return false;
            }
            if (child instanceof VtlAssignment) {
                VtlVariable assignedVariable = ((VtlAssignment) child).getAssignedVariable();
                if (assignedVariable != null && !processor.execute(assignedVariable, state)) {
                    return false;
                }
            }
            if (child instanceof VtlParse) {
                if (filesVisited == null) {
                    filesVisited = new HashSet<consulo.language.psi.PsiFile>();
                }
                VtlFile file = ((VtlParse) child).resolveFile();
                if (file != null && !filesVisited.contains(file)) {
                    filesVisited.add(file);
                    if (!processDeclarations(processor, consulo.language.psi.resolve.ResolveState.initial(), null, filesVisited, file)) {
                        return false;
                    }
                }
            }
            child = child.getPrevSibling();
        } while (child != null);
        return true;
    }

    public static boolean isFormalNotationStart(@Nullable consulo.language.psi.PsiElement element) {
        if (element == null) {
            return false;
        }
        String childText = element.getText().trim();
        return "${".equals(childText) || "$!{".equals(childText);
    }

    @Nonnull
    public static String getPresentableText(@Nullable final PsiType psiType) {
        return psiType == null ? NULL_TYPE_NAME : psiType.getPresentableText();
    }

    @Nonnull
    public static String getUnqualifiedName(@Nullable final String typeName) {
        if (typeName == null) {
            return NULL_TYPE_NAME;
        }
        return FULLY_QUALIFIED_NAME_PATTERN.matcher(typeName).replaceAll("$3");
    }

    @Nullable
    public static PsiType getBoxedType(PsiType type, @Nonnull consulo.language.psi.PsiElement context) {
        if (!(type instanceof PsiPrimitiveType) || PsiType.VOID.equals(type)) {
            return type;
        }
      PsiClassType boxedType = ((PsiPrimitiveType)type).getBoxedType(context);
        if (boxedType == null) {
            return type;
        }
        return boxedType;
    }

    @Nonnull
    public static PsiSubstitutor getSuperClassSubstitutor(@Nonnull PsiClass superClass, @Nonnull PsiClassType classType) {
        final PsiClassType.ClassResolveResult classResolveResult = classType.resolveGenerics();
        return TypeConversionUtil.getSuperClassSubstitutor(superClass, classResolveResult.getElement(), classResolveResult.getSubstitutor());
    }

    @Nullable
    public static consulo.document.util.TextRange findRange(@Nonnull String source, @Nonnull String startMarker, @Nonnull String endMarker) {
        int start = source.indexOf(startMarker);
        if (start < 0) {
            return null;
        }
        start += startMarker.length();
        final int end = source.indexOf(endMarker, start);
        if (end < start) {
            return null;
        }
        return new TextRange(start, end);
    }

    public static boolean isTypeOf(final consulo.language.psi.PsiElement element, final VtlTokenType... elementTypes) {
        if (element == null) {
            return false;
        }
        consulo.language.psi.PsiElement child = element.getFirstChild();
        if (child == null) {
            return false;
        }
        final IElementType childType = child.getNode().getElementType();
        for (VtlTokenType elementType : elementTypes) {
            if (childType == elementType) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public static FileReference[] getFileReferences(@Nonnull String text, @Nonnull consulo.language.psi.PsiElement element, int startOffset, final boolean considerVelocityProperties) {
        FileReferenceSet set = considerVelocityProperties
                ? new VtlFileReferenceSet(text, element, startOffset)
                : new SoftFileReferenceSet(text, element, startOffset);
        return set.getAllReferences();
    }

    @Nullable
    public static <T extends consulo.language.psi.PsiFile> T findFile(@Nonnull PsiReference[] references, Class<T> fileClass) {
        for (final consulo.language.psi.PsiReference reference : references) {
            final consulo.language.psi.PsiElement target = reference.resolve();
            if (fileClass.isInstance(target)) {
                return (T) target;
            }
        }
        return null;
    }

    @Nullable
    public static String getRelativePath(@Nonnull consulo.language.psi.PsiFile base, @Nonnull consulo.language.psi.PsiFile target) {
        return getRelativePath(getPath(base), getPath(target));
    }

    @Nullable
    public static String getRelativePath(@Nullable String basePath, @Nullable String targetPath) {
        if (basePath == null || targetPath == null) {
            return null;
        }
        String relativePath = FileUtil.getRelativePath(new File(basePath), new File(targetPath));
        if (relativePath == null) {
            return null;
        }
        return FileUtil.toSystemIndependentName(relativePath);
    }

    @Nullable
    public static String getPath(@Nonnull consulo.language.psi.PsiFile file) {
        final VirtualFile baseFile = file.getVirtualFile();
        if (baseFile == null) {
            return null;
        }
        return baseFile.getPath();
    }

    public static LookupElement createPsiTypeLookupElement(final consulo.language.psi.PsiElement element, final String typeName) {
        return ApplicationManager.getApplication().runReadAction(new Computable<LookupElement>() {
            public LookupElement compute() {
                try {
                    final PsiElementFactory elementFactory = JavaPsiFacade.getInstance(element.getProject()).getElementFactory();
                    return new PsiTypeCanonicalLookupElement(elementFactory.createTypeFromText(typeName, element));
                } catch (IncorrectOperationException e) {
                    return null;
                }
            }
        });
    }
}
