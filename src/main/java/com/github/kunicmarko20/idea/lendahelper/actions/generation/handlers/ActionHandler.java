package com.github.kunicmarko20.idea.lendahelper.actions.generation.handlers;

import com.github.kunicmarko20.idea.lendahelper.service.ClassPropertyFinder;
import com.github.kunicmarko20.idea.lendahelper.service.EditorPositionFinder;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.PhpCodeEditUtil;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

abstract class ActionHandler implements LanguageCodeInsightActionHandler {
    protected PhpFile file;
    protected PhpClass phpClass;
    protected PhpNamedElementNode[] classProperties;
    protected Editor editor;
    protected Project project;

    @Override
    public boolean isValidFor(Editor editor, PsiFile file) {
        if (!(file instanceof PhpFile)) {
            return false;
        }

        PhpClass phpClass = PhpCodeEditUtil.findClassAtCaret(editor, file);

        return phpClass != null && !phpClass.isInterface();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        this.project = project;
        this.editor = editor;
        this.file = (PhpFile) file;
        this.phpClass = PhpCodeEditUtil.findClassAtCaret(editor, this.file);
        this.classProperties = ClassPropertyFinder.find(this.phpClass);

        if (!this.isValid()) {
            return;
        }

        String body = this.body();

        int insertPosition = EditorPositionFinder.suitablePosition(editor, this.file);
        int endPosition = insertPosition + body.length();

        editor.getDocument().insertString(insertPosition, body);
        CodeStyleManager.getInstance(project).reformatText(this.file, insertPosition, endPosition);
    }

    @NotNull
    protected abstract String body();

    @NotNull
    protected abstract boolean isValid();
}