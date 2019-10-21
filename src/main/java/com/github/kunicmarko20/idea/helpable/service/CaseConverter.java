package com.github.kunicmarko20.idea.helpable.service;

public class CaseConverter {
    public static String fromSnakeToCamelCase(String phrase) {
        StringBuilder camelCasedWords = new StringBuilder();

        String[] words = phrase.split("_");

        camelCasedWords.append(words[0].toLowerCase());

        for (int i = 1; i < words.length; i++) {
            camelCasedWords.append(Character.toUpperCase(words[i].charAt(0)));

            if (words[i].length() > 1) {
                camelCasedWords.append(words[i].substring(1).toLowerCase());
            }
        }

        return camelCasedWords.toString();
    }
}
