package com.intellij.velocity.inspections;

import com.intellij.velocity.VelocityBundle;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.project.Project;

import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.ModuleWideDefineMacroLibraryRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class ModuleWideDefineMacroLibraryRefIntention extends DefineMacroLibraryRefIntention
{
	public ModuleWideDefineMacroLibraryRefIntention()
	{
		super(VelocityBundle.message("add.macro.library.ref.fix.name.module.wide"));
	}

	public void invoke(@Nonnull final Project project, final consulo.codeEditor.Editor editor, final consulo.language.psi.PsiFile file) throws consulo.language.util.IncorrectOperationException
	{
		chooseTargetFile(file, editor, false);
	}
}
