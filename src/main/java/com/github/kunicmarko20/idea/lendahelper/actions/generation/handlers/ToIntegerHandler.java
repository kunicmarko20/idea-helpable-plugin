package com.github.kunicmarko20.idea.lendahelper.actions.generation.handlers;

import com.github.kunicmarko20.idea.lendahelper.service.MemberChooser;
import com.intellij.codeInsight.hint.HintManager;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import org.jetbrains.annotations.NotNull;

public class ToIntegerHandler extends ActionHandler {
    final private String TEMPLATE = "public function toInteger():int{return $this->%PROPERTY%;}";

    @Override
    @NotNull
    protected String body() {
        PhpNamedElementNode[] properties = this.classProperties;

        if (properties.length > 1) {
            properties = MemberChooser.choose(properties, this.project, false);
        }

        return this.TEMPLATE
                .replace("%PROPERTY%", properties[0].getText());
    }

    @Override
    @NotNull
    protected boolean isValid() {
        if (this.classProperties.length < 1) {
            HintManager.getInstance().showErrorHint(this.editor, "At least one property is needed to generate ToInteger method.");
            return false;
        }

        return true;
    }
}
