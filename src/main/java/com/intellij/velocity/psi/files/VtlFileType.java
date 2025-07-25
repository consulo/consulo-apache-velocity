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
package com.intellij.velocity.psi.files;

import com.intellij.velocity.VtlIcons;
import com.intellij.velocity.psi.VtlLanguage;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.language.file.LanguageFileType;
import consulo.language.template.TemplateLanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
public class VtlFileType extends LanguageFileType implements TemplateLanguageFileType {
    public static final VtlFileType INSTANCE = new VtlFileType();

    private VtlFileType() {
        super(VtlLanguage.INSTANCE);
    }

    @Override
    @Nonnull
    public String getId() {
        return "VTL";
    }

    @Nonnull
    @Override
    public LocalizeValue getDisplayName() {
        return VelocityLocalize.vtlFileTypeDisplayName();
    }

    @Override
    @Nonnull
    public LocalizeValue getDescription() {
        return VelocityLocalize.fileTypeDescription();
    }

    @Override
    @Nonnull
    public String getDefaultExtension() {
        return "vm";
    }

    @Override
    @Nonnull
    public Image getIcon() {
        return VtlIcons.VTL_ICON;
    }

    @Nonnull
    public String[] getExtensions() {
        return new String[]{
            getDefaultExtension(),
            "ft",
            "vsl"
        };
    }
}
