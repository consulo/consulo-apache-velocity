package com.intellij.velocity.spring;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaPackage;
import com.intellij.velocity.VtlGlobalMacroProvider;
import com.intellij.velocity.psi.VtlMacro;
import com.intellij.velocity.psi.files.VtlFile;

/**
 * @author Alexey Chmutov
 */
public class SpringGlobalMacroProvider extends VtlGlobalMacroProvider {
    private static final String SPRING_VM_PACKAGE = "org.springframework.web.servlet.view.velocity";
    private static final String SPRING_VM = "spring.vm";

    @NotNull
    public Collection<VtlMacro> getGlobalMacros(@NotNull final VtlFile file) {
        final PsiJavaPackage aPackage = JavaPsiFacade.getInstance(file.getProject()).findPackage(SPRING_VM_PACKAGE);
        if (aPackage != null) {
            for (final PsiDirectory directory : aPackage.getDirectories()) {
                final PsiFile springVmFile = directory.findFile(SPRING_VM);
                if (springVmFile instanceof VtlFile) {
                    return ((VtlFile) springVmFile).getDefinedMacros();
                }
            }
        }
        return Collections.emptySet();
    }
}
