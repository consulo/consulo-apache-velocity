package com.intellij.velocity.psi.parsers;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.VtlCompositeElementType;
import com.intellij.velocity.psi.VtlCompositeStarterTokenType;
import static com.intellij.velocity.psi.parsers.VtlParser.*;
import static com.intellij.velocity.psi.VtlElementTypes.*;
import com.intellij.openapi.util.Key;


/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.03.2008
 */
public abstract class CompositeBodyParser {

    public abstract void parseBody(PsiBuilder builder, PsiBuilder.Marker bodyMarker);

    protected static void finishCompositeWithEnd(PsiBuilder builder, PsiBuilder.Marker bodyMarker, VtlCompositeElementType bodyType) {
        if (builder.getTokenType() != SHARP_END) {
            bodyMarker.error(VelocityBundle.message("token.expected", "#end"));
        } else {
            builder.advanceLexer();
            bodyMarker.done(bodyType);
        }
    }

    protected static boolean assertToken(PsiBuilder builder, IElementType expected) {
        if (builder.getTokenType() != expected) {
            builder.error(VelocityBundle.message("token.expected", expected));
            return false;
        } else {
            builder.advanceLexer();
            return true;
        }
    }

    protected static boolean assertToken(PsiBuilder builder, IElementType expected, VtlCompositeElementType compositeElementType) {
        if (builder.getTokenType() != expected) {
            builder.error(VelocityBundle.message("token.expected", expected));
            return false;
        } else {
            PsiBuilder.Marker start = builder.mark();
            builder.advanceLexer();
            start.done(compositeElementType);
            return true;
        }
    }

    protected static boolean consumeTokenIfPresent(PsiBuilder builder, IElementType tokenType) {
        if (builder.getTokenType() == tokenType) {
            builder.advanceLexer();
            return true;
        }
        return false;
    }

    protected static boolean parseArgumentList(PsiBuilder builder, boolean requireSeparator) {
        if (!assertToken(builder, LEFT_PAREN)) {
            builder.error(VelocityBundle.message("argument.list.expected"));
            return false;
        }
        PsiBuilder.Marker listMarker = builder.mark();
        if (builder.getTokenType() != RIGHT_PAREN) {
            parseList(builder, ListHandler.GENERAL_LIST_HANDLER, requireSeparator);
        }
        listMarker.done(ARGUMENT_LIST);
        assertToken(builder, RIGHT_PAREN);
        return true;
    }

    protected static boolean parseConditionalExpression(PsiBuilder builder) {
        if (!assertToken(builder, LEFT_PAREN)) {
            return false;
        }
        parseBinaryExpression(builder);
        assertToken(builder, RIGHT_PAREN);
        return true;
    }

  static void assertVariable(PsiBuilder builder, VtlCompositeElementType elementType, String errorMsg) {
        final IElementType variableStarter = builder.getTokenType();
        builder.advanceLexer();
        PsiBuilder.Marker variable = builder.mark();
        if (variableStarter != START_REFERENCE && variableStarter != START_REF_FORMAL) {
            variable.drop();
            builder.error(errorMsg);
            return;
        }
        boolean identFound = assertToken(builder, IDENTIFIER);
        if (variableStarter == START_REF_FORMAL) {
            assertToken(builder, RIGHT_BRACE);
        }
        if (identFound) {
            variable.done(elementType);
        } else {
            variable.drop();
        }
    }

    static class CompositeEndDetector {
        public boolean isCompositeFinished(PsiBuilder builder) {
            return builder.getTokenType() == SHARP_END;
        }

        public boolean isTokenInvalid(IElementType tokenType) {
            if (tokenType == TEMPLATE_TEXT || tokenType == SHARP_STOP) {
                return false;
            }
            if (tokenType == SHARP_ELSE || tokenType == SHARP_ELSEIF) {
                return true;
            }
            return !(tokenType instanceof VtlCompositeStarterTokenType);
        }
    }

    static final CompositeEndDetector COMMON_END_DETECTOR = new CompositeEndDetector();

    private static final Key<Integer> FOREACH_COUNTER = Key.create("FOREACH_COUNTER");

    static boolean noForeachStarted(PsiBuilder builder) {
        return builder.getUserData(FOREACH_COUNTER) == null;
    }

    static void incrementForeachCounter(PsiBuilder builder) {
        Integer counter = builder.getUserData(FOREACH_COUNTER);
        if(counter == null) counter = 0;
        builder.putUserData(FOREACH_COUNTER, counter + 1);
    }

    static void decrementForeachCounter(PsiBuilder builder) {
        Integer counter = builder.getUserData(FOREACH_COUNTER);
        assert counter != null;
        counter = counter > 1 ? counter - 1 : null;
        builder.putUserData(FOREACH_COUNTER, counter);
    }
}
