package com.mlesniak.sonar.report;

import com.mlesniak.runner.BeanUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration bean.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class SonarReportConfiguration {
    private transient static SonarReportConfiguration INSTANCE;
    private transient Map<String, String> nonFields = new HashMap<>();

    private String user;
    private String password;
    private String server;
    private String reportFile;

    public String getServer() {
        return server;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public void add(String key, String value) {
        nonFields.put(key, value);
    }

    public String getReportFile() {
        return reportFile;
    }

    public String get(String field) {
        return nonFields.get(field);
    }

    public static SonarReportConfiguration get() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return BeanUtils.toString(this);
    }
}
