package com.mlesniak.sonar.report;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
            try {
                func.handleField(field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(accStatus);
        }
    }

    public static void setField(Object instance, String name, Object value) {
        Field field = null;
        try {
            field = instance.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }
        boolean accStatus = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        field.setAccessible(accStatus);
    }

    public static String toString(Object instance) {
        final StringBuilder sb = new StringBuilder();

        BeanUtils.forEachField(instance, field -> {
            try {
                if (Modifier.isTransient(field.getModifiers())) {
                    // Ignore internal fields.
                    return;
                }
                if (field.get(instance) == instance) {
                    // Prevent recursion (simple enough for this)
                    return;
                }
                sb.append(field.getName());
                sb.append("=");
                Object o = field.get(instance);
                sb.append(o == null ? "null" : o.toString());
            } catch (IllegalAccessException e) {
                System.out.println("TODO Logging");
            }
            sb.append("\n");
        });

        return sb.toString();
    }
}
