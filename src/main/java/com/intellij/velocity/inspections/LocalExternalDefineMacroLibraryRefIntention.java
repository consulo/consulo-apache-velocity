package com.intellij.velocity.inspections;

import com.intellij.velocity.VelocityBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;

import javax.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalExternalDefineMacroLibraryRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalExternalDefineMacroLibraryRefIntention extends DefineMacroLibraryRefIntention
{
	public LocalExternalDefineMacroLibraryRefIntention()
	{
		super(VelocityBundle.message("add.macro.library.ref.fix.name.external"));
	}

	public void invoke(@Nonnull final consulo.project.Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException
	{
		chooseTargetFile(file, editor, true);
	}
}
