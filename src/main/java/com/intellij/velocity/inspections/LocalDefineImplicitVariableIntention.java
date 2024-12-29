package com.intellij.velocity.inspections;

import com.intellij.velocity.VelocityBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.project.Project;

import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalDefineImplicitVariableIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalDefineImplicitVariableIntention extends DefineImplicitVariableIntention
{
	public LocalDefineImplicitVariableIntention()
	{
		super(VelocityBundle.message("add.implicit.variable.fix.name.local"));
	}

	public void invoke(@Nonnull final Project project, final consulo.codeEditor.Editor editor, final consulo.language.psi.PsiFile file) throws consulo.language.util.IncorrectOperationException
	{
		defineInComment(editor, file, file, false);
	}
}
