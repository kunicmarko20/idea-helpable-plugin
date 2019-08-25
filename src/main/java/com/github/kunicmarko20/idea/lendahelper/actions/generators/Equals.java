package com.github.kunicmarko20.idea.lendahelper.actions.generators;

import com.github.kunicmarko20.idea.lendahelper.service.MemberChooser;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.PhpCodeEditUtil;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import com.github.kunicmarko20.idea.lendahelper.service.ClassPropertyFinder;

public class Equals extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getData(PlatformDataKeys.PROJECT);
        final Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile file = event.getData(LangDataKeys.PSI_FILE);

        PhpFile phpFile = (PhpFile)file;
        PhpClass targetClass = PhpCodeEditUtil.findClassAtCaret(editor, phpFile);
        if (targetClass != null) {
            HintManager.getInstance().showErrorHint(editor, "Target Class not found.");
        }

        PhpNamedElementNode[] fieldsToShow = ClassPropertyFinder.find(targetClass);

        if (fieldsToShow.length == 0) {
            HintManager.getInstance().showErrorHint(editor, "No fields to generate equals for.");
            return;
        }

        PhpNamedElementNode[] members = MemberChooser.choose(fieldsToShow, project);
    }
}
