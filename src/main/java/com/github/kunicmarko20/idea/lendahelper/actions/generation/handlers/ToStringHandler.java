package com.github.kunicmarko20.idea.lendahelper.actions.generation.handlers;

import com.github.kunicmarko20.idea.lendahelper.service.PropertyChooser;
import com.intellij.codeInsight.hint.HintManager;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import org.jetbrains.annotations.NotNull;

public class ToStringHandler extends ActionHandler {
    final private String TEMPLATE = "public function toString():string{return $this->%PROPERTY%;}";

    @Override
    @NotNull
    protected String body() {
        PhpNamedElementNode[] properties = this.classProperties;

        if (properties.length > 1) {
            properties = PropertyChooser.choose(properties, this.project, false);
        }

        return this.TEMPLATE
                .replace("%PROPERTY%", properties[0].getText());
    }

    @Override
    @NotNull
    protected boolean isValid() {
        if (this.classProperties.length < 1) {
            HintManager.getInstance().showErrorHint(this.editor, "At least one property is needed to generate ToString method.");
            return false;
        }

        return true;
    }
}
