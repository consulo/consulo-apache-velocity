package com.intellij.velocity.inspections;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalDefineVelocityPropertiesRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalDefineVelocityPropertiesRefIntention extends DefineVelocityPropertiesRefIntention {
    public LocalDefineVelocityPropertiesRefIntention() {
        super(VelocityLocalize.addVelocityPropertiesRefFixNameLocal());
    }

    public void invoke(@Nonnull final Project project, final consulo.codeEditor.Editor editor, final PsiFile file) throws IncorrectOperationException {
        defineInComment(editor, file, file, false);
    }
}
