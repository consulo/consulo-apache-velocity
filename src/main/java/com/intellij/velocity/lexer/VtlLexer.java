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

package com.intellij.velocity.lexer;

import static com.intellij.velocity.psi.VtlElementTypes.MULTILINE_COMMENT;
import static com.intellij.velocity.psi.VtlElementTypes.TEMPLATE_TEXT;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;

/**
 * @author Alexey Chmutov
 */
public class VtlLexer extends MergingLexerAdapter {
  private static final TokenSet TOKENS_TO_MERGE = TokenSet.create(MULTILINE_COMMENT, TEMPLATE_TEXT);

  public VtlLexer() {
    super(new FlexAdapter(new _VtlLexer()), TOKENS_TO_MERGE);
  }
}
