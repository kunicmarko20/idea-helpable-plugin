package com.github.kunicmarko20.idea.lendahelper.actions.generation.handlers;

import com.github.kunicmarko20.idea.lendahelper.service.PropertyChooser;
import com.github.kunicmarko20.idea.lendahelper.service.PropertyTypeFinder;
import com.intellij.codeInsight.hint.HintManager;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.elements.Field;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;

public class WitherHandler extends ActionHandler {
    final private String TEMPLATE = "public function with%CAPITALIZED_PROPERTY%(%PROPERTY_TYPE% $%PROPERTY%):self{$instance = clone $this; $instance->%PROPERTY% = $%PROPERTY%;return $instance;}";

    @Override
    @NotNull
    protected String body() {
        PhpNamedElementNode[] properties = this.classProperties;

        if (properties.length > 1) {
            properties = PropertyChooser.choose(this.classProperties, this.project);
        }

        StringBuilder body = new StringBuilder();
        for (PhpNamedElementNode property : properties) {
            body.append(
                TEMPLATE.replace(
                    "%CAPITALIZED_PROPERTY%",
                    WordUtils.capitalize(property.getText())
                ).replace(
                    "%PROPERTY%",
                    property.getText()
                ).replace(
                    "%PROPERTY_TYPE%",
                    PropertyTypeFinder.find((Field) property.getPsiElement(), this.project)
                )
            );
        }

        return body.toString();
    }

    @Override
    @NotNull
    protected boolean isValid() {
        if (this.classProperties.length < 1) {
            HintManager.getInstance().showErrorHint(this.editor, "At least one property is needed to generate With* method.");
            return false;
        }

        return true;
    }
}
