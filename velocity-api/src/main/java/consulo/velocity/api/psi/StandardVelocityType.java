/*
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

package consulo.velocity.api.psi;

import javax.annotation.Nonnull;

import consulo.velocity.api.facade.VelocityType;

/**
 * @author VISTALL
 * @since 2020-05-19
 */
public final class StandardVelocityType implements VelocityType
{
	public static final StandardVelocityType INT = new StandardVelocityType("int", true);
	public static final StandardVelocityType DOUBLE = new StandardVelocityType("double", true);
	public static final StandardVelocityType BOOLEAN = new StandardVelocityType("boolean", true);
	public static final StandardVelocityType VOID = new StandardVelocityType("void", true);
	public static final StandardVelocityType STRING = new StandardVelocityType("string", false);
	public static final StandardVelocityType LIST = new StandardVelocityType("List", false);
	public static final StandardVelocityType MAP = new StandardVelocityType("Map", false);

	private final String myTypeText;
	private final boolean myPrimitive;

	private StandardVelocityType(String typeText, boolean primitive)
	{
		myTypeText = typeText;
		myPrimitive = primitive;
	}

	public boolean isPrimitive()
	{
		return myPrimitive;
	}

	@Nonnull
	@Override
	public String getPresentableText()
	{
		return myTypeText;
	}
}
