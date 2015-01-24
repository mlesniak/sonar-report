package com.mlesniak.sonar.report;

/**
 * Configuration bean.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class Configuration {
    private static Configuration INSTANCE;

    private String user;
    private String password;
    private String server;

    public String getServer() {
        return server;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public static Configuration get() {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return BeanUtils.toString(this);
    }
}
