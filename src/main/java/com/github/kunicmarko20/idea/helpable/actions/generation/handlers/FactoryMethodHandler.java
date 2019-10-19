package com.github.kunicmarko20.idea.helpable.actions.generation.handlers;

import com.github.kunicmarko20.idea.helpable.service.PropertyTypeFinder;
import com.intellij.codeInsight.hint.HintManager;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.elements.Field;
import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.HashMap;

public class FactoryMethodHandler extends ActionHandler {
    @Override
    @NotNull
    protected String body() {
        HashMap<String, String> properties = new HashMap<>();

        for (PhpNamedElementNode property: this.classProperties) {
            properties.put(
                property.getText(),
                PropertyTypeFinder.find(
                    (Field) property.getPsiElement(),
                    this.project
                )
            );
        }

        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/factory_method.twig.html");
        JtwigModel model = JtwigModel.newModel()
                .with("properties", properties);

        return template.render(model);
    }

    @Override
    @NotNull
    protected boolean isValid() {
        if (this.classProperties.length < 1) {
            HintManager.getInstance().showErrorHint(this.editor, "At least one property is needed to generate Factory method.");
            return false;
        }

        if (this.methodExists("with")) {
            HintManager.getInstance().showErrorHint(this.editor, "With method already exists.");
            return false;
        }

        return true;
    }
}
