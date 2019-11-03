package com.github.kunicmarko20.idea.helpable.actions.generation.handlers;

import com.intellij.codeInsight.hint.HintManager;
import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class EqualsHandler extends ActionHandler {
    @Override
    @NotNull
    protected String body() {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/equals.twig.php");
        JtwigModel model = JtwigModel.newModel()
                .with("type", this.phpClass.getName())
                .with("properties", this.classProperties);

        return template.render(model);
    }

    @Override
    @NotNull
    protected boolean isValid() {
        if (this.classProperties.length < 1) {
            HintManager.getInstance().showErrorHint(this.editor, "At least one property is needed to generate Equals method.");
            return false;
        }

        if (this.methodExists("equals")) {
            HintManager.getInstance().showErrorHint(this.editor, "Equals method already exists.");
            return false;
        }

        return true;
    }
}
