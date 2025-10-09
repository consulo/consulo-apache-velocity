package com.intellij.velocity.inspections;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalDefineVelocityPropertiesRefForFilesIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalDefineVelocityPropertiesRefForFilesIntention extends DefineVelocityPropertiesRefForFilesIntention {
    public LocalDefineVelocityPropertiesRefForFilesIntention() {
        super(VelocityLocalize.addVelocityPropertiesRefFixNameLocal());
    }

    public void invoke(@Nonnull final consulo.project.Project project, final Editor editor, final PsiFile file) throws consulo.language.util.IncorrectOperationException {
        defineInComment(editor, file, file, false);
    }
}
