package com.github.kunicmarko20.idea.lendahelper.actions.generation;

import com.github.kunicmarko20.idea.lendahelper.actions.generation.handlers.ToStringHandler;
import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.CodeInsightAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class ToStringAction extends CodeInsightAction {
    private final ToStringHandler handler = new ToStringHandler();

    protected boolean isValidForFile(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        return this.handler.isValidFor(editor, file);
    }

    @NotNull
    @Override
    protected CodeInsightActionHandler getHandler() {
        return this.handler;
    }
}
