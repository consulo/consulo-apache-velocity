package com.intellij.velocity.tests;

import com.intellij.lang.properties.references.PropertyReference;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import java.util.function.Function;

import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.VtlLoopVariable;
import com.intellij.velocity.psi.VtlParameterDeclaration;
import com.intellij.velocity.psi.VtlVariable;
import com.intellij.velocity.psi.VtlImplicitVariable;
import com.intellij.velocity.psi.directives.VtlForeach;
import com.intellij.velocity.psi.files.VtlFile;
import consulo.java.module.util.JavaClassNames;
import consulo.language.psi.ResolveResult;
import consulo.language.psi.meta.PsiPresentableMetaData;
import consulo.util.collection.ContainerUtil;

import java.util.List;

/**
 * @author Alexey Chmutov
 */
public abstract class VtlResolveTest extends JavaCodeInsightFixtureTestCase {

    protected String getBasePath() {
        return "/svnPlugins/velocity/tests/testData/resolve/";
    }

    @Override
    protected void tuneFixture(final JavaModuleFixtureBuilder moduleBuilder) {
        moduleBuilder.setMockJdkLevel(JavaModuleFixtureBuilder.MockJdkLevel.jdk15);
    }

    public void testJavaPropertyToGetter() throws Throwable {
        Util.addJavaClassTo(myFixture);
        assertEquals("getProp1", assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class).getName());
    }

    public void testJavaPropertyToSetter() throws Throwable {
        Util.addJavaClassTo(myFixture);
        assertEquals("setProp1", assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class).getName());
    }

    public void testJavaGetter() throws Throwable {
        Util.addJavaClassTo(myFixture);
        assertEquals("getProp1", assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class).getName());
    }

    public void testJavaSetter() throws Throwable {
        Util.addJavaClassTo(myFixture);
        assertEquals("setProp1", assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class).getName());
    }

    public void testVariableAssignedFromGetter() throws Throwable {
        PsiClass clazz = Util.addJavaClassTo(myFixture);
        VtlVariable var = assertInstanceOf(resolveReferenceAtCaret(), VtlVariable.class);
        PsiType varType = var.getPsiType();
        assertInstanceOf(varType, PsiClassType.class);
        PsiClass varClass = com.intellij.psi.util.PsiUtil.resolveClassInType(varType);
        assertEquals(clazz, varClass);
        consulo.language.psi.meta.PsiPresentableMetaData metadata = assertInstanceOf(var, PsiPresentableMetaData.class);
        assertEquals(VelocityBundle.message("type.name.variable"), metadata.getTypeName());
    }

    public void testInnerLoopVariable() throws Throwable {
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        assertNull(loopVar.getPsiType());
        assertEquals(37, loopVar.getTextOffset());
        assertEquals(VelocityBundle.message("type.name.loop.variable"), loopVar.getTypeName());
    }

    public void testOuterLoopVariable() throws Throwable {
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        assertNotNull(loopVar.getPsiType());
        assertEquals(10, loopVar.getTextOffset());
    }

    public void testLoopVariableFromMap() throws Throwable {
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        PsiType varType = loopVar.getPsiType();
        assertNotNull(varType);
        assertEquals("Double", varType.getCanonicalText());
    }

    public void testLoopVariableFromListLiteral() throws Throwable {
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        PsiType varType = loopVar.getPsiType();
        assertNotNull(varType);
        assertEquals("java.lang.Object", varType.getCanonicalText());
    }

    public void testLoopVariableFromIterator() throws Throwable {
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        PsiType varType = loopVar.getPsiType();
        assertNotNull(varType);
        assertEquals("String", varType.getCanonicalText());
    }

    public void testLoopVariableFromArray() throws Throwable {
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        PsiType varType = loopVar.getPsiType();
        assertNotNull(varType);
        assertEquals("java.util.Date", varType.getCanonicalText());
    }

    public void testLoopVariableFromChainedMethods() throws Throwable {
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        PsiType varType = loopVar.getPsiType();
        assertNotNull(varType);
        assertEquals("String", varType.getCanonicalText());
    }

    public void testLoopVariableFromChainedProperties() throws Throwable {
        VtlVariable var = assertInstanceOf(resolveReferenceAtCaret(), VtlVariable.class);
        PsiType varType = var.getPsiType();
        assertNotNull(varType);
        assertEquals("Date", varType.getCanonicalText());
    }

    public void testLoopVariableFromLoopVariable() throws Throwable {
        Util.addJavaInterfaceWithOverloadedMethodTo(myFixture);
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        PsiType varType = loopVar.getPsiType();
        assertNotNull(varType);
        assertEquals("my.pack.SomeInterface", varType.getCanonicalText());
        assertEquals(72, loopVar.getTextOffset());
    }

    public void testVelocityCount() throws Throwable {
        doTestFixedNameReference("velocityCount", 27);
    }

    public void testVelocityHasNext() throws Throwable {
        doTestFixedNameReference("velocityHasNext", 29);
    }

    private void doTestFixedNameReference(String elementName, int elementTextOffset) throws Throwable {
        Util.addEmptyJavaClassTo(myFixture);
        VtlForeach.FixedNameReferenceElement vc = assertInstanceOf(resolveReferenceAtCaret(), VtlForeach.FixedNameReferenceElement.class);
        assertEquals(elementName, vc.getName());
        assertNotNull(vc.getPsiType());
        assertEquals(elementTextOffset, vc.getNavigationElement().getTextOffset());
    }

    public void testOverrideMacroParamInForeach() throws Throwable {
        Util.addEmptyJavaClassTo(myFixture);
        VtlLoopVariable loopVar = assertInstanceOf(resolveReferenceAtCaret(), VtlLoopVariable.class);
        assertNotNull(loopVar.getPsiType());
        assertEquals(30, loopVar.getTextOffset());
    }

    public void testOverrideMacroParam() throws Throwable {
        Util.addEmptyJavaClassTo(myFixture);
        VtlVariable var = assertInstanceOf(resolveReferenceAtCaret(), VtlVariable.class);
        assertNotNull(var.getPsiType());
        assertEquals(26, var.getNavigationElement().getTextOffset());
        consulo.language.psi.meta.PsiPresentableMetaData metadata = assertInstanceOf(var, consulo.language.psi.meta.PsiPresentableMetaData.class);
        assertEquals(VelocityBundle.message("type.name.variable"), metadata.getTypeName());
    }

    public void testMacroParamAndForeach() throws Throwable {
        Util.addEmptyJavaClassTo(myFixture);
        VtlParameterDeclaration param = assertInstanceOf(resolveReferenceAtCaret(), VtlParameterDeclaration.class);
        assertNull(param.getPsiType());
        assertEquals(12, param.getTextOffset());
        assertEquals(VelocityBundle.message("type.name.macro.parameter"), param.getTypeName());
    }

    public void testMacroParam() throws Throwable {
        Util.addEmptyJavaClassTo(myFixture);
        VtlParameterDeclaration param = assertInstanceOf(resolveReferenceAtCaret(), VtlParameterDeclaration.class);
        assertNull(param.getPsiType());
        assertEquals(12, param.getTextOffset());
    }

    public void testGetByString() throws Throwable {
        myFixture.addClass("package my.pack; " +
                "public class GetterOwner {" +
                "  public Object get(Object o) { return o;}" +
                "  public Object get(String s) { return s;}" +
                "}");
        doTestGetMethod(JavaClassNames.JAVA_LANG_STRING);
    }

    public void testGetByObject() throws Throwable {
        myFixture.addClass("package my.pack; " +
                "public class GetterOwner {" +
                "  public Object get(Object o) { return o;}" +
                "}");
        doTestGetMethod(JavaClassNames.JAVA_LANG_OBJECT);
    }

    private void doTestGetMethod(String className) throws Throwable {
        final PsiMethod method = assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class);
        assertEquals("get", method.getName());
        PsiParameter[] parameters = method.getParameterList().getParameters();
        assertEquals(1, parameters.length);
        final PsiElementFactory factory = JavaPsiFacade.getInstance(method.getProject()).getElementFactory();
        PsiClassType javaLangObject = factory.createTypeByFQClassName(className, method.getResolveScope());
        assertTrue(parameters[0].getType().isAssignableFrom(javaLangObject));
    }

    public void testLowerCaseGetter() throws Throwable {
        myFixture.addClass("package my.pack; " +
                "public class GetterOwner {" +
                "  public Object getlowercase() { return null;}" +
                "}");
        assertEquals("getlowercase", assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class).getName());
    }

    public void testUpperCaseGetter() throws Throwable {
        Util.addJavaClassWith2Getters(myFixture);
        assertEquals("getUpperCase", assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class).getName());
    }

    public void testBothCasesGettersAndLowerCaseRef() throws Throwable {
        Util.addJavaClassWith3Getters(myFixture);
        assertEquals("getlowerCase", assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class).getName());
    }

    public void testBothCasesGettersAndUpperCaseRef() throws Throwable {
        Util.addJavaClassWith3Getters(myFixture);
        assertEquals("getLowerCase", assertInstanceOf(resolveReferenceAtCaret(), PsiMethod.class).getName());
    }

    public void testUnresolvedGetter() throws Throwable {
        myFixture.addClass("package my.pack; " +
                "public class GetterOwner {" +
                "  public Object getlowerCase() { return null;}" +
                "  public Object getLowerCase() { return null;}" +
                "}");
        consulo.language.psi.PsiReference ref = myFixture.getReferenceAtCaretPosition(Util.getInputDataFileName(getTestName(true)));
        assertNotNull(ref);
        assertNull(ref.resolve());
    }

    public void testPropertyReference() throws Throwable {
        consulo.language.psi.PsiReference ref = myFixture.getReferenceAtCaretPosition(Util.getInputDataFileName(getTestName(true)));
        assertInstanceOf(ref, PropertyReference.class);
    }

    public void testPropertyReferenceDoublequoted() throws Throwable {
        consulo.language.psi.PsiReference ref = myFixture.getReferenceAtCaretPosition(Util.getInputDataFileName(getTestName(true)));
        assertInstanceOf(ref, PropertyReference.class);
    }

    public void testFileReferenceDoublequoted() throws Throwable {
        myFixture.copyFileToProject("macroLibrary.vm");
        VtlFile file = assertInstanceOf(resolveReferenceAtCaret(), VtlFile.class);
        assertEquals("macroLibrary.vm", file.getName());
    }

    public void testFileNameThruResourceLoaderPath() throws Throwable {
        myFixture.copyFileToProject("velocity.properties");
        myFixture.copyFileToProject("velocity_implicit1.vm");
        myFixture.copyDirectoryToProject("runtime", "runtime");
        VtlFile file = assertInstanceOf(resolveReferenceAtCaret(), VtlFile.class);
        assertEquals("fileToParse.vm", file.getName());
    }

    public void testExternalImplicitVariable() throws Throwable {
        myFixture.copyFileToProject("velocity_implicit1.vm");
        VtlImplicitVariable var = assertInstanceOf(resolveReferenceAtCaret(), VtlImplicitVariable.class);
        assertEquals("someVarExternal", var.getName());
    }

    public void testExternalImplicitVariableUnresolved() throws Throwable {
        myFixture.copyFileToProject("velocity_implicit1.vm");
        assertNull("someVarExternal shouldn't be resolved", resolveReferenceAtCaret());
    }

    public void testExternalImplicitVariableUnresolvedTargetFileExists() throws Throwable {
        myFixture.copyFileToProject("velocity_implicit1.vm");
        myFixture.copyFileToProject(Util.getInputDataFileName("externalImplicitVariable"));
        assertNull("someVarExternal shouldn't be resolved", resolveReferenceAtCaret());
    }

    protected consulo.language.psi.PsiElement resolveReferenceAtCaret() throws Throwable {
        return myFixture.getReferenceAtCaretPositionWithAssertion(Util.getInputDataFileName(getTestName(true))).resolve();
    }

    protected List<consulo.language.psi.PsiElement> multiResolveAtCaret() throws Throwable {
        final consulo.language.psi.PsiReference reference = myFixture.getReferenceAtCaretPositionWithAssertion(Util.getInputDataFileName(getTestName(true)));
        final consulo.language.psi.PsiPolyVariantReference multiReference = assertInstanceOf(reference, consulo.language.psi.PsiPolyVariantReference.class);
        return ContainerUtil.map(multiReference.multiResolve(false), new Function<ResolveResult, consulo.language.psi.PsiElement>() {
            public consulo.language.psi.PsiElement fun(final consulo.language.psi.ResolveResult resolveResult) {
                return resolveResult.getElement();
            }
        });
    }
}