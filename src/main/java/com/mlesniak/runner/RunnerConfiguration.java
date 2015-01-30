package com.mlesniak.runner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class RunnerConfiguration {
    private transient static RunnerConfiguration INSTANCE;
    protected transient Map<String, String> nonFields = new HashMap<>();

    public static RunnerConfiguration get() {
        return INSTANCE;
    }

    public String get(String field) {
        return nonFields.get(field);
    }

    @Override
    public String toString() {
        String variables = BeanUtils.toString(this);
        if (nonFields.isEmpty()) {
            return variables;
        }

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : nonFields.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append("\n");
        }
        // Remove last \n.
        sb.deleteCharAt(sb.length() - 1);

        return variables + "\n" + sb.toString();
    }

    public void add(String key, String value) {
        nonFields.put(key, value);
    }
}
