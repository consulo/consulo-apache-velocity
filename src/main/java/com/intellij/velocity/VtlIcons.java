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

import consulo.annotation.DeprecationInfo;
import consulo.apache.velocity.icon.VelocityIconGroup;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 22.05.2008
 */
@Deprecated
@DeprecationInfo("Use VelocityIconGroup")
public interface VtlIcons {
    Image SHARP_ICON = VelocityIconGroup.sharp();

    Image VTL_ICON = VelocityIconGroup.velocity();
}
