package com.github.kunicmarko20.idea.helpable.actions.generation.handlers;

import com.github.kunicmarko20.idea.helpable.service.PropertyChooser;
import com.github.kunicmarko20.idea.helpable.service.PropertyTypeFinder;
import com.intellij.codeInsight.hint.HintManager;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.elements.Field;
import org.apache.commons.lang.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public class ToHandler extends ActionHandler {
    @Override
    protected String body() {
        PhpNamedElementNode[] properties = this.classProperties;

        if (properties.length > 1) {
            properties = PropertyChooser.choose(properties, this.project, false);
        }

        if (properties == null) {
            return null;
        }

        String propertyType = PropertyTypeFinder.find((Field) properties[0].getPsiElement(), this.project);
        String normalizedTypeName = WordUtils.capitalize(this.normalizeTypeName(propertyType));

        if (this.methodExists("to" + normalizedTypeName)) {
            HintManager.getInstance().showErrorHint(this.editor, "Method already exists.");
            return "";
        }

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/to.twig.php");
        JtwigModel model = JtwigModel.newModel()
                .with("property", properties[0].getText())
                .with("type", propertyType)
                .with("type_method_name", normalizedTypeName);

        return template.render(model);
    }

    private String normalizeTypeName(String type) {
        type = type.replace("?", "");

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
            HintManager.getInstance().showErrorHint(this.editor, "At least one property is needed to generate To* method.");
            return false;
        }

        return true;
    }
}
