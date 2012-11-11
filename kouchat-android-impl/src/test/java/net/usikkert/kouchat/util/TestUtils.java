
/***************************************************************************
 *   Copyright 2006-2012 by Christian Ihle                                 *
 *   kontakt@usikkert.net                                                  *
 *                                                                         *
 *   This file is part of KouChat.                                         *
 *                                                                         *
 *   KouChat is free software; you can redistribute it and/or modify       *
 *   it under the terms of the GNU Lesser General Public License as        *
 *   published by the Free Software Foundation, either version 3 of        *
 *   the License, or (at your option) any later version.                   *
 *                                                                         *
 *   KouChat is distributed in the hope that it will be useful,            *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU      *
 *   Lesser General Public License for more details.                       *
 *                                                                         *
 *   You should have received a copy of the GNU Lesser General Public      *
 *   License along with KouChat.                                           *
 *   If not, see <http://www.gnu.org/licenses/>.                           *
 ***************************************************************************/

package net.usikkert.kouchat.util;

import java.lang.reflect.Field;

/**
 * Utilities for tests.
 *
 * @author Christian Ihle
 */
public final class TestUtils {

    private TestUtils() {

    }

    /**
     * Gets the value of the field with the specified name in the specified object.
     *
     * @param object The object to get the value from.
     * @param fieldClass The class of the field.
     * @param fieldName The name of the field.
     * @param <T> The class of the field.
     * @return The value in the field of the object.
     */
    public static <T> T getFieldValue(final Object object, final Class<T> fieldClass, final String fieldName) {
        Validate.notNull(object, "The object to get the value from can not be null");
        Validate.notNull(fieldClass, "The class of the field can not be null");
        Validate.notEmpty(fieldName, "The name of the field can not be empty");

        final Field field = getField(object, fieldName);
        return getValue(object, fieldClass, field);
    }

    /**
     * Set the value in the field with the specified name in the specified object.
     *
     * @param object The object to set the value in.
     * @param fieldName The name of the field.
     * @param value The value to set in the field.
     */
    public static void setFieldValue(final Object object, final String fieldName, final Object value) {
        Validate.notNull(object, "The object to set the value in can not be null");
        Validate.notEmpty(fieldName, "The name of the field can not be empty");

        final Field field = getField(object, fieldName);
        setValue(object, value, field);
    }

    private static void setValue(final Object object, final Object value, final Field field) {
        final boolean originalAccessible = field.isAccessible();

        try {
            field.setAccessible(true);
            field.set(object, value);
        }

        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        finally {
            field.setAccessible(originalAccessible);
        }
    }

    private static Field getField(final Object object, final String fieldName) {
        try {
            return object.getClass().getDeclaredField(fieldName);
        }

        catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T getValue(final Object object, final Class<T> fieldClass, final Field field) {
        final boolean originalAccessible = field.isAccessible();

        try {
            field.setAccessible(true);
            return fieldClass.cast(field.get(object));
        }

        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        finally {
            field.setAccessible(originalAccessible);
        }
    }
}
