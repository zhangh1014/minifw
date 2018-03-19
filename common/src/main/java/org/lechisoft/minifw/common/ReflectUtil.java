package org.lechisoft.minifw.common;


import com.sun.istack.internal.NotNull;

import java.lang.reflect.Field;

public class ReflectUtil {

    /**
     * 获取属性对象
     *
     * @param obj       目标对象
     * @param fieldName 属性名称
     * @return 属性对象
     */
    public static Field getField(@NotNull Object obj, String fieldName) {
        Field field = null;
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException ignored) {
            }
        }
        return field;
    }

    /**
     * 获取属性的值
     *
     * @param obj       目标对象
     * @param fieldName 属性名称
     * @return 属性的值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        Field field = ReflectUtil.getField(obj, fieldName);
        if (null != field) {
            field.setAccessible(true);
            try {
                return field.get(obj);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
        return null;
    }

    /**
     * 设置属性的值
     *
     * @param obj        目标对象
     * @param fieldName  属性名称
     * @param fieldValue 属性的值
     */
    public static boolean setFieldValue(Object obj, String fieldName, String fieldValue) {
        Field field = ReflectUtil.getField(obj, fieldName);
        if (null != field) {
            try {
                field.setAccessible(true);
                field.set(obj, fieldValue);
                return true;
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }
        }
        return false;
    }

    /**
     * 获取属性对象
     *
     * @param obj   目标对象
     * @param clazz 查找类型
     * @return 属性对象
     */
    public static Field getField(@NotNull Object obj, Class clazz) {
        for (Class<?> objClazz = obj.getClass(); objClazz != Object.class; objClazz = objClazz.getSuperclass()) {
            Field[] fields = objClazz.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(clazz)) {
                    return field;
                }
            }
        }
        return null;
    }
}