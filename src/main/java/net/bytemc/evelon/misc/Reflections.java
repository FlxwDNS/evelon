/*
 * Copyright 2019-2023 ByteMC team & contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bytemc.evelon.misc;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

public final class Reflections {

    public static final Unsafe unsafe;
    public static final String EMPTY_STRING = "";

    // all java elements
    public static final List<Class<?>> JAVA_ELEMENTS = List.of(String.class, Integer.class, Boolean.class, Short.class, Float.class, Byte.class, Double.class, Long.class);

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (IllegalAccessException | NoSuchFieldException var1) {
            throw new RuntimeException(var1);
        }
    }

    /**
     * @param parent object which contains the field
     * @param field  which should be read
     * @return the value of the field
     */
    public static Object readField(Object parent, Field field) {
        field.setAccessible(true);
        try {
            return field.get(parent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object readFieldFromId(Object parent, String fieldId) {
        return readField(parent, getField(parent.getClass(), fieldId));
    }

    public static void writeField(Object parent, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(parent, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param clazz which can maybe a number or something else
     * @return true if the clazz is assignable from number of equals int. Class
     */
    public static boolean isNumber(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz) ||
            (clazz.isPrimitive() && clazz == int.class || clazz == long.class || clazz == double.class
                || clazz == float.class || clazz == short.class || clazz == byte.class);
    }

    public static boolean isNumberFromField(Class<?> clazz, String fieldId) {
        return isNumber(getField(clazz, fieldId).getType());
    }

    public static Field getField(Class<?> parent, String fieldId) {
        try {
            return parent.getDeclaredField(fieldId);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param tClass which should be allocated
     * @param <T>    type of the class
     * @return a new instance of the class
     */
    public static <T> T allocate(Class<T> tClass) {
        try {
            return (T) unsafe.allocateInstance(tClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> List<T> writeList(List<T> element, T value) {
        element.add(value);
        return element;
    }

    public static Class<?>[] readGenericFromClass(Field field) {
        final ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        return Arrays.stream(parameterizedType.getActualTypeArguments()).map(type -> (Class<?>) type).toArray(value -> new Class<?>[value]);
    }
}
