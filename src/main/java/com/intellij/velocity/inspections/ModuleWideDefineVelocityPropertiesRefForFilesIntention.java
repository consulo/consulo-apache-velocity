package com.intellij.velocity.inspections;

import com.intellij.velocity.VelocityBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.project.Project;

import javax.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.ModuleWideDefineVelocityPropertiesRefForFilesIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class ModuleWideDefineVelocityPropertiesRefForFilesIntention extends DefineVelocityPropertiesRefForFilesIntention
{
	public ModuleWideDefineVelocityPropertiesRefForFilesIntention()
	{
		super(VelocityBundle.message("add.velocity.properties.ref.fix.name.module.wide"));
	}

	public void invoke(@Nonnull final Project project, final Editor editor, final PsiFile file) throws consulo.language.util.IncorrectOperationException
	{
		chooseTargetFile(file, editor, false);
	}
}
