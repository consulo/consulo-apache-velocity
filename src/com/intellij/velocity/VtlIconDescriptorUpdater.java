package com.intellij.velocity;

import org.jetbrains.annotations.NotNull;
import com.intellij.icons.AllIcons;
import com.intellij.ide.IconDescriptor;
import com.intellij.ide.IconDescriptorUpdater;
import com.intellij.psi.PsiElement;
import com.intellij.velocity.psi.VtlParameterDeclaration;
import com.intellij.velocity.psi.VtlVariable;

/**
 * @author VISTALL
 * @since 06.10.13.
 */
public class VtlIconDescriptorUpdater implements IconDescriptorUpdater
{
	@Override
	public void updateIcon(@NotNull IconDescriptor iconDescriptor, @NotNull PsiElement element, int i)
	{
		if(element instanceof VtlParameterDeclaration)
		{
			iconDescriptor.setMainIcon(AllIcons.Nodes.Parameter);
		}
		if(element instanceof VtlVariable)
		{
			iconDescriptor.setMainIcon(AllIcons.Nodes.Variable);
		}
	}
}
