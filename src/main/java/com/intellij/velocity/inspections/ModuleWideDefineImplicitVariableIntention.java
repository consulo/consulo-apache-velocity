package com.intellij.velocity.inspections;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.ModuleWideDefineImplicitVariableIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class ModuleWideDefineImplicitVariableIntention extends DefineImplicitVariableIntention {
    public ModuleWideDefineImplicitVariableIntention() {
        super(VelocityLocalize.addImplicitVariableFixNameModuleWide());
    }

    public void invoke(@Nonnull final Project project, final Editor editor, final consulo.language.psi.PsiFile file) throws IncorrectOperationException {
        chooseTargetFile(file, editor, false);
    }
}
