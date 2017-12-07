package com.github.taniqng.eventbus.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 使用说明: <br>
 * <p>
 * 反射工具类
 * </p>
 */
public class EntityUtils {

	/**
	 * 
	 * @Title: getSuperClassGenricType
	 * @Description: 获取类的泛型参数类型
	 * @param clazz
	 *            类
	 * @param index
	 *            泛型参数index
	 * @return Class<?>
	 */
	public static Class<?> getSuperClassGenricType(final Class<?> clazz,
			final int index) {
		Type genType = clazz.getGenericSuperclass();
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class<?>) params[index];
	}

	public static Class<?> getFirstSuperInterfaceGenricType(
			final Class<?> clazz, final int index) {
		Type[] genTypes = clazz.getGenericInterfaces();
		if (genTypes == null || genTypes.length == 0) {
			return Object.class;
		}
		Type genType = genTypes[0];
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class<?>) params[index];
	}

	public static Class<?> getSuperClassGenricType(final Type genType,
			final int index) {
		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}
		return (Class<?>) params[index];
	}

	/**
	 * 获取指定类，指定属性的指定注解，找不到返回null
	 * 
	 * @param clazz
	 * @param fieldName
	 * @param annotation
	 * @return T
	 */
	public static <T extends Annotation> T findAnnotation(final Class<?> clazz,
			String fieldName, Class<T> annotation) {
		try {
			Field f = clazz.getDeclaredField(fieldName);
			return f.getAnnotation(annotation);
		} catch (NoSuchFieldException | SecurityException e) {
			Class<?> superClazz = clazz.getSuperclass();
			if (superClazz != null && !superClazz.equals(Object.class)) {
				return findAnnotation(superClazz, fieldName, annotation);
			} else {
				return null;
			}
		}
	}

	public static <A extends Annotation> Field findFirstField(
			final Class<?> clazz, Class<A> annotation) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			try {
				if (f.getAnnotation(annotation) != null) {
					return f;
				}

			} catch (SecurityException e) {
				// ignore
			}
		}

		Class<?> superClazz = clazz.getSuperclass();
		if (superClazz != null && !superClazz.equals(Object.class)) {
			return findFirstField(superClazz, annotation);
		} else {
			return null;
		}
	}

}
