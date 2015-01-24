package com.mlesniak.sonar.report;

/**
 * Main entry point.
 *
 * @author Michael Lesniak (mail@mlesniak.com)
 */
public class Main {
    private Configuration config;

    public static void main(String[] args) {
        new Main().run(args);
    }

    private void run(String[] args) {
        config = ConfigurationTool.parse(Configuration.class, args);
        System.out.println(config);
    }
}
