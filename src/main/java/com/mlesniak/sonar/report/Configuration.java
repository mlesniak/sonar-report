package com.mlesniak.sonar.report;

/**
 * Configuration bean.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class Configuration {
    private String server;

    public String getServer() {
        return server;
    }

    @Override
    public String toString() {
        return BeanUtils.toString(this);
    }
}
