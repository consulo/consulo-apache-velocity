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

package com.intellij.velocity.psi;

import com.intellij.velocity.psi.parsers.CompositeBodyParser;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.03.2008
 */
public class VtlCompositeStarterTokenType extends VtlTokenType {

    private final CompositeBodyParser myBodyParser;

    public VtlCompositeStarterTokenType(@NotNull @NonNls final String debugName, @NotNull CompositeBodyParser bodyParser) {
        super(debugName);
        this.myBodyParser = bodyParser;
    }

    @NotNull
    public CompositeBodyParser getCompositeBodyParser() {
        return myBodyParser;
    }
}
