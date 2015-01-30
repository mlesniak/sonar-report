package com.mlesniak.runner;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Configuration bean.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class ConfigurationTool {
    private static Logger LOG = LoggerFactory.getLogger(ConfigurationTool.class);
    private static String appName;
    private static Class<? extends RunnerConfiguration> bean;

    public static RunnerConfiguration parse(String[] args) {
        try {
            initializeInstanceVariablesFromAnnotations();
            initializeLogging(appName);
            RunnerConfiguration instance = bean.newInstance();
            Properties props = loadProperties(appName, args);
            Map<String, String> argMap = parseArgs(args);
            RunnerConfiguration config = parseToInstance(instance, props, argMap);
            addNonFields(instance, props, argMap);
            BeanUtils.setField(config, "INSTANCE", config);
            handleLogLevel(config);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Configuration:\n{}", config.toString());
            }
            return config;
        } catch (Exception e) {
            LOG.error("Error while generating configuration: {}", e.getMessage());
            System.exit(1);
        }

        // Never reached.
        return null;
    }

    private static void initializeInstanceVariablesFromAnnotations() throws ClassNotFoundException {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            if (stackTrace[i].getClassName().equals("com.mlesniak.runner.ConfigurationTool")) {
                String annotatedClass = stackTrace[i + 2].getClassName();
                Class<?> annClass = ConfigurationTool.class.getClassLoader().loadClass(annotatedClass);
                Runner annotation = annClass.getAnnotation(Runner.class);
                if (annotation == null) {
                    throw new IllegalArgumentException("Main class not annotated with Runner.");
                }
                bean = annotation.configClass();
                appName = annotation.appName();
                return;
            }
        }
    }

    private static void initializeLogging(String appName) throws JoranException {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(context);
        // Override default configuration.
        context.reset();
        context.putProperty("application-name", appName);
        InputStream stream = ConfigurationTool.class.getClassLoader().getResourceAsStream("logback.xml");
        jc.doConfigure(stream);
    }

    private static <T extends RunnerConfiguration> void handleLogLevel(T config) {
        if (config.get("logLevel") != null) {
            // Think about a good generalization for com.mlesniak.
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.mlesniak");
            Level logLevel = Level.toLevel(config.get("logLevel"));
            root.setLevel(logLevel);
        }
    }

    /**
     * Add property and command line fields which are not set as fields.
     */
    private static <T extends RunnerConfiguration> void addNonFields(T instance, Properties props, Map<String, String> argMap) {
        Set<String> fieldNames = new HashSet<String>();
        for (Field field : instance.getClass().getDeclaredFields()) {
            fieldNames.add(field.getName());
        }

        // Handle additional key values from property files.
        for (Object key : props.keySet()) {
            if (!fieldNames.contains(key.toString())) {
                instance.add(key.toString(), props.get(key).toString());
            }
        }

        // Handle additional key values from command line (overwrite property files).
        for (Map.Entry<String, String> entry : argMap.entrySet()) {
            if (!fieldNames.contains(entry.getKey())) {
                instance.add(entry.getKey(), entry.getValue());
            }
        }
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> argMap = new HashMap<>();

        // Is an additional argument given? Then it's probably a file name.
        int startAt = 0;
        if (args.length % 2 == 1) {
            startAt = 1;
        }

        for (int i = startAt; i < args.length; i += 2) {
            String key = args[i].substring(1, args[i].length());
            argMap.put(key, args[i + 1]);
        }

        return argMap;
    }

    private static Properties loadProperties(String defaultFilename, String[] args) throws IOException {
        Properties props = new Properties();

        String filename = defaultFilename + ".properties";
        if (args.length % 2 == 1) {
            filename = args[0];
        }

        File file = new File(filename);
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
                if (value != null) {
                    field.set(instance, value);
                }
            } catch (IllegalAccessException e) {
                LOG.error("Internal error while accessing field: {}, error:{}", field.getName(), e.getMessage());
            }
        });

        return instance;
    }
}
