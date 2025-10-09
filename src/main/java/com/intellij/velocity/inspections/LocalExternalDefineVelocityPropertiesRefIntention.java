package com.intellij.velocity.inspections;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalExternalDefineVelocityPropertiesRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalExternalDefineVelocityPropertiesRefIntention extends DefineVelocityPropertiesRefIntention {
    public LocalExternalDefineVelocityPropertiesRefIntention() {
        super(VelocityLocalize.addVelocityPropertiesRefFixNameExternal());
    }

    public void invoke(@Nonnull final Project project, final Editor editor, final PsiFile file) throws IncorrectOperationException {
        chooseTargetFile(file, editor, true);
    }
}
