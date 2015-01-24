package com.mlesniak.sonar.report;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration bean.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class ConfigurationTool {

    public static <T> T parse(Class<T> bean, String[] args) {
        try {
            T instance = bean.newInstance();
            Properties props = loadProperties();
            Map<String, String> argMap = parseArgs(args);
            return parseToInstance(instance, props, argMap);
        } catch (Exception e) {
            System.err.println("TODO Logging");
            System.exit(1);
        }

        // Never reached.
        return null;
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> argMap = new HashMap<>();

        for (int i = 0; i < args.length; i += 2) {
            String key = args[i].substring(1, args[i].length());
            argMap.put(key, args[i + 1]);
        }

        return argMap;
    }

    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();

        File file = new File("application.properties");
        if (!file.exists()) {
            return props;
        }
        props.load(new FileReader(file));
        return props;
    }

    private static <T> T parseToInstance(T instance, Properties props, Map<String, String> args) throws IllegalAccessException {
        BeanUtils.forEachField(instance, field -> {
            // Determine value.
            String fieldName = field.getName();
            String value = null;
            if (props.containsKey(fieldName)) {
                value = props.getProperty(fieldName);
            }
            if (args.containsKey(fieldName)) {
                value = args.get(fieldName);
            }

            // Write value.
            try {
                field.set(instance, value);
            } catch (IllegalAccessException e) {
                System.out.println("TODO Logging");
            }
        });

        return instance;
    }
}
