package com.intellij.velocity.tests;

import com.intellij.psi.PsiType;
import consulo.language.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import com.intellij.velocity.psi.VtlExpression;

import static consulo.java.module.util.JavaClassNames.JAVA_LANG_STRING;
import static com.intellij.psi.PsiType.*;

/**
 * @author : Alexey Chmutov
 */
public abstract class VtlExpressionTypeTest extends JavaCodeInsightFixtureTestCase {

    public void testLeftString() throws Throwable {
        assertExpressionType(JAVA_LANG_STRING);
    }

    public void testRightString() throws Throwable {
        assertExpressionType(JAVA_LANG_STRING);
    }

    public void testIntPlusDouble() throws Throwable {
        assertExpressionType(DOUBLE);
    }

    public void testIntGtDouble() throws Throwable {
        assertExpressionType(BOOLEAN);
    }

    public void testBooleanOrInt() throws Throwable {
        assertExpressionType(BOOLEAN);
    }

    public void testStringMinusInt() throws Throwable {
        assertExpressionType((String)null);
    }

    public void testBooleanPlusInt() throws Throwable {
        assertExpressionType((String)null);
    }

    public void testNotString() throws Throwable {
        assertExpressionType(BOOLEAN);
    }

    public void testNotDouble() throws Throwable {
        assertExpressionType(BOOLEAN);
    }

    public void testMinusString() throws Throwable {
        assertExpressionType((String)null);
    }

    public void testMinusInt() throws Throwable {
        assertExpressionType(INT);
    }

    protected void assertExpressionType(PsiType type) throws Throwable {
        assertExpressionType(type.getCanonicalText());
    }

    protected void assertExpressionType(String typeName) throws Throwable {
        VtlExpression expression = findVtlExpressionAtCaret();
        if (typeName == null) {
            assertNull(expression.getPsiType());
        } else {
            PsiType type = expression.getPsiType();
            assertNotNull(type);
            assertEquals(typeName, type.getCanonicalText());
        }
    }

    protected String getBasePath() {
        return "/svnPlugins/velocity/tests/testData/expressionType";
    }

    protected VtlExpression findVtlExpressionAtCaret() throws Throwable {
        myFixture.setTestDataPath(getTestDataPath());
        myFixture.configureByFile(Util.getInputDataFileName(getTestName(true)));

        int offset = myFixture.getEditor().getCaretModel().getOffset();
        VtlExpression expression = PsiTreeUtil.findElementOfClassAtOffset(myFixture.getFile(), offset, VtlExpression.class, false);
        assertNotNull(expression);
        return expression;
    }
}
