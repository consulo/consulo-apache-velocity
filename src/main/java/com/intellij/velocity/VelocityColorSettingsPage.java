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

package com.intellij.velocity;

import com.intellij.velocity.psi.files.VtlSyntaxHighlighter;
import consulo.annotation.component.ExtensionImpl;
import consulo.apache.velocity.localize.VelocityLocalize;
import consulo.colorScheme.setting.AttributesDescriptor;
import consulo.language.editor.colorScheme.setting.ColorSettingsPage;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.localize.LocalizeValue;
import jakarta.annotation.Nonnull;

import static com.intellij.velocity.psi.files.VtlSyntaxHighlighter.*;

/**
 * @author Alexey Chmutov
 */
@ExtensionImpl
public class VelocityColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] ATTRS;

    static {
        ATTRS = new AttributesDescriptor[]{
            new AttributesDescriptor(VelocityLocalize.velocityColorDot(), VELOCITY_DOT),
            new AttributesDescriptor(VelocityLocalize.velocityColorParenths(), VELOCITY_PARENTHS),
            new AttributesDescriptor(VelocityLocalize.velocityColorBrackets(), VELOCITY_BRACKETS),
            new AttributesDescriptor(VelocityLocalize.velocityColorBraces(), VELOCITY_BRACES),
            new AttributesDescriptor(VelocityLocalize.velocityColorOperationSign(), VELOCITY_OPERATION_SIGN),
            new AttributesDescriptor(VelocityLocalize.velocityColorString(), VELOCITY_STRING),
            new AttributesDescriptor(VelocityLocalize.velocityColorEscape(), VELOCITY_ESCAPE),
            new AttributesDescriptor(VelocityLocalize.velocityColorNumber(), VELOCITY_NUMBER),
            new AttributesDescriptor(VelocityLocalize.velocityColorKeyword(), VELOCITY_KEYWORD),
            new AttributesDescriptor(VelocityLocalize.velocityColorComma(), VELOCITY_COMMA),
            new AttributesDescriptor(VelocityLocalize.velocityColorSemicolon(), VELOCITY_SEMICOLON),
            new AttributesDescriptor(VelocityLocalize.velocityColorDirective(), VELOCITY_DIRECTIVE),
            new AttributesDescriptor(VelocityLocalize.velocityColorReference(), VELOCITY_REFERENCE),
            new AttributesDescriptor(VelocityLocalize.velocityColorComment(), VELOCITY_COMMENT),
            new AttributesDescriptor(VelocityLocalize.velocityColorBadCharacter(), VELOCITY_BAD_CHARACTER),
            new AttributesDescriptor(VelocityLocalize.velocityColorScriptingBackground(), VELOCITY_SCRIPTING_BACKGROUND),
        };
    }

    @Override
    @Nonnull
    public LocalizeValue getDisplayName() {
        return VelocityLocalize.vtlLanguageDisplayName();
    }

    @Override
    @Nonnull
    public AttributesDescriptor[] getAttributeDescriptors() {
        return ATTRS;
    }

    @Override
    @Nonnull
    public SyntaxHighlighter getHighlighter() {
        return new VtlSyntaxHighlighter();
    }

    @Override
    @Nonnull
    public String getDemoText() {
        return "#* comment  \n" +
            "  comment *#\n" +
            "$reference.method('string').property\n" +
            "## another comment\n" +
            "#foreach($loopvar in [-4..77])\n" +
            "  #set($var = {1:\"double-quoted ${loopvar.toString()}\", 2:false #fff})\n" +
            "#{end}\n\n" +
            "#{macro}(m1 $p1, $p2 $p3 $)\n" +
            "  #if($p1 * ${p2} eq $p3)\n" +
            "#templatetext\n" +
            "  #{else}template text\n" +
            "  #end\n" +
            "#end\n" +
            "#m1(4 5 7)\n" +
            "#{m1}(8, 7, 6)\n";
    }
}
