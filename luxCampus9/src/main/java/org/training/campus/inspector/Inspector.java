package org.training.campus.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public final class Inspector {

	private Inspector() {
	}

	public static <T> T spawnObject(Class<T> cl, Class<?>[] argTypes, Object... args)
			throws ReflectiveOperationException {
		return cl.getDeclaredConstructor(argTypes).newInstance(args);
	}

	public static <T> void callMethodsWithNoArgs(T obj) {
		selectMethodsPerform(obj, method -> method.getParameterCount() == 0, Inspector::callMethod);
	}

	private static void callMethod(Object obj, Method method) {
		try {
			method.setAccessible(true);
			method.invoke(obj);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static <T> void selectMethodsPerform(Class<T> cl, Predicate<Method> methodCheck,
			BiConsumer<Class<T>, Method> action) {
		Arrays.asList(cl.getDeclaredMethods()).stream().filter(methodCheck)
				.forEach(method -> action.accept(cl, method));
	}

	private static <T> void selectMethodsPerform(T obj, Predicate<Method> methodCheck, BiConsumer<T, Method> action) {
		Arrays.asList(obj.getClass().getDeclaredMethods()).stream().filter(methodCheck)
				.forEach(method -> action.accept(obj, method));
	}

	public static <T> void printMethodSignaturesWithFinal(T obj) {
		selectMethodsPerform(obj, Inspector::checkIfFinal, (T o, Method m) -> System.out.println(m));
	}

	private static boolean checkIfFinal(Method method) {
		boolean containsFinal = Modifier.isFinal(method.getModifiers());
		for (Parameter p : method.getParameters()) {
			containsFinal = containsFinal || Modifier.isFinal(p.getModifiers());
		}
		return containsFinal;
	}

	public static <T> void printNonPublicMethods(Class<T> cl) {
		selectMethodsPerform(cl, Inspector::checkIfNonPublic, (Class<T> c, Method m) -> System.out.println(m));
	}

	private static boolean checkIfNonPublic(Method method) {
		return !Modifier.isPublic(method.getModifiers());
	}

	public static void printAncestorsAndImplementedInterfaces(Class<?> cl) {
		Class<?> ancestorClass = cl;
		while (ancestorClass != null) {
			System.out.println(ancestorClass);
			printImplementedInterfaces(ancestorClass);
			ancestorClass = ancestorClass.getSuperclass();
		}
	}

	private static void printImplementedInterfaces(Class<?> cl) {
		for (Class<?> interf : cl.getInterfaces()) {
			System.out.printf("     class %s implements %s%n", cl.getName(), interf.toString());
		}
	}

	public static void initializePrivateFields(Object obj) {
		selectFieldsPerform(obj, Inspector::checkIfPrivate, Inspector::initializeField);
	}

	private static <T> void selectFieldsPerform(T obj, Predicate<Field> fieldCheck, BiConsumer<T, Field> action) {
		Arrays.asList(obj.getClass().getDeclaredFields()).stream().filter(fieldCheck)
				.forEach(field -> action.accept(obj, field));
	}

	private static boolean checkIfPrivate(Field field) {
		return Modifier.isPrivate(field.getModifiers());
	}

	private static void initializeField(Object obj, Field field) {
		Class<?> type = field.getType();
		field.setAccessible(true);
		try {
			if (type.isPrimitive()) {
				if (type == boolean.class) {
					field.setBoolean(obj, false);
				} else if (type == byte.class) {
					field.setByte(obj, (byte) 0);
				} else if (type == short.class) {
					field.setShort(obj, (short) 0);
				} else if (type == int.class) {
					field.setInt(obj, 0);
				} else if (type == long.class) {
					field.setLong(obj, 0);
				} else if (type == float.class) {
					field.setFloat(obj, 0f);
				} else if (type == double.class) {
					field.setDouble(obj, 0D);
				} else if (type == char.class) {
					field.setChar(obj, '\0');
				}
			} else {
				field.set(obj, null);
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}

	}

	public static void main(String[] args) {
		try {
			System.out.println("______________________Task 1_________________________");
			var num = spawnObject(BigInteger.class, new Class[] { String.class, int.class }, new Object[] { "FF", 16 });
			System.out.printf("created object of BigInteger: value = %s%n", num.toString());
			var decimal = spawnObject(BigDecimal.class,
					new Class[] { char[].class, int.class, int.class, MathContext.class },
					new Object[] { "12345".toCharArray(), 0, 5, MathContext.DECIMAL32 });
			System.out.printf("created object of BigDecimal: value = %s%n", decimal.toString());

			System.out.println("______________________Task 2_________________________");
			var testObject = new MethodContainerClass();
			callMethodsWithNoArgs(testObject);

			System.out.println("______________________Task 3_________________________");
			printMethodSignaturesWithFinal(testObject);

			System.out.println("______________________Task 4_________________________");
			printNonPublicMethods(BigDecimal.class);

			System.out.println("______________________Task 5_________________________");
			printAncestorsAndImplementedInterfaces(LinkedHashMap.class);

			System.out.println("______________________Task 6_________________________");
			var dummy = new FieldContainerClass();
			System.out.printf("initial values are %s%n", dummy);
			initializePrivateFields(dummy);
			System.out.printf("initialized to zero/null values are %s%n", dummy);

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

}

class MethodContainerClass {
	public final void a() {
		System.out.println("a");
	}

	void b() {
		System.out.println("b");
	}

	protected void c() {
		System.out.println("c");
	}

	private final void d() {
		System.out.println("d");
	}

	public void e(final int a) {
		System.out.println("e");
	}

	void f(final String a) {
		System.out.println("f");
	}
}

class FieldContainerClass {
	private boolean booleanValue = true;
	private byte byteValue = Byte.MAX_VALUE;
	private short shortValue = Short.MAX_VALUE;
	private int intValue = Integer.MAX_VALUE;
	private long longValue = Long.MAX_VALUE;
	private float floatValue = Float.MAX_VALUE;
	private double doubleValue = Double.MAX_VALUE;
	private char charValue = 'A';
	private BigDecimal bigDecValue = BigDecimal.ONE;
	private BigInteger bigIntValue = BigInteger.TEN;

	@Override
	public String toString() {
		var join = new StringJoiner(",", "[", "]");
		join.add(Boolean.toString(booleanValue)).add(Byte.toString(byteValue)).add(Short.toString(shortValue))
				.add(Integer.toBinaryString(intValue)).add(Long.toString(longValue)).add(Float.toString(floatValue))
				.add(Double.toString(doubleValue)).add(Character.toString(charValue)).add(Objects.toString(bigDecValue))
				.add(Objects.toString(bigIntValue));
		return join.toString();
	}
}