package com.intellij.velocity.tests;

import consulo.ide.impl.idea.codeInsight.navigation.actions.GotoTypeDeclarationAction;
import consulo.language.psi.PsiElement;
import consulo.language.psi.search.IndexPattern;
import consulo.ui.ex.action.ActionManager;
import consulo.ui.ex.action.IdeActions;
import com.intellij.psi.PsiClass;
import consulo.language.psi.search.IndexPatternOccurrence;
import consulo.language.psi.search.IndexPatternSearch;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import consulo.language.editor.action.CodeInsightAction;

/**
 * @author Alexey Chmutov
 */
public abstract class VtlActionsTest extends JavaCodeInsightFixtureTestCase
{

	public String getBasePath()
	{
		return "/svnPlugins/velocity/tests/testData/actions/";
	}

	public void testCommentBlock() throws Throwable
	{
		doTest(IdeActions.ACTION_COMMENT_BLOCK);
	}

	public void testCommentLine() throws Throwable
	{
		doTest(IdeActions.ACTION_COMMENT_LINE);
	}

	public void testUncommentBlock() throws Throwable
	{
		doTest(IdeActions.ACTION_COMMENT_BLOCK);
	}

	public void testCommentPlainTextBlock() throws Throwable
	{
		doTest(IdeActions.ACTION_COMMENT_BLOCK);
	}

	public void testCommentPlainTextLine() throws Throwable
	{
		doTest(IdeActions.ACTION_COMMENT_LINE);
	}

	public void testUncommentLine() throws Throwable
	{
		doTest(IdeActions.ACTION_COMMENT_LINE);
	}

	public void testGotoTypeDeclaration() throws Throwable
	{
		final PsiClass barClass = Util.addEmptyJavaClassTo(myFixture);
		configureByFile();
		PsiElement foundByAction = GotoTypeDeclarationAction.findSymbolType(myFixture.getEditor(), myFixture.getEditor().getCaretModel().getOffset());
		assertEquals(barClass, foundByAction);
	}

	public void testTodo() throws Throwable
	{
		configureByFile();
		final consulo.language.psi.search.IndexPattern indexPattern = new IndexPattern("TODO", true);
		assertEquals(2, IndexPatternSearch.search(myFixture.getFile(), indexPattern).toArray(new IndexPatternOccurrence[0]).length);
	}

	private void doTest(final String actionId) throws Throwable
	{
		configureByFile();
		CodeInsightAction action = (CodeInsightAction) ActionManager.getInstance().getAction(actionId);
		action.actionPerformedImpl(myModule.getProject(), myFixture.getEditor());
		myFixture.checkResultByFile(Util.getExpectedResultFileName(getTestName(true)));
	}

	private void configureByFile() throws Throwable
	{
		myFixture.configureByFile(Util.getInputDataFileName(getTestName(true)));
	}
}