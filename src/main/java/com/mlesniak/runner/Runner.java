package com.mlesniak.runner;

import com.mlesniak.sonar.report.Configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Configuration annotation to define properties for a runnable main class.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Runner {
    Class<Configuration> configClass();

    String appName();
}
