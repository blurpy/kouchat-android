
/***************************************************************************
 *   Copyright 2006-2019 by Christian Ihle                                 *
 *   contact@kouchat.net                                                   *
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

import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.jetbrains.annotations.NonNls;

/**
 * Utilities for tests.
 *
 * @author Christian Ihle
 */
public final class TestUtils {

    private TestUtils() {

    }

    /**
     * If this system is a Mac.
     */
    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    /**
     * Gets the value of the field with the specified name in the specified object. Supports inheritance.
     *
     * @param object The object to get the value from.
     * @param fieldClass The class of the field.
     * @param fieldName The name of the field.
     * @param <T> The class of the field.
     * @return The value in the field of the object.
     */
    public static <T> T getFieldValue(final Object object, final Class<T> fieldClass, @NonNls final String fieldName) {
        Validate.notNull(object, "The object to get the value from can not be null");
        Validate.notNull(fieldClass, "The class of the field can not be null");
        Validate.notEmpty(fieldName, "The name of the field can not be empty");

        final Field field = getField(object, fieldName);
        return getValue(object, fieldClass, field);
    }

    /**
     * Set the value in the field with the specified name in the specified object. Supports inheritance.
     *
     * @param object The object to set the value in.
     * @param fieldName The name of the field.
     * @param value The value to set in the field.
     */
    public static void setFieldValue(final Object object, @NonNls final String fieldName, final Object value) {
        Validate.notNull(object, "The object to set the value in can not be null");
        Validate.notEmpty(fieldName, "The name of the field can not be empty");

        final Field field = getField(object, fieldName);
        setValue(object, value, field);
    }

    /**
     * Creates a mock from the specified class and sets it as the value in the field with the specified name
     * in the specified object. Supports inheritance.
     *
     * @param object The object to set the value in.
     * @param fieldName The name of the field.
     * @param classToMock The class to create a mock from, as the value to set in the field.
     */
    public static <T> T setFieldValueWithMock(final Object object, @NonNls final String fieldName, final Class<T> classToMock) {
        Validate.notNull(classToMock, "The class to mock can not be null");

        final T mock = mock(classToMock);
        setFieldValue(object, fieldName, mock);

        return mock;
    }

    /**
     * Checks whether the value of the field with the given name in the given object is <code>null</code>.
     *
     * @param object The object to check.
     * @param fieldName The name of the field to check.
     * @return If the value is null.
     */
    public static boolean fieldValueIsNull(final Object object, @NonNls final String fieldName) {
        return getFieldValue(object, Object.class, fieldName) == null;
    }

    /**
     * Checks whether all the fields in the given object are <code>null</code>, except for primitives.
     *
     * @param object The object to check.
     * @return If all fields are null.
     */
    public static boolean allFieldsAreNull(final Object object) {
        final Field[] declaredFields = object.getClass().getDeclaredFields();

        for (final Field declaredField : declaredFields) {
            final Class<?> declaredFieldType = declaredField.getType();

            if (declaredFieldType.isPrimitive()) {
                // Primitive can't be null - ignoring
                continue;
            }

            if (!fieldValueIsNull(object, declaredField.getName())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks whether all the fields in the given object have a value other than <code>null</code>.
     *
     * @param object The object to check.
     * @return If all fields are have a value.
     */
    public static boolean allFieldsHaveValue(final Object object) {
        final Field[] declaredFields = object.getClass().getDeclaredFields();

        for (final Field declaredField : declaredFields) {
            if (fieldValueIsNull(object, declaredField.getName())) {
                return false;
            }
        }

        return true;
    }

    private static void setValue(final Object object, final Object value, final Field field) {
        final boolean originalAccessible = field.isAccessible();

        try {
            field.setAccessible(true);
            removeFinal(field);
            field.set(object, value);
        }

        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        finally {
            field.setAccessible(originalAccessible);
        }
    }

    // Mostly useful for static final fields
    private static void removeFinal(final Field field) throws NoSuchFieldException, IllegalAccessException {
        final Field modifiersField = Field.class.getDeclaredField("modifiers");

        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }

    private static Field getField(final Object object, final String fieldName) {
        return getField(object.getClass(), fieldName);
    }

    private static Field getField(final Class<?> objectClass, final String fieldName) {
        final Field[] declaredFields = objectClass.getDeclaredFields();

        for (final Field declaredField : declaredFields) {
            if (declaredField.getName().equals(fieldName)) {
                return declaredField;
            }
        }

        final Class<?> objectSuperclass = objectClass.getSuperclass();

        if (objectSuperclass != null) {
            return getField(objectSuperclass, fieldName);
        }

        throw new RuntimeException(new NoSuchFieldException(fieldName));
    }

    private static <T> T getValue(final Object object, final Class<T> fieldClass, final Field field) {
        final boolean originalAccessible = field.isAccessible();

        try {
            field.setAccessible(true);
            return fieldClass.cast(field.get(object));
        }

        catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        finally {
            field.setAccessible(originalAccessible);
        }
    }
}
