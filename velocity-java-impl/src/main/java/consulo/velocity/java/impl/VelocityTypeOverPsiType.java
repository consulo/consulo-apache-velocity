package consulo.velocity.java.impl;

import javax.annotation.Nonnull;

import com.intellij.psi.PsiType;
import consulo.velocity.api.facade.VelocityType;

/**
 * @author VISTALL
 * @since 2020-05-19
 */
public class VelocityTypeOverPsiType implements VelocityType
{
	private final PsiType myPsiType;

	public VelocityTypeOverPsiType(PsiType psiType)
	{
		myPsiType = psiType;
	}

	public PsiType getPsiType()
	{
		return myPsiType;
	}

	@Nonnull
	@Override
	public String getPresentableText()
	{
		return myPsiType.getPresentableText();
	}
}
