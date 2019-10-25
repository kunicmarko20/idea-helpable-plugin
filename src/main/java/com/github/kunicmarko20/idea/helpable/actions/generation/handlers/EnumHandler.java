package com.github.kunicmarko20.idea.helpable.actions.generation.handlers;

import com.github.kunicmarko20.idea.helpable.service.CaseConverter;
import com.github.kunicmarko20.idea.helpable.service.ClassFieldFinder;
import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import org.jetbrains.annotations.NotNull;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.util.HashMap;

public class EnumHandler extends ActionHandler {
    @Override
    @NotNull
    protected String body() {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/enum.twig.html");

        JtwigModel model = JtwigModel.newModel()
                .with("type", this.phpClass.getName())
                .with("variants", this.camelCasedVariants(ClassFieldFinder.constants(this.phpClass)));

        return template.render(model);
    }

    private HashMap<String, String> camelCasedVariants(PhpNamedElementNode[] constants) {
        HashMap<String, String> camelCasedVariants = new HashMap<>();

        for (PhpNamedElementNode constant : constants) {
            camelCasedVariants.put(
                CaseConverter.fromSnakeToCamelCase(constant.getText()),
                constant.getText()
            );
        }

        return camelCasedVariants;
    }

    @Override
    @NotNull
    protected boolean isValid() {
        return true;
    }
}
