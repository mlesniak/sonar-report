package com.mlesniak.sonar.report;

import com.mlesniak.runner.RunnerConfiguration;

/**
 * Configuration bean.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class SonarReportConfiguration extends RunnerConfiguration {
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

    public String getReportFile() {
        return reportFile;
    }

}
