package io.github.ppdzm.utils.universal.core;

/**
 * @author Created by Stuart Alex on 2021/5/18.
 */
public class SystemProperties {

    public static String set(String key, String value) {
        return System.setProperty(key, value);
    }

    public static boolean isKeyOfProfile(String key) {
        return key.equals(CoreConstants.PROFILE_ACTIVE_KEY) || key.equals(CoreConstants.PROFILE_EXTENSION_KEY) || key.equals(CoreConstants.PROFILE_PREFIX_KEY);
    }

    public static String language() {
        return System.getProperty(CoreConstants.PROGRAM_LANGUAGE_KEY, "en");
    }

    public static boolean getLogging2Stdout() {
        return Boolean.parseBoolean(System.getProperty(CoreConstants.LOGGING_STDOUT_ENABLED_KEY, "false"));
    }

    public static void setLogging2Stdout(boolean enabled) {
        set(CoreConstants.LOGGING_STDOUT_ENABLED_KEY, String.valueOf(enabled));
    }

    public static String configFilePrefix() {
        return System.getProperty(CoreConstants.PROFILE_PREFIX_KEY, System.getProperty(CoreConstants.PROFILE_PREFIX_KEY_ALIAS, CoreConstants.DEFAULT_PREFIX_VALUE));
    }

    public static String configFileActive() {
        return System.getProperty(CoreConstants.PROFILE_ACTIVE_KEY, System.getProperty(CoreConstants.PROFILE_ACTIVE_KEY_ALIAS));
    }

    public static String configFileExtension() {
        return System.getProperty(CoreConstants.PROFILE_EXTENSION_KEY, System.getProperty(CoreConstants.PROFILE_EXTENSION_KEY_ALIAS, CoreConstants.DEFAULT_EXTENSION_VALUE));
    }
}
