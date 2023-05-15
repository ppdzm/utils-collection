package io.github.ppdzm.utils.universal.cli;

import io.github.ppdzm.utils.universal.core.SystemProperties;

import java.nio.charset.StandardCharsets;
import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Stuart Alex on 2017/9/8.
 */
public class MessageGenerator {
    private static final Map<String, ResourceBundle> resourceBundles = new HashMap<>();

    public static String generate(String name, int value) {
        ResourceBundle resourceBundle = getResourceBundle();
        if (resourceBundle != null && resourceBundle.containsKey(name)) {
            String pattern = new String(resourceBundle.getString(name).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            try {
                return MessageFormat.format(new ChoiceFormat(pattern).format(value), value);
            } catch (IllegalArgumentException e) {
                return generate(name, Integer.toString(value));
            }
        } else {
            return "Message missing: " + name + ":" + value;
        }
    }

    public static String generate(String name, Object... args) {
        ResourceBundle resourceBundle = getResourceBundle();
        if (resourceBundle != null && resourceBundle.containsKey(name)) {
            String pattern = new String(resourceBundle.getString(name).getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            return MessageFormat.format(pattern, args);
        } else {
            return "Message missing: " + name + " " + Arrays.stream(args).map(Object::toString).collect(Collectors.joining(" "));
        }
    }

    private static ResourceBundle getResourceBundle() {
        String language = SystemProperties.language();
        if (!resourceBundles.containsKey(language)) {
            try {
                ResourceBundle resourceBundle = ResourceBundle.getBundle("message", Locale.forLanguageTag(language));
                resourceBundles.put(language, resourceBundle);
            } catch (Exception e) {
                // Handle the exception
            }
        }
        return resourceBundles.get(language);
    }

}
