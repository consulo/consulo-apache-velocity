package consulo.velocity.java.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.TypeConversionUtil;
import com.intellij.velocity.psi.VtlExpression;
import consulo.annotation.access.RequiredReadAction;
import consulo.java.module.extension.JavaModuleExtension;
import consulo.java.module.util.JavaClassNames;
import consulo.velocity.api.facade.VelocityFacade;
import consulo.velocity.api.facade.VelocityType;
import consulo.velocity.api.psi.StandardVelocityType;

/**
 * @author VISTALL
 * @since 2020-05-19
 */
public class JavaVelocityFacade implements VelocityFacade
{
	@RequiredReadAction
	@Override
	public boolean isMyModule(Module module)
	{
		return ModuleUtilCore.getExtension(module, JavaModuleExtension.class) != null;
	}

	@Override
	public boolean isPrimitiveType(@Nullable VelocityType type)
	{
		if(VelocityFacade.super.isPrimitiveType(type))
		{
			return true;
		}

		if(type instanceof VelocityTypeOverPsiType)
		{
			return ((VelocityTypeOverPsiType) type).getPsiType() instanceof PsiPrimitiveType;
		}
		return false;
	}

	@Override
	public boolean isVoidType(@Nullable VelocityType type)
	{
		if(VelocityFacade.super.isVoidType(type))
		{
			return true;
		}

		if(type instanceof VelocityTypeOverPsiType)
		{
			return ((VelocityTypeOverPsiType) type).getPsiType().equals(PsiType.VOID);
		}
		return false;
	}

	@Override
	public VelocityType extractTypeFromIterable(@Nonnull VtlExpression vtlExpression)
	{
		PsiType psiType = extractTypeFromIterableImpl(vtlExpression);
		if(psiType == null)
		{
			return null;
		}
		return new VelocityTypeOverPsiType(psiType);
	}

	@Nullable
	@Override
	public VelocityType createTypeFromText(@Nonnull String type, @Nonnull PsiFile file, @Nullable PsiElement context)
	{
		return new VelocityTypeOverPsiType(JavaPsiFacade.getInstance(file.getProject()).getElementFactory().createTypeFromText(type, context));
	}

	@Nullable
	private PsiType convertToPsiType(@Nullable VelocityType type, @Nonnull PsiElement scopeElement)
	{
		if(type == null)
		{
			return null;
		}

		if(type instanceof VelocityTypeOverPsiType)
		{
			return ((VelocityTypeOverPsiType) type).getPsiType();
		}

		if(type == StandardVelocityType.INT)
		{
			return PsiType.INT;
		}

		if(type == StandardVelocityType.DOUBLE)
		{
			return PsiType.DOUBLE;
		}

		if(type == StandardVelocityType.BOOLEAN)
		{
			return PsiType.BOOLEAN;
		}

		if(type == StandardVelocityType.VOID)
		{
			return PsiType.VOID;
		}

		if(type == StandardVelocityType.STRING)
		{
			return PsiType.getJavaLangString(scopeElement.getManager(), scopeElement.getResolveScope());
		}

		if(type == StandardVelocityType.LIST)
		{
			return JavaPsiFacade.getElementFactory(scopeElement.getProject()).createTypeByFQClassName(JavaClassNames.JAVA_UTIL_LIST);
		}

		if(type == StandardVelocityType.MAP)
		{
			return JavaPsiFacade.getElementFactory(scopeElement.getProject()).createTypeByFQClassName(JavaClassNames.JAVA_UTIL_MAP);
		}
		return null;
	}

	@Nullable
	private PsiType extractTypeFromIterableImpl(VtlExpression expr)
	{
		if(expr == null)
		{
			return null;
		}
		PsiType type = convertToPsiType(expr.getPsiType(), expr);
		if(type == null)
		{
			return null;
		}
		if(type instanceof PsiArrayType)
		{
			return ((PsiArrayType) type).getComponentType();
		}
		if(!(type instanceof PsiClassType))
		{
			return null;
		}
		PsiClassType classType = (PsiClassType) type;
		PsiElementFactory factory = JavaPsiFacade.getInstance(expr.getProject()).getElementFactory();
		GlobalSearchScope scope = expr.getResolveScope();

		for(Object[] iterable : VELOCITY_ITERABLES)
		{
			PsiClassType iterableClassType = factory.createTypeByFQClassName((String) iterable[0], scope);
			if(!TypeConversionUtil.isAssignable(iterableClassType, classType))
			{
				continue;
			}
			final PsiClass iterableClass = iterableClassType.resolve();
			if(iterableClass == null)
			{
				continue;
			}
			final PsiSubstitutor substitutor = JavaVelocityPsiUtil.getSuperClassSubstitutor(iterableClass, classType);
			PsiTypeParameter[] paremeters = iterableClass.getTypeParameters();
			int paramIndex = ((Integer) iterable[1]).intValue();
			PsiType result = paramIndex < paremeters.length ? substitutor.substitute(paremeters[paramIndex]) : null;
			return result != null ? result : factory.createTypeByFQClassName(JavaClassNames.JAVA_LANG_OBJECT, scope);
		}
		return null;
	}

	private static final Object[][] VELOCITY_ITERABLES = {
			{
					JavaClassNames.JAVA_UTIL_ITERATOR,
					0
			},
			{
					JavaClassNames.JAVA_UTIL_COLLECTION,
					0
			},
			{
					JavaClassNames.JAVA_UTIL_MAP,
					1
			}
	};
}
