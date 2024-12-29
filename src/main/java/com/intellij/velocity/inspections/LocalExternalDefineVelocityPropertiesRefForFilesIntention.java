package com.intellij.velocity.inspections;

import com.intellij.velocity.VelocityBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;

import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalExternalDefineVelocityPropertiesRefForFilesIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalExternalDefineVelocityPropertiesRefForFilesIntention extends DefineVelocityPropertiesRefForFilesIntention
{
	public LocalExternalDefineVelocityPropertiesRefForFilesIntention()
	{
		super(VelocityBundle.message("add.velocity.properties.ref.fix.name.external"));
	}

	public void invoke(@Nonnull final consulo.project.Project project, final consulo.codeEditor.Editor editor, final PsiFile file) throws IncorrectOperationException
	{
		chooseTargetFile(file, editor, true);
	}
}
