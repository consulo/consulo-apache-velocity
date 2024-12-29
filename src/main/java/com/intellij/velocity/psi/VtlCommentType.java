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

import org.jetbrains.annotations.NonNls;
import jakarta.annotation.Nonnull;

/**
 * @author Alexey Chmutov
 */
public class VtlCommentType extends VtlTokenType {
    private final int myStartDelta;
    private final int myEndDelta;
    public VtlCommentType(@Nonnull @NonNls final String debugName, int startDelta, int endDelta) {
        super(debugName);
        myStartDelta = startDelta;
        myEndDelta = endDelta;
    }

    public int getStartDelta() {
        return myStartDelta;
    }

    public int getEndDelta() {
        return myEndDelta;
    }
}
