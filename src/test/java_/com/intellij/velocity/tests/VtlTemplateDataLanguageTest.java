package com.intellij.velocity.tests;

import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.java.JavaLanguage;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiFileFactory;
import consulo.virtualFileSystem.VirtualFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import com.intellij.velocity.psi.VtlLanguage;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.files.VtlFileViewProvider;
import com.intellij.velocity.psi.files.VtlFileViewProviderFactory;
import consulo.language.Language;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 18.06.2008
 */
public abstract class VtlTemplateDataLanguageTest extends JavaCodeInsightFixtureTestCase {
    public void testManuallyMappedDataLanguage() throws Throwable {
        VtlFile file = createFile("file1.vm");
        Util.mapTemplateDataLanguageFor(file, JavaLanguage.INSTANCE);
        VirtualFile vFile = file.getViewProvider().getVirtualFile();
        VtlFileViewProvider vtlFileViewProvider = new VtlFileViewProviderFactory().createFileViewProvider(vFile, VtlLanguage.INSTANCE, getPsiManager(), false);
        assertSame(JavaLanguage.INSTANCE, vtlFileViewProvider.getTemplateDataLanguage());
    }

    public void testDataLanguageFromExtension() throws Throwable {
        VtlFile file = createFile("file1.java.vm");
        Language dataLanguage = getTemplateDataLanguage(file);
        assertSame(JavaLanguage.INSTANCE, dataLanguage);
    }

    public void testDataLanguageDefaultsToHtml() throws Throwable {
        VtlFile file = createFile("file1.vm");
        Language dataLanguage = getTemplateDataLanguage(file);
        assertSame(HTMLLanguage.INSTANCE, dataLanguage);
    }

    private Language getTemplateDataLanguage(VtlFile file) {
        FileViewProvider fileViewProvider = file.getViewProvider();
        VtlFileViewProvider vtlFileViewProvider = assertInstanceOf(fileViewProvider, VtlFileViewProvider.class);
        return vtlFileViewProvider.getTemplateDataLanguage();
    }

    private VtlFile createFile(String name) {
        PsiFile file = PsiFileFactory.getInstance(myFixture.getProject()).createFileFromText(name, "");
        assertInstanceOf(file, VtlFile.class);
        return (VtlFile) file;
    }
}
