package com.github.kunicmarko20.idea.helpable.service;

import com.jetbrains.php.lang.actions.PhpNamedElementNode;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

public class ClassPropertyFinder {
    @NotNull
    public static PhpNamedElementNode[] find(@NotNull PhpClass phpClass) {
        TreeMap<String, PhpNamedElementNode> nodes = new TreeMap<>();
        Collection<Field> fields = phpClass.getFields();
        Iterator fieldIterator = fields.iterator();

        while (fieldIterator.hasNext()) {
            Field field = (Field) fieldIterator.next();
            if (!field.isConstant()) {
                nodes.put(field.getName(), new PhpNamedElementNode(field));
            }
        }

        return nodes.values().toArray(new PhpNamedElementNode[0]);
    }
}
