package com.github.kunicmarko20.idea.lendahelper.actions.generation.handlers;

import com.github.kunicmarko20.idea.lendahelper.service.PropertyChooser;
import com.github.kunicmarko20.idea.lendahelper.service.PropertyTypeFinder;
import com.intellij.codeInsight.hint.HintManager;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.elements.Field;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;

public class ToHandler extends ActionHandler {
    final private String TEMPLATE = "public function to%TYPE_METHOD_NAME%():%TYPE%{return $this->%PROPERTY%;}";

    @Override
    @NotNull
    protected String body() {
        PhpNamedElementNode[] properties = this.classProperties;

        if (properties.length > 1) {
            properties = PropertyChooser.choose(properties, this.project, false);
        }

        String propertyType = PropertyTypeFinder.find((Field) properties[0].getPsiElement(), this.project);

        return this.TEMPLATE
                .replace("%PROPERTY%", properties[0].getText())
                .replace("%TYPE%", propertyType)
                .replace("%TYPE_METHOD_NAME%", WordUtils.capitalize(this.normalizeTypeName(propertyType)));
    }

    private String normalizeTypeName(String type) {
        switch (type) {
            case "bool":
                return "Boolean";
            case "int":
                return "Integer";
            default:
                return type;
        }
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
