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

import com.intellij.java.language.psi.PsiType;
import com.intellij.velocity.VelocityBundle;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
* User: Alexey Chmutov
* Date: 27.06.2008
*/
public class VtlOperatorExpression extends VtlCompositeElement implements VtlExpression {

    private final boolean myBinary;

    public VtlOperatorExpression(@Nonnull final ASTNode node, boolean binary) {
        super(node);
        myBinary = binary;
    }

    public PsiType getPsiType() {
        VtlExpressionTypeCalculator typeCalculator = getOperationSign().getTypeCalculator();
        VtlExpression operand1 = getOperand1();
        if (operand1 == null) {
            return null;
        }
        if(!myBinary) {
            return typeCalculator.calculateUnary(operand1);
        }
        VtlExpression operand2 = getOperand2();
        if (operand2 == null) {
            return null;
        }
        return typeCalculator.calculateBinary(operand1, operand2);
    }

    @Nonnull
    private VtlOperatorTokenType getOperationSign() {
        final ASTNode operationNode = getNode().findChildByType(VtlElementTypes.OPERATIONS);
        assert operationNode != null : getText();
        IElementType tokenType = operationNode.getElementType();
        assert tokenType instanceof VtlOperatorTokenType : getText();
        return (VtlOperatorTokenType) tokenType;
    }

    private VtlExpression getOperand1() {
        return findChildByClass(VtlExpression.class);
    }

    private VtlExpression getOperand2() {
        VtlExpression first = getOperand1();
        if (first == null) {
            return null;
        }
        PsiElement second = first.getNextSibling();
        while (second != null && !(second instanceof VtlExpression)) {
            second = second.getNextSibling();
        }
        return (VtlExpression) second;
    }

    public String getIndefiniteTypeMessage() {
        VtlExpression op1 = getOperand1();
        PsiType opType1 = op1 == null ? null : op1.getPsiType();
        if(opType1 == null) {
            return null;
        }
        if(!myBinary) {
            return VelocityBundle.message("invalid.operand.type", PsiUtil.getPresentableText(opType1));
        }
        VtlExpression op2 = getOperand2();
        PsiType opType2 = op2 == null ? null : op2.getPsiType();
        if(opType2 == null) {
            return null;
        }
        return VelocityBundle.message("invalid.operands.type", PsiUtil.getPresentableText(opType1), PsiUtil.getPresentableText(opType2));
    }
}
