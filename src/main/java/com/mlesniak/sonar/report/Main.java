package com.mlesniak.sonar.report;

import java.util.List;

/**
 * Main entry point.
 *
 * @author Michael Lesniak (mail@mlesniak.com)
 */
public class Main {
    private Configuration config;

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    private void run(String[] args) throws Exception {
        config = ConfigurationTool.parse(Configuration.class, args);
        // TODO ML Logging
        System.out.println("Configuration:");
        System.out.println(config);

        SonarConnection sonar = new SonarConnection();
        sonar.login();
        List<SonarConnection.Issue> issues = sonar.getIssues();
        for (SonarConnection.Issue issue : issues) {
            System.out.println(issue.getComponent() + "\t" + issue.getRule() + "\t" + issue.getMessage());
        }
    }


}
