package com.github.kunicmarko20.idea.helpable.service;

import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;
import java.util.function.Function;

public class ClassFieldFinder {
    @NotNull
    public static PhpNamedElementNode[] constants(@NotNull PhpClass phpClass) {
        return find(phpClass, Field::isConstant);
    }

    private static PhpNamedElementNode[] find(@NotNull PhpClass phpClass, Function<Field, Boolean> filter) {
        TreeMap<String, PhpNamedElementNode> nodes = new TreeMap<>();
        Field[] fields = phpClass.getOwnFields();

        for (Field field : fields) {
            if (filter.apply(field)) {
                nodes.put(field.getName(), new PhpNamedElementNode(field));
            }
        }

        return nodes.values().toArray(new PhpNamedElementNode[0]);
    }

    @NotNull
    public static PhpNamedElementNode[] properties(@NotNull PhpClass phpClass) {
        return find(phpClass, (field) -> !field.isConstant());
    }
}
