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

import static com.intellij.psi.util.TypeConversionUtil.isNumericType;
import static com.intellij.psi.util.TypeConversionUtil.unboxAndBalanceTypes;

import org.jetbrains.annotations.NotNull;
import org.mustbe.consulo.java.util.JavaClassNames;
import com.intellij.psi.PsiType;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.06.2008
 */
public abstract class VtlExpressionTypeCalculator
{
	private VtlExpressionTypeCalculator()
	{
	}

	public PsiType calculateBinary(@NotNull VtlExpression leftOperand, @NotNull VtlExpression rightOperand)
	{
		PsiType rightType = rightOperand.getPsiType();
		if(rightType == null)
		{
			return null;
		}
		PsiType leftType = leftOperand.getPsiType();
		if(leftType == null)
		{
			return null;
		}
		return checkAndReturnNumeric(leftType, rightType);
	}

	public PsiType calculateUnary(@NotNull VtlExpression expression)
	{
		throw new AssertionError(this);
	}

	private static PsiType checkAndReturnNumeric(PsiType leftType, PsiType rightType)
	{
		if(isNumericType(leftType) && isNumericType(rightType))
		{
			return unboxAndBalanceTypes(leftType, rightType);
		}
		return null;
	}

	public static final VtlExpressionTypeCalculator PLUS_CALCULATOR = new VtlExpressionTypeCalculator()
	{
		@Override
		public PsiType calculateBinary(@NotNull VtlExpression leftOperand, @NotNull VtlExpression rightOperand)
		{
			PsiType rightType = rightOperand.getPsiType();
			if(rightType == null || rightType.equalsToText(JavaClassNames.JAVA_LANG_STRING))
			{
				return rightType;
			}
			PsiType leftType = leftOperand.getPsiType();
			if(leftType == null || leftType.equalsToText(JavaClassNames.JAVA_LANG_STRING))
			{
				return leftType;
			}
			return checkAndReturnNumeric(leftType, rightType);
		}
	};

	public static final VtlExpressionTypeCalculator MINUS_CALCULATOR = new VtlExpressionTypeCalculator()
	{
		@Override
		public PsiType calculateUnary(@NotNull VtlExpression operand)
		{
			PsiType type = operand.getPsiType();
			return type != null && isNumericType(type) ? type : null;
		}
	};

	public static final VtlExpressionTypeCalculator MULTIPLICATIVE_CALCULATOR = new VtlExpressionTypeCalculator()
	{
	};

	public static final VtlExpressionTypeCalculator BOOLEAN_CALCULATOR = new VtlExpressionTypeCalculator()
	{
		@Override
		public PsiType calculateBinary(@NotNull VtlExpression leftOperand, @NotNull VtlExpression rightOperand)
		{
			return PsiType.BOOLEAN;
		}

		@Override
		public PsiType calculateUnary(@NotNull VtlExpression operand)
		{
			return PsiType.BOOLEAN;
		}
	};
}
