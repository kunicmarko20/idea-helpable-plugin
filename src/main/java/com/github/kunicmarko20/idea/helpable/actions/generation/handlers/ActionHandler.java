package com.github.kunicmarko20.idea.helpable.actions.generation.handlers;

import com.github.kunicmarko20.idea.helpable.service.ClassFieldFinder;
import com.github.kunicmarko20.idea.helpable.service.EditorPositionFinder;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.PhpCodeEditUtil;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

abstract class ActionHandler implements LanguageCodeInsightActionHandler {
    protected PhpFile file;
    protected PhpClass phpClass;
    protected PhpNamedElementNode[] classProperties;
    protected Editor editor;
    protected Project project;
    protected List<String> existingMethods;

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
        this.classProperties = ClassFieldFinder.properties(this.phpClass);
        this.existingMethods();

        if (!this.isValid()) {
            return;
        }

        String body = this.body();

        if (body == null) {
            return;
        }

        int insertPosition = EditorPositionFinder.suitablePosition(editor, this.file);
        int endPosition = insertPosition + body.length();

        ApplicationManager.getApplication().runWriteAction(() -> {
            editor.getDocument().insertString(insertPosition, body);
            CodeStyleManager.getInstance(project).reformatText(this.file, insertPosition, endPosition);
        });
    }

    private void existingMethods() {
        Collection<Method> methods = this.phpClass.getMethods();
        List<String> existingMethods = new ArrayList<>();

        for (Method method : methods) {
            existingMethods.add(method.getName());
        }

        this.existingMethods = existingMethods;
    }

    protected boolean methodExists(String method) {
        return this.existingMethods.contains(method);
    }

    public boolean startInWriteAction() {
        return false;
    }

    protected abstract String body();

    @NotNull
    protected abstract boolean isValid();
}
