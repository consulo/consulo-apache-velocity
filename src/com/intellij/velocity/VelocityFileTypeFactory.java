/*
 * Copyright (c) 2000-2005 by JetBrains s.r.o. All Rights Reserved.
 * Use is subject to license terms.
 */
package com.intellij.velocity;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.velocity.psi.files.VtlFileType;

/**
 * @author Alexey Chmutov
 */
public class VelocityFileTypeFactory extends FileTypeFactory
{
	@Override
	public void createFileTypes(final @NotNull FileTypeConsumer consumer)
	{
		for(String exp : VtlFileType.INSTANCE.getExtensions())
		{
			consumer.consume(VtlFileType.INSTANCE, exp);
		}
	}
}
