package com.mlesniak.sonar.report;

import com.mlesniak.runner.BaseRunner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;


/**
 * Main entry point.
 *
 * @author Michael Lesniak (mail@mlesniak.com)
 */
public class Main extends BaseRunner {
    public static void main(String[] args) throws Exception {
        new Main().run(args);
    }

    public void run(String[] args) throws Exception {
        initRunner(SonarReportConfiguration.class, "sonar-report", args);

        SonarReportConfiguration config = SonarReportConfiguration.get();
        info("Starting sonar reporting.");
        SonarConnection sonar = new SonarConnection();
        sonar.login();
        List<SonarConnection.Issue> issues = sonar.getIssues();
        TemplateOutput templateOutput = new TemplateOutput();
        String result = templateOutput.processIssues(issues);
        FileUtils.writeStringToFile(new File(config.getReportFile()), result);
        info("Wrote report to " + config.getReportFile());
    }


}
