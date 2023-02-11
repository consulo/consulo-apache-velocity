package com.intellij.velocity.inspections;

import com.intellij.velocity.VelocityBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;

import javax.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalExternalDefineVelocityPropertiesRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalExternalDefineVelocityPropertiesRefIntention extends DefineVelocityPropertiesRefIntention
{
	public LocalExternalDefineVelocityPropertiesRefIntention()
	{
		super(VelocityBundle.message("add.velocity.properties.ref.fix.name.external"));
	}

	public void invoke(@Nonnull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException
	{
		chooseTargetFile(file, editor, true);
	}
}
