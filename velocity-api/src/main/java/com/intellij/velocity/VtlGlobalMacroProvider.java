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

import java.util.Collection;

import javax.annotation.Nonnull;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.velocity.psi.VtlMacro;
import consulo.velocity.api.psi.VelocityFile;

/**
 * @author Alexey Chmutov
 */
public abstract class VtlGlobalMacroProvider {
    public static final ExtensionPointName<VtlGlobalMacroProvider> EP_NAME = ExtensionPointName.create("com.intellij.velocity.globalMacroProvider");

    @Nonnull
    public abstract Collection<VtlMacro> getGlobalMacros(@Nonnull VelocityFile file);

}