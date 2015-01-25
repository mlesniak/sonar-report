package com.mlesniak.sonar.report;

import org.apache.commons.io.FileUtils;

import java.io.File;
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
        TemplateOutput templateOutput = new TemplateOutput();
        String result = templateOutput.processIssues(issues);
        FileUtils.writeStringToFile(new File(config.getReportFile()), result);
    }


}
