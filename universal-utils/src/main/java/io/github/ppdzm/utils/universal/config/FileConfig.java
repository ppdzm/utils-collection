package io.github.ppdzm.utils.universal.config;

import io.github.ppdzm.utils.universal.base.ResourceUtils;
import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.core.CoreConstants;
import io.github.ppdzm.utils.universal.core.SystemProperties;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class FileConfig extends Config {

    public FileConfig(String name, String extension) throws Exception {
        this.properties = initialize(name, extension);
    }

    public FileConfig() throws Exception {
        this.properties = initialize();
    }

    public FileConfig(String[] args) throws Exception {
        this.properties = initialize(args);
    }

    private Properties initialize(String name, String extension) throws Exception {
        String fixedExtension = extension;
        if (!extension.startsWith(".")) {
            fixedExtension = "." + extension;
        }
        String active = SystemProperties.configFileActive();
        if (active == null) {
            InputStream inputStream = ResourceUtils.locateAsInputStream(name + fixedExtension);
            Properties properties = new Properties();
            properties.load(inputStream);
            active = properties.getProperty(CoreConstants.PROFILE_ACTIVE_KEY, "");
        }
        if (active.isEmpty()) {
            logInfo("Profile default activated");
        } else {
            logInfo("Profile " + active + " activated");
        }
        String profileName = "";
        if (name.isEmpty()) {
            profileName = active + fixedExtension;
        } else if (active.isEmpty()) {
            profileName = name + fixedExtension;
        } else {
            profileName = name + "-" + active + fixedExtension;
        }
        logInfo("Load config from file " + profileName);
        Properties properties = new Properties();
        URL url = ResourceUtils.locateResourceAsURL(profileName);
        if (url != null) {
            String path = url.getPath();
            logInfo("Config file located at " + path);
            InputStream inputStream = url.openStream();
            properties.load(inputStream);
            properties.setProperty(CoreConstants.PROFILE_PATH_KEY, path.substring(0, path.lastIndexOf("/")));
        } else {
            logWarning("Config file " + profileName + " not found, using only default configurations defined in code");
        }
        return properties;
    }

    private Properties initialize(String[] args) throws Exception {
        Properties cliProperties = new Properties();
        CliUtils.parseArguments(args, cliProperties);
        for (Object key : cliProperties.keySet()) {
            String value = cliProperties.getProperty(key.toString());
            if (key.toString().equals(CoreConstants.PROFILE_ACTIVE_KEY) || key.toString().equals(CoreConstants.PROFILE_EXTENSION_KEY) || key.toString().equals(CoreConstants.PROFILE_PREFIX_KEY)) {
                System.setProperty(key.toString(), value);
            }
        }
        Properties properties = initialize(SystemProperties.configFilePrefix(), SystemProperties.configFileExtension());
        for (Object key : cliProperties.keySet()) {
            String value = cliProperties.getProperty(key.toString());
            properties.put(key, value);
        }
        return properties;
    }

    private Properties initialize() throws Exception {
        return initialize(SystemProperties.configFilePrefix(), SystemProperties.configFileExtension());
    }

}