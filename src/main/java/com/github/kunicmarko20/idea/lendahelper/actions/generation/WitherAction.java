package com.github.kunicmarko20.idea.lendahelper.actions.generation;

import com.github.kunicmarko20.idea.lendahelper.actions.generation.handlers.WitherHandler;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.CodeInsightAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class WitherAction extends CodeInsightAction {
    private final WitherHandler handler = new WitherHandler();

    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return this.handler.isValidFor(editor, file);
    }

    @NotNull
    @Override
    protected CodeInsightActionHandler getHandler() {
        return this.handler;
    }
}
