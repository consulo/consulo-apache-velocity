/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.velocity.inspections;

import java.io.IOException;
import java.util.Collection;

import javax.swing.Icon;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.template.Template;
import com.intellij.codeInsight.template.TemplateManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.VtlFileIndex;
import com.intellij.velocity.VtlIcons;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.files.VtlFile;
import com.intellij.velocity.psi.files.VtlFileViewProvider;
import com.intellij.velocity.psi.reference.VtlReferenceExpression;
import consulo.roots.ContentFolderScopes;

/**
 * @author Alexey Chmutov
 */
public abstract class DefineInCommentIntention implements IntentionAction
{
	private final String myText;
	private final String myFamilyName;
	public static final String VELOCITY_IMPLICIT_VM = "velocity_implicit.vm";

	public DefineInCommentIntention(@Nonnull String text, @Nonnull String familyName)
	{
		myText = text;
		myFamilyName = familyName;
	}

	@Override
	@Nonnull
	public String getText()
	{
		return myText;
	}

	@Override
	@Nonnull
	public String getFamilyName()
	{
		return myFamilyName;
	}

	@Override
	public final boolean isAvailable(@Nonnull final Project project, final Editor editor, final PsiFile file)
	{
		return file.getViewProvider() instanceof VtlFileViewProvider &&
				getReferenceElement(editor, file) != null &&
				ModuleUtil.findModuleForPsiElement(file) != null;
	}

	@Nullable
	protected PsiElement getReferenceElement(@Nonnull final Editor editor, @Nonnull final PsiFile file)
	{
		final VtlReferenceExpression ref = Util.findReferenceExpression(editor, file);
		return ref != null && ref.multiResolve(false).length == 0 && isAvailable(ref) ? ref : null;
	}

	protected boolean isAvailable(@Nonnull VtlReferenceExpression ref)
	{
		return true;
	}

	protected void defineInComment(
			final Editor editor,
			final PsiFile fileWithVarReference,
			final PsiFile fileToInsertComment,
			final boolean addFileReference)
	{
		final PsiElement ref = getReferenceElement(editor, fileWithVarReference);
		assert ref != null;
		final Project project = fileWithVarReference.getProject();
		if(!FileModificationService.getInstance().prepareFileForWrite(fileToInsertComment))
		{
			return;
		}

		final PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
		final Document documentToInsertComment = documentManager.getDocument(fileToInsertComment);
		assert documentToInsertComment != null;
		new WriteCommandAction(project)
		{
			@Override
			protected void run(Result result) throws Throwable
			{
				Editor editor = FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project,
						fileToInsertComment.getViewProvider().getVirtualFile(), 0), true);
				assert editor != null;
				assert documentToInsertComment == editor.getDocument();
				int insertionIndex = documentToInsertComment.getText().startsWith(VtlFileIndex.IMPLICIT_INCLUDE_MARKER) ? VtlFileIndex
						.IMPLICIT_INCLUDE_MARKER.length() : 0;
				editor.getCaretModel().moveToOffset(insertionIndex);
				TemplateManager manager = TemplateManager.getInstance(project);
				final Template template = manager.createTemplate("", "");
				final String relativePath = addFileReference ? PsiUtil.getRelativePath(fileToInsertComment, fileWithVarReference) : null;
				prepareTemplate(template, ref, relativePath, fileToInsertComment);
				manager.startTemplate(editor, template);
			}
		}.execute();
	}

	protected abstract void prepareTemplate(
			@Nonnull Template template,
			@Nonnull PsiElement element,
			@Nullable String relativePath,
			@Nonnull PsiFile fileToInsertComment);

	protected void chooseTargetFile(final PsiFile file, final Editor editor, final boolean addFileReference)
	{
		final Collection<VtlFile> implicitlyIncludedFiles = VtlFileIndex.getImplicitlyIncludedFiles(file);
		if(implicitlyIncludedFiles.size() == 1)
		{
			defineInComment(editor, file, implicitlyIncludedFiles.iterator().next(), addFileReference);
			return;
		}

		if(implicitlyIncludedFiles.size() < 1)
		{
			final VtlFile newTargetFile = new WriteCommandAction<VtlFile>(file.getProject())
			{
				@Override
				protected void run(Result<VtlFile> result) throws Throwable
				{
					final VirtualFile virtualFile = createVelocityImplicitVmFile();
					if(virtualFile == null)
					{
						return;
					}
					VfsUtil.saveText(virtualFile, VtlFileIndex.IMPLICIT_INCLUDE_MARKER);
					final PsiFile psiFile = file.getManager().findFile(virtualFile);
					if(psiFile instanceof VtlFile)
					{
						result.setResult((VtlFile) psiFile);
					}
				}

				@Nullable
				private VirtualFile createVelocityImplicitVmFile() throws IOException
				{
					final Module module = ModuleUtil.findModuleForPsiElement(file);
					final VirtualFile[] roots = ModuleRootManager.getInstance(module).getContentFolderFiles(ContentFolderScopes.all(false));
					if(roots.length > 0)
					{
						return roots[0].createChildData(this, VELOCITY_IMPLICIT_VM);
					}
					final PsiDirectory psiDirectory = file.getContainingDirectory();
					return psiDirectory == null ? null : psiDirectory.getVirtualFile().createChildData(this, VELOCITY_IMPLICIT_VM);
				}
			}.execute().getResultObject();
			if(newTargetFile != null)
			{
				defineInComment(editor, file, newTargetFile, addFileReference);
			}
			return;
		}

		final BaseListPopupStep<VtlFile> step = new BaseListPopupStep<VtlFile>(VelocityBundle.message("choose.external.definitions.file"),
				implicitlyIncludedFiles.toArray(new VtlFile[implicitlyIncludedFiles.size()]))
		{
			@Nonnull
			@Override
			public String getTextFor(final VtlFile value)
			{
				return value.getViewProvider().getVirtualFile().getName();
			}

			@Override
			public PopupStep onChosen(final VtlFile selectedValue, final boolean finalChoice)
			{
				if(finalChoice)
				{
					defineInComment(editor, file, selectedValue, addFileReference);
				}
				return super.onChosen(selectedValue, finalChoice);
			}

			@Override
			public boolean isSpeedSearchEnabled()
			{
				return true;
			}

			@Override
			public Icon getIconFor(final VtlFile aValue)
			{
				return VtlIcons.VTL_ICON;
			}
		};
		JBPopupFactory.getInstance().createListPopup(step).showInBestPositionFor(editor);
	}

	@Override
	public boolean startInWriteAction()
	{
		return true;
	}
}