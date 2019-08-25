package com.github.kunicmarko20.idea.lendahelper.service;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MemberChooser {
    @Nullable
    public static PhpNamedElementNode[] choose(PhpNamedElementNode[] members, Project project) {
        PhpNamedElementNode[] nodes = (PhpNamedElementNode[]) fixOrderToBeAsOriginalFiles(members).toArray(new PhpNamedElementNode[members.length]);
        com.intellij.ide.util.MemberChooser<PhpNamedElementNode> chooser = new com.intellij.ide.util.MemberChooser<PhpNamedElementNode>(nodes, false, true, project);
        chooser.setTitle("Choose Fields");
        chooser.setCopyJavadocVisible(false);
        chooser.show();
        List<PhpNamedElementNode> list = chooser.getSelectedElements();
        return list == null ? null : (PhpNamedElementNode[]) list.toArray(new PhpNamedElementNode[0]);
    }

    private static Collection<PhpNamedElementNode> fixOrderToBeAsOriginalFiles(PhpNamedElementNode[] selected) {
        List<PhpNamedElementNode> newSelected = ContainerUtil.newArrayList(selected);
        Collections.sort(newSelected, (o1, o2) -> {
            PsiElement psiElement = o1.getPsiElement();
            PsiElement psiElement2 = o2.getPsiElement();
            PsiFile containingFile = psiElement.getContainingFile();
            PsiFile containingFile2 = psiElement2.getContainingFile();
            return containingFile == containingFile2 ? psiElement.getTextOffset() - psiElement2.getTextOffset() : containingFile.getName().compareTo(containingFile2.getName());
        });
        return newSelected;
    }
}