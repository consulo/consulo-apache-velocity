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

import consulo.language.ast.IElementType;
import com.intellij.velocity.psi.VtlElementTypes;
import com.intellij.velocity.psi.VtlLanguage;
import consulo.language.impl.psi.template.TemplateDataElementType;

/**
 * @author Alexey Chmutov
 */
public class VtlFileElementTypes {
    private static final consulo.language.ast.IElementType OUTER_ELEMENT_TYPE = new IElementType("VTL_FRAGMENT", VtlLanguage.INSTANCE);
    public static final consulo.language.impl.psi.template.TemplateDataElementType TEMPLATE_DATA =
            new TemplateDataElementType("VTL_TEMPLATE_DATA", VtlLanguage.INSTANCE, VtlElementTypes.TEMPLATE_TEXT, OUTER_ELEMENT_TYPE);

    private VtlFileElementTypes() {
    }
}
