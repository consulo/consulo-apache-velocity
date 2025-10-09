package com.intellij.velocity.inspections;

import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.codeEditor.Editor;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import jakarta.annotation.Nonnull;

@ExtensionImpl
@IntentionMetaData(ignoreId = "velocity.LocalExternalDefineImplicitVariableIntention", fileExtensions = "vm", categories = "Apache Velocity")
public class LocalExternalDefineImplicitVariableIntention extends DefineImplicitVariableIntention {
    public LocalExternalDefineImplicitVariableIntention() {
        super(VelocityLocalize.addImplicitVariableFixNameExternal());
    }

    public void invoke(@Nonnull final Project project, final Editor editor, final PsiFile file) throws consulo.language.util.IncorrectOperationException {
        chooseTargetFile(file, editor, true);
    }
}
