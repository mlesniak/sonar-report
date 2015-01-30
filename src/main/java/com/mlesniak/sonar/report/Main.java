package com.mlesniak.sonar.report;

import com.mlesniak.runner.ConfigurationTool;
import com.mlesniak.runner.Runner;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;


/**
 * Main entry point.
 *
 * @author Michael Lesniak (mail@mlesniak.com)
 */
@Runner(configClass = Configuration.class, appName = "sonar-report")
public class Main {
    private static Logger LOG = LoggerFactory.getLogger(Main.class);;

    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    private void run(String[] args) throws Exception {
        Configuration config = ConfigurationTool.parse(args);

        // Testing.
        LOG.info("Starting sonar reporting.");
        System.exit(1);

        SonarConnection sonar = new SonarConnection();
        sonar.login();
        List<SonarConnection.Issue> issues = sonar.getIssues();
        TemplateOutput templateOutput = new TemplateOutput();
        String result = templateOutput.processIssues(issues);
        FileUtils.writeStringToFile(new File(config.getReportFile()), result);
        LOG.info("Wrote report to {}", config.getReportFile());
    }


}
