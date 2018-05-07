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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Alexey Chmutov
 */
class VtlFileProxy {
    private final VtlFile myFile;
    private final VtlFile myScopeFile;

    public VtlFileProxy(@Nonnull VtlFile file, @Nullable VtlFile scopeFile) {
        myFile = file;
        myScopeFile = scopeFile;
    }

    @Nonnull
    public VtlFile getFile() {
        return myFile;
    }

    public boolean isVisibleIn(@Nullable VtlFile placeFile) {
        return placeFile == null || myScopeFile == null || placeFile.isEquivalentTo(myScopeFile);
    }
}
