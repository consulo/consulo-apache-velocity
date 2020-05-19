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

package com.intellij.velocity.psi.reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.beanProperties.BeanProperty;
import com.intellij.util.IncorrectOperationException;

/**
 * @author Alexey Chmutov
 */
public class VelocityStyleBeanProperty extends BeanProperty {
    private String myName;

    protected VelocityStyleBeanProperty(@Nonnull PsiMethod method, @Nonnull String name) {
        super(method);
        myName = name;
    }

    @Nonnull
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public PsiMethod getGetter() {
        final PsiMethod method = getMethod();
        if (VelocityNamingUtil.isPropertyGetter(method)) {
            return method;
        }
        return VelocityNamingUtil.findPropertyGetter(method.getContainingClass(), getName());
    }

    @Override
    public PsiMethod getSetter() {
        final PsiMethod method = getMethod();
        if (VelocityNamingUtil.isPropertySetter(method)) {
            return method;
        }
        return VelocityNamingUtil.findPropertySetter(method.getContainingClass(), getName());
    }

    @Override
    public void setName(String newName) throws IncorrectOperationException {
        super.setName(newName);
        myName = newName;
    }

  @Override
  @Nonnull
  public PsiType getPropertyType() {
    PsiType type = VelocityNamingUtil.getPropertyType(getMethod());
    assert type != null;
    return type;
  }

    @Nullable
    public static BeanProperty createVelocityStyleBeanProperty(@Nonnull PsiMethod method, @Nullable String name) {
        if(name == null) {
            return null;
        }
        return VelocityNamingUtil.isPropertyAccessor(method) || VelocityNamingUtil.isGetByStringOrByObjectMethod(method)
                ? new VelocityStyleBeanProperty(method, name)
                : null;
    }


}
