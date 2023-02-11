package com.intellij.velocity.inspections;

import com.intellij.velocity.VelocityBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;

import javax.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.ModuleWideDefineVelocityPropertiesRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class ModuleWideDefineVelocityPropertiesRefIntention extends DefineVelocityPropertiesRefIntention
{
	public ModuleWideDefineVelocityPropertiesRefIntention()
	{
		super(VelocityBundle.message("add.velocity.properties.ref.fix.name.module.wide"));
	}

	public void invoke(@Nonnull final Project project, final consulo.codeEditor.Editor editor, final consulo.language.psi.PsiFile file) throws IncorrectOperationException
	{
		chooseTargetFile(file, editor, false);
	}
}
