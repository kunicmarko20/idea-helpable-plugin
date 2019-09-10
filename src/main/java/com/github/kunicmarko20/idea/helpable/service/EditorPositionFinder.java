package com.github.kunicmarko20.idea.helpable.service;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.parser.PhpElementTypes;
import com.jetbrains.php.lang.parser.PhpStubElementTypes;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;

public class EditorPositionFinder {
    public static int suitablePosition(Editor editor, PhpFile phpFile) {
        PsiElement currElement = phpFile.findElementAt(editor.getCaretModel().getOffset());
        if (currElement != null) {
            PsiElement parent = currElement.getParent();

            for(PsiElement prevParent = currElement; parent != null && !(parent instanceof PhpFile); parent = parent.getParent()) {
                if (isClassMember(parent)) {
                    return getNextPosition(parent);
                }

                if (parent instanceof PhpClass) {
                    while(prevParent != null) {
                        if (isClassMember(prevParent) || PhpPsiUtil.isOfType(prevParent, PhpTokenTypes.chLBRACE)) {
                            return getNextPosition(prevParent);
                        }

                        prevParent = prevParent.getPrevSibling();
                    }

                    for(PsiElement classChild = parent.getFirstChild(); classChild != null; classChild = classChild.getNextSibling()) {
                        if (PhpPsiUtil.isOfType(classChild, PhpTokenTypes.chLBRACE)) {
                            return getNextPosition(classChild);
                        }
                    }
                }

                prevParent = parent;
            }
        }

        return -1;
    }

    private static boolean isClassMember(PsiElement element) {
        if (element == null) {
            return false;
        }

        IElementType elementType = element.getNode().getElementType();

        return elementType == PhpElementTypes.CLASS_FIELDS
                || elementType == PhpElementTypes.CLASS_CONSTANTS
                || elementType == PhpStubElementTypes.CLASS_METHOD;
    }

    private static int getNextPosition(PsiElement element) {
        PsiElement next = element.getNextSibling();

        return next != null ?
                next.getTextOffset()
                : -1;
    }
}
