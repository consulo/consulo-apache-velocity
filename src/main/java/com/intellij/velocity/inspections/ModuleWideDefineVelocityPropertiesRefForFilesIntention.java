package com.intellij.velocity.inspections;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.ModuleWideDefineVelocityPropertiesRefForFilesIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class ModuleWideDefineVelocityPropertiesRefForFilesIntention extends DefineVelocityPropertiesRefForFilesIntention {
    public ModuleWideDefineVelocityPropertiesRefForFilesIntention() {
        super(VelocityLocalize.addVelocityPropertiesRefFixNameModuleWide());
    }

    public void invoke(@Nonnull final Project project, final Editor editor, final PsiFile file) throws consulo.language.util.IncorrectOperationException {
        chooseTargetFile(file, editor, false);
    }
}
