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

import com.intellij.java.language.psi.CommonClassNames;
import com.intellij.java.language.psi.PsiType;
import com.intellij.java.language.psi.util.TypeConversionUtil;
import jakarta.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 * @since 2008-06-27
 */
public abstract class VtlExpressionTypeCalculator
{
	private VtlExpressionTypeCalculator()
	{
	}

	public PsiType calculateBinary(@Nonnull VtlExpression leftOperand, @Nonnull VtlExpression rightOperand)
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

	public PsiType calculateUnary(@Nonnull VtlExpression expression)
	{
		throw new AssertionError(this);
	}

	private static PsiType checkAndReturnNumeric(PsiType leftType, PsiType rightType)
	{
		if(TypeConversionUtil.isNumericType(leftType) && TypeConversionUtil.isNumericType(rightType))
		{
			return TypeConversionUtil.unboxAndBalanceTypes(leftType, rightType);
		}
		return null;
	}

	public static final VtlExpressionTypeCalculator PLUS_CALCULATOR = new VtlExpressionTypeCalculator()
	{
		@Override
		public PsiType calculateBinary(@Nonnull VtlExpression leftOperand, @Nonnull VtlExpression rightOperand)
		{
			PsiType rightType = rightOperand.getPsiType();
			if(rightType == null || rightType.equalsToText(CommonClassNames.JAVA_LANG_STRING))
			{
				return rightType;
			}
			PsiType leftType = leftOperand.getPsiType();
			if(leftType == null || leftType.equalsToText(CommonClassNames.JAVA_LANG_STRING))
			{
				return leftType;
			}
			return checkAndReturnNumeric(leftType, rightType);
		}
	};

	public static final VtlExpressionTypeCalculator MINUS_CALCULATOR = new VtlExpressionTypeCalculator()
	{
		@Override
		public PsiType calculateUnary(@Nonnull VtlExpression operand)
		{
			PsiType type = operand.getPsiType();
			return type != null && TypeConversionUtil.isNumericType(type) ? type : null;
		}
	};

	public static final VtlExpressionTypeCalculator MULTIPLICATIVE_CALCULATOR = new VtlExpressionTypeCalculator()
	{
	};

	public static final VtlExpressionTypeCalculator BOOLEAN_CALCULATOR = new VtlExpressionTypeCalculator()
	{
		@Override
		public PsiType calculateBinary(@Nonnull VtlExpression leftOperand, @Nonnull VtlExpression rightOperand)
		{
			return PsiType.BOOLEAN;
		}

		@Override
		public PsiType calculateUnary(@Nonnull VtlExpression operand)
		{
			return PsiType.BOOLEAN;
		}
	};
}
