package com.intellij.velocity.inspections;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalDefineMacroLibraryRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalDefineMacroLibraryRefIntention extends DefineMacroLibraryRefIntention {
    public LocalDefineMacroLibraryRefIntention() {
        super(VelocityLocalize.addMacroLibraryRefFixNameLocal());
    }

    public void invoke(@Nonnull final consulo.project.Project project, final Editor editor, final PsiFile file) throws consulo.language.util.IncorrectOperationException {
        defineInComment(editor, file, file, false);
    }
}
