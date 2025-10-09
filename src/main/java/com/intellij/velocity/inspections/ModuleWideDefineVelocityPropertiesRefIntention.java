package com.intellij.velocity.inspections;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.ModuleWideDefineVelocityPropertiesRefIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class ModuleWideDefineVelocityPropertiesRefIntention extends DefineVelocityPropertiesRefIntention {
    public ModuleWideDefineVelocityPropertiesRefIntention() {
        super(VelocityLocalize.addVelocityPropertiesRefFixNameModuleWide());
    }

    public void invoke(@Nonnull final Project project, final consulo.codeEditor.Editor editor, final consulo.language.psi.PsiFile file) throws IncorrectOperationException {
        chooseTargetFile(file, editor, false);
    }
}
