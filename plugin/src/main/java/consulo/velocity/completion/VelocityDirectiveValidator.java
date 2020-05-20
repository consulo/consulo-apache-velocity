/*
 * Copyright 2000-2009 JetBrains s.r.o.
 * Copyright 2013-2020 consulo.io
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

package consulo.velocity.completion;

import com.intellij.psi.PsiElement;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlElementTypes;
import com.intellij.velocity.psi.directives.VtlDirective;
import consulo.velocity.api.psi.VelocityFile;

public class VelocityDirectiveValidator
{
	public static boolean areParenthesesNeeded(final VtlDirective child, final String childName)
	{
		return !("break".equals(childName) || "else".equals(childName) || "end".equals(childName) || "stop".equals(childName));
	}

	public static boolean isAllowed(final VtlDirective child, final String childName)
	{
		PsiElement parent = child.getParent();
		if("macro".equals(childName))
		{
			return parent instanceof VelocityFile;
		}
		if("end".equals(childName))
		{
			return PsiUtil.isTypeOf(parent, VtlElementTypes.SHARP_FOREACH, VtlElementTypes.SHARP_IF, VtlElementTypes.SHARP_LITERAL, VtlElementTypes.SHARP_MACRODECL, VtlElementTypes.SHARP_DEFINE)
					|| PsiUtil.isTypeOf(parent.getParent(), VtlElementTypes.SHARP_IF)
					&& PsiUtil.isTypeOf(parent, VtlElementTypes.SHARP_ELSE, VtlElementTypes.SHARP_ELSEIF);
		}
		if("else".equals(childName) || "elseif".equals(childName))
		{
			return PsiUtil.isTypeOf(parent, VtlElementTypes.SHARP_IF)
					|| PsiUtil.isTypeOf(parent.getParent(), VtlElementTypes.SHARP_IF)
					&& PsiUtil.isTypeOf(parent, VtlElementTypes.SHARP_ELSEIF);
		}
		if("break".equals(childName))
		{
			return PsiUtil.isTypeOf(parent, VtlElementTypes.SHARP_FOREACH);
		}
		return true;
	}
}
