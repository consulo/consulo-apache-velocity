package com.intellij.velocity.psi.directives;

import static com.intellij.velocity.psi.VtlCompositeElementTypes.LOOP_VARIABLE;

import java.util.Collection;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.ide.IconDescriptorUpdaters;
import com.intellij.lang.ASTNode;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.RenameableFakePsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.IncorrectOperationException;
import com.intellij.velocity.VelocityBundle;
import com.intellij.velocity.psi.PsiUtil;
import com.intellij.velocity.psi.VtlDirectiveHeader;
import com.intellij.velocity.psi.VtlVariable;

/**
 * @author : Alexey Chmutov
 */
public class VtlForeach extends VtlDirectiveImpl {
    private FixedNameReferenceElement velocityCountElement = null;
    private FixedNameReferenceElement velocityHasNextElement = null;

    public VtlForeach(ASTNode node) {
        super(node, "foreach", true);
    }

    public boolean processDeclarations(@NotNull final PsiScopeProcessor processor, @NotNull final ResolveState state, final PsiElement lastParent,
                                       @NotNull final PsiElement place) {
        final VtlDirectiveHeader header = findHeaderOfDirective();
        PsiElement ancestorOfPlace = place.getParent();
        while(ancestorOfPlace != null && ancestorOfPlace != this) {
            if(ancestorOfPlace == header) {
                return true;
            }
            ancestorOfPlace = ancestorOfPlace.getParent();
        }
        if (lastParent != getVelocityCountElement() && !processor.execute(getVelocityCountElement(), state)) {
            return false;
        }
        if (lastParent != getVelocityHasNextElement() && !processor.execute(getVelocityHasNextElement(), state)) {
            return false;
        }
        PsiElement loopVariable = header.findChildByType(LOOP_VARIABLE);
        if (loopVariable != null && lastParent != loopVariable && !processor.execute(loopVariable, state)) {
            return false;
        }
        return super.processDeclarations(processor, state, lastParent, place);
    }

    private FixedNameReferenceElement getVelocityCountElement() {
        if(velocityCountElement == null) {
            velocityCountElement = new FixedNameReferenceElement("velocityCount", CommonClassNames.JAVA_LANG_INTEGER);
        }
        return velocityCountElement;
    }

    private FixedNameReferenceElement getVelocityHasNextElement() {
        if(velocityHasNextElement == null) {
            velocityHasNextElement = new FixedNameReferenceElement("velocityHasNext", CommonClassNames.JAVA_LANG_BOOLEAN);
        }
        return velocityHasNextElement;
    }

    public class FixedNameReferenceElement extends RenameableFakePsiElement implements VtlVariable {
        @NonNls
        private final String myName;
        private final String myTypeName;

        private FixedNameReferenceElement(@NotNull String name, @NotNull String typeName) {
            super(VtlForeach.this.getContainingFile());
            myName = name;
            myTypeName = typeName;
        }

        public PsiElement getParent() {
            return VtlForeach.this;
        }

        @NotNull
        @Override
        public PsiElement getNavigationElement() {
            return VtlForeach.this;
        }

        public Icon getIcon() {
            return IconDescriptorUpdaters.getIcon(this, 0);
        }

        @NotNull
        @Override
        public String getName() {
            return myName;
        }

        public String getTypeName() {
            return PsiUtil.getUnqualifiedName(myTypeName);
        }

        @Override
        public PsiElement setName(@NotNull @NonNls String s) throws IncorrectOperationException {
            throw new IncorrectOperationException(VelocityBundle.message("operation.not.allowed"));
        }

        public Collection<PsiReference> findReferences(final PsiElement element) {
            return ReferencesSearch.search(element).findAll();
        }

        @Nullable
        public PsiType getPsiType() {
            return JavaPsiFacade.getInstance(getProject()).getElementFactory().createTypeByFQClassName(myTypeName, getResolveScope());
        }
    }
}