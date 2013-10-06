/*
 * Copyright (c) 2000-2005 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package com.intellij.velocity.psi;

import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiType;
import com.intellij.velocity.VelocityBundle;

/**
 * @author Alexey Chmutov
 */
public class VtlParameterDeclaration extends VtlPresentableNamedElement implements VtlVariable {

    public VtlParameterDeclaration(final ASTNode node) {
        super(node);
    }

    public String getTypeName() {
        return VelocityBundle.message("type.name.macro.parameter");
    }

    @Nullable
    public PsiType getPsiType() {
        return null;
    }
}
