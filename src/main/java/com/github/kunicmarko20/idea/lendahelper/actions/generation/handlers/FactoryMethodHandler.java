package com.github.kunicmarko20.idea.lendahelper.actions.generation.handlers;

import com.github.kunicmarko20.idea.lendahelper.service.PropertyTypeFinder;
import com.intellij.codeInsight.hint.HintManager;
import com.jetbrains.php.lang.psi.elements.Field;
import org.jetbrains.annotations.NotNull;

public class FactoryMethodHandler extends ActionHandler {
    final private String TEMPLATE = "public static function with(%PARAMETERS%):self{$instance = new self(); %BODY% return $instance;}";
    final private String EXPRESSION_SET_VALUE = "$instance->%PROPERTY% = $%PROPERTY%;";
    final private String EXPRESSION_PARAMETER = "%TYPE% $%NAME%";

    @Override
    @NotNull
    protected String body() {
        StringBuilder body = new StringBuilder();
        StringBuilder parameters = new StringBuilder();

        int propertiesLength = this.classProperties.length;
        for (int i = 0; i < propertiesLength; i++) {
            if (i > 0) {
                parameters.append(", ");
            }

            String propertyType = PropertyTypeFinder.find((Field) this.classProperties[i].getPsiElement(), this.project);

            parameters.append(
                EXPRESSION_PARAMETER.replace(
                    "%TYPE%",
                    propertyType
                ).replace(
                    "%NAME%",
                    this.classProperties[i].getText()
                )
            );

            body.append(
                EXPRESSION_SET_VALUE.replace(
                "%PROPERTY%",
                    this.classProperties[i].getText()
                )
            );
        }

        return TEMPLATE.replace(
            "%PARAMETERS%",
            parameters
        ).replace(
            "%BODY%",
            body
        );
    }

    @Override
    @NotNull
    protected boolean isValid() {
        if (this.classProperties.length < 1) {
            HintManager.getInstance().showErrorHint(this.editor, "At least one property is needed to generate Factory method.");
            return false;
        }

        return true;
    }
}
