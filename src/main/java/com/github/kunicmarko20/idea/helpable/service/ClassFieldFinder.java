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
        return find(phpClass.getOwnFields(), Field::isConstant);
    }

    private static PhpNamedElementNode[] find(@NotNull Field[] fields, Function<Field, Boolean> filter) {
        TreeMap<String, PhpNamedElementNode> nodes = new TreeMap<>();

        for (Field field : fields) {
            if (filter.apply(field)) {
                nodes.put(field.getName(), new PhpNamedElementNode(field));
            }
        }

        return nodes.values().toArray(new PhpNamedElementNode[0]);
    }

    @NotNull
    public static PhpNamedElementNode[] ownedProperties(@NotNull PhpClass phpClass) {
        return find(phpClass.getOwnFields(), (field) -> !field.isConstant());
    }

    @NotNull
    public static PhpNamedElementNode[] allProperties(@NotNull PhpClass phpClass) {
        return find(phpClass.getFields().toArray(new Field[0]), (field) -> !field.isConstant());
    }
}
