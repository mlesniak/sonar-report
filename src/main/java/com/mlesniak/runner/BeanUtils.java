package com.mlesniak.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Utility functions to work with java beans.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
public class BeanUtils {
    private static Logger LOG = LoggerFactory.getLogger(BeanUtils.class);

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

    public static void setField(Object instance, String name, Object value) throws IllegalAccessException, NoSuchFieldException {
        Field field = null;
        // Will work for the simple scenarios targeted by this library.
        field = instance.getClass().getSuperclass().getDeclaredField(name);
        boolean accStatus = field.isAccessible();
        field.setAccessible(true);
        field.set(instance, value);
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
                LOG.error("Error whil accessing field {}, error:{}", field.getName(), e.getMessage());
            }
            sb.append("\n");
        });

        // Remove last \n.
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }
}
