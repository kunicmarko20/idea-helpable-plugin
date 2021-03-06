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

import java.util.ArrayList;
import java.util.List;

public class WitherHandler extends ActionHandler {
    @Override
    protected String body() {
        PhpNamedElementNode[] properties = this.classProperties;

        if (properties.length > 1) {
            properties = PropertyChooser.choose(this.filteredProperties(), this.project);
        }

        if (properties == null) {
            return null;
        }

        StringBuilder body = new StringBuilder();
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/with.twig.php");

        for (PhpNamedElementNode property : properties) {
            body.append(
                template.render(
                    JtwigModel.newModel()
                            .with("property", property.getText())
                            .with(
                                    "property_type",
                                    PropertyTypeFinder.find(
                                            (Field) property.getPsiElement(),
                                            this.project
                                    )
                            )
                )
            );
        }

        return body.toString();
    }

    private PhpNamedElementNode[] filteredProperties() {
        List<PhpNamedElementNode> properties = new ArrayList<>();

        for (PhpNamedElementNode property : classProperties) {
            if (!this.methodExists("with" + WordUtils.capitalize(property.getText()))) {
                properties.add(property);
            }
        }

        return properties.toArray(new PhpNamedElementNode[properties.size()]);
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
