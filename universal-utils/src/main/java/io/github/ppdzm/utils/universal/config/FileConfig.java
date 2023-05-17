package io.github.ppdzm.utils.universal.config;

import io.github.ppdzm.utils.universal.base.ResourceUtils;
import io.github.ppdzm.utils.universal.cli.CliUtils;
import io.github.ppdzm.utils.universal.cli.Render;
import io.github.ppdzm.utils.universal.core.CoreConstants;
import io.github.ppdzm.utils.universal.core.SystemProperties;
import io.github.ppdzm.utils.universal.formats.json.JacksonJsonUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * @author Created by Stuart Alex on 2021/5/7.
 */
public class FileConfig extends AbstractConfig {
    private static final long serialVersionUID = 7904251176493043350L;
    private static final String JSON_EXTENSION=".json";

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
            logging.logInfo("Profile " + CliUtils.rendering("default", Render.GREEN) + CliUtils.rendering(" activated", Render.MAGENTA));
        } else {
            System.setProperty(CoreConstants.PROFILE_ACTIVE_KEY, active);
            System.setProperty(CoreConstants.PROFILE_ACTIVE_KEY_ALIAS, active);
            logging.logInfo("Profile " + CliUtils.rendering(active, Render.GREEN) + CliUtils.rendering(" activated", Render.MAGENTA));
        }
        String[] splits = active.split("/");
        active = splits[splits.length - 1];
        splits[splits.length - 1] = "";
        String directory = String.join("/", splits);
        String profileName = "";
        if (name.isEmpty()) {
            profileName = directory + active + fixedExtension;
        } else if (active.isEmpty()) {
            profileName = directory + name + fixedExtension;
        } else {
            profileName = directory + name + "-" + active + fixedExtension;
        }
        logging.logInfo("Load config from file " + CliUtils.rendering(profileName, Render.GREEN));
        Properties properties = new Properties();
        URL url = ResourceUtils.locateResourceAsUrl(profileName);
        if (url != null) {
            String path = url.getPath();
            logging.logInfo("Config file located at " + CliUtils.rendering(path, Render.GREEN));
            InputStream inputStream = url.openStream();
            if (fixedExtension.equals(JSON_EXTENSION)) {
                Map<String, Object> configMap = JacksonJsonUtils.parse(url.openStream(), Map.class);
                properties.put(CoreConstants.PROFILE_ROOT, configMap);
            } else {
                properties.load(inputStream);
            }
            properties.setProperty(CoreConstants.PROFILE_PATH_KEY, path.substring(0, path.lastIndexOf("/")));
        } else {
            logging.logWarning(CliUtils.rendering("Config file ", Render.YELLOW) +
                    CliUtils.rendering(profileName, Render.GREEN) +
                    CliUtils.rendering(" not found, using only default configurations defined in code", Render.YELLOW));
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
            if (key.toString().equals(CoreConstants.PROFILE_ACTIVE_KEY) || key.toString().equals(CoreConstants.PROFILE_EXTENSION_KEY) || key.toString().equals(CoreConstants.PROFILE_PREFIX_KEY)) {
                System.setProperty(key.toString(), value);
            }
            properties.put(key, value);
        }
        return properties;
    }

    private Properties initialize() throws Exception {
        return initialize(SystemProperties.configFilePrefix(), SystemProperties.configFileExtension());
    }

}