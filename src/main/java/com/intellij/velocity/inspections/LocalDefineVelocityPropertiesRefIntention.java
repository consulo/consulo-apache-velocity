package com.intellij.velocity.inspections;

import com.intellij.velocity.VelocityBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;

import javax.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalDefineVelocityPropertiesRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalDefineVelocityPropertiesRefIntention extends DefineVelocityPropertiesRefIntention
{
	public LocalDefineVelocityPropertiesRefIntention()
	{
		super(VelocityBundle.message("add.velocity.properties.ref.fix.name.local"));
	}

	public void invoke(@Nonnull final Project project, final consulo.codeEditor.Editor editor, final PsiFile file) throws IncorrectOperationException
	{
		defineInComment(editor, file, file, false);
	}
}
