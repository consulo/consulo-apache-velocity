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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.velocity.psi.directives.VtlDirective;
import com.intellij.velocity.psi.files.VtlFile;

/**
 * @author Alexey Chmutov
 */
public class VtlFoldingBuilder implements FoldingBuilder, DumbAware {
    @Nonnull
    public FoldingDescriptor[] buildFoldRegions(@Nonnull final ASTNode node, @Nonnull final Document document) {
        final PsiElement element = node.getPsi();
        if (!(element instanceof VtlFile)) {
            return FoldingDescriptor.EMPTY;
        }
        List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
        for (VtlDirective composite : ((VtlFile) element).getDirectiveChildren()) {
            addFoldingDescriptors(descriptors, composite);
        }
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private static void addFoldingDescriptors(final List<FoldingDescriptor> descriptors, final VtlDirective composite) {
        final int start = composite.getFoldingStartOffset();
        final int end = composite.getFoldingEndOffset();
        final ASTNode node = composite.getNode();
        if (start + 1 >= end || node == null) {
            return;
        }
        descriptors.add(new FoldingDescriptor(node, new TextRange(start, end)));
        for (final VtlDirective child : composite.getDirectiveChildren()) {
            addFoldingDescriptors(descriptors, child);
        }
    }

    public String getPlaceholderText(@Nonnull final ASTNode node) {
        return "...";
    }

    public boolean isCollapsedByDefault(@Nonnull final ASTNode node) {
        return false;
    }
}

