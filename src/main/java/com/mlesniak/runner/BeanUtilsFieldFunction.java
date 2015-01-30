package com.mlesniak.runner;

import java.lang.reflect.Field;

/**
 * Function to work with a field.
 *
 * The field will always be accessible.
 *
 * @author Michael Lesniak (mlesniak@micromata.de)
 */
@FunctionalInterface
public interface BeanUtilsFieldFunction {
    void handleField(Field field) throws IllegalAccessException;
}
