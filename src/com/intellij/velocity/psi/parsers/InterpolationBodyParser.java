package com.intellij.velocity.psi.parsers;

import static com.intellij.velocity.psi.VtlElementTypes.IDENTIFIER;
import static com.intellij.velocity.psi.VtlElementTypes.INTERPOLATION;
import static com.intellij.velocity.psi.VtlElementTypes.JAVA_DOT;
import static com.intellij.velocity.psi.VtlElementTypes.LEFT_PAREN;
import static com.intellij.velocity.psi.VtlElementTypes.METHOD_CALL_EXPRESSION;
import static com.intellij.velocity.psi.VtlElementTypes.REFERENCE_EXPRESSION;

import com.intellij.lang.PsiBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey Chmutov
 * Date: 27.03.2008
 */
public class InterpolationBodyParser extends CompositeBodyParser {

    public static final InterpolationBodyParser INSTANCE = new InterpolationBodyParser();

    private InterpolationBodyParser() {}

    public void parseBody(PsiBuilder builder, PsiBuilder.Marker bodyMarker) {
        parseBodyInternal(builder);
        if(bodyMarker != null) {
            bodyMarker.done(INTERPOLATION);
        }
    }

    static void parseBodyInternal(PsiBuilder builder) {
        PsiBuilder.Marker referenceExpression = builder.mark();
        if(assertToken(builder, IDENTIFIER, REFERENCE_EXPRESSION)) {
          while(builder.getTokenType() == JAVA_DOT) {
              builder.advanceLexer();
              if(!assertToken(builder, IDENTIFIER)) {
                break;
              }
              referenceExpression.done(REFERENCE_EXPRESSION);
              if(builder.getTokenType() == LEFT_PAREN) {
                  referenceExpression = referenceExpression.precede();
                  parseArgumentList(builder, true);
                  referenceExpression.done(METHOD_CALL_EXPRESSION);
              }
              referenceExpression = referenceExpression.precede();
          }
        }
        referenceExpression.drop();
    }

}