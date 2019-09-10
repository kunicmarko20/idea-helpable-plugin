package com.github.kunicmarko20.idea.helpable.service;

import com.intellij.openapi.project.Project;
import com.jetbrains.php.codeInsight.PhpCodeInsightUtil;
import com.jetbrains.php.config.PhpLanguageFeature;
import com.jetbrains.php.config.PhpProjectConfigurationFacade;
import com.jetbrains.php.lang.PhpCodeUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class PropertyTypeFinder {
    @NotNull
    public static String find(@NotNull Field field, @NotNull Project project) {
        PhpPsiElement scopeForUseOperator = PhpCodeInsightUtil.findScopeForUseOperator(field);
        String docType = PhpCodeUtil.getTypeHint(field, scopeForUseOperator);

        return convertDocTypeToType(docType, project);
    }

    @NotNull
    private static String convertDocTypeToType(String docType, Project project) {
        String type = docType.contains("[]") ? "array" : docType;
        type = type.contains("boolean") ? "bool" : type;
        if (typeWithNull(docType)) {
            type = convertNullableType(project, type);
        }

        return type;
    }

    private static boolean typeWithNull(String docType) {
        return docType.split(Pattern.quote("|")).length == 2 && docType.toUpperCase().contains("NULL");
    }

    private static String convertNullableType(Project project, String type) {
        String[] split = type.split(Pattern.quote("|"));
        boolean hasNullableTypeFeature = PhpProjectConfigurationFacade.getInstance(project).getLanguageLevel().hasFeature(PhpLanguageFeature.NULLABLES);

        return split[0].equalsIgnoreCase("null") ? (hasNullableTypeFeature ? "?" : "") + split[1] : (hasNullableTypeFeature ? "?" : "") + split[0];
    }
}
