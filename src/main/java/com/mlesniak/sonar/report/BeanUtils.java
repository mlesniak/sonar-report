package com.mlesniak.sonar.report;

import java.lang.reflect.Field;

/**
 * Utility functions to work with java beans.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class BeanUtils {
    public static void forEachField(Object instance, BeanUtilsFieldFunction func) {
        for (Field field : instance.getClass().getDeclaredFields()) {
            boolean accStatus = field.isAccessible();
            field.setAccessible(true);
            func.handleField(field);
            field.setAccessible(accStatus);
        }
    }

    public static String toString(Object instance) {
        final StringBuilder sb = new StringBuilder();

        BeanUtils.forEachField(instance, field -> {
            sb.append(field.getName());
            sb.append("=");
            try {
                sb.append(field.get(instance).toString());
            } catch (IllegalAccessException e) {
                System.out.println("TODO Logging");
            }
        });

        return sb.toString();
    }
}
