package com.github.kunicmarko20.idea.helpable.actions.generation.handlers;

import com.intellij.codeInsight.hint.HintManager;
import org.jetbrains.annotations.NotNull;

public class EqualsHandler extends ActionHandler {
    final private String TEMPLATE = "public function equals(%TYPE% $other):bool{return %BODY%;}";
    final private String EXPRESSION = "$this->%PROPERTY% === $other->%PROPERTY%";

    @Override
    @NotNull
    protected String body() {
        StringBuilder body = new StringBuilder();

        int propertiesLength = this.classProperties.length;
        for (int i = 0; i < propertiesLength; i++) {
            if (i > 0) {
                body.append("&& ");
            }

            body.append(EXPRESSION.replace(
                "%PROPERTY%",
                this.classProperties[i].getText()
            ));
        }

        return this.TEMPLATE
                .replace("%TYPE%", this.phpClass.getName())
                .replace("%BODY%", body);
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
