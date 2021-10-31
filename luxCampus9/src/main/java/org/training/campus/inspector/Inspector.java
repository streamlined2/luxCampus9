package org.training.campus.inspector;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.LinkedHashMap;
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
		selectMethodsPerform(obj, method -> method.getParameterCount() == 0, (T o, Method m) -> {
			try {
				m.setAccessible(true);
				m.invoke(o);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		});
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
		selectMethodsPerform(obj, Inspector::checkForFinal, (T o, Method m) -> System.out.println(m));
	}

	private static boolean checkForFinal(Method method) {
		boolean containsFinal = Modifier.isFinal(method.getModifiers());
		for (Parameter p : method.getParameters()) {
			containsFinal = containsFinal || Modifier.isFinal(p.getModifiers());
		}
		return containsFinal;
	}

	public static <T> void printNonPublicMethods(Class<T> cl) {
		selectMethodsPerform(cl, Inspector::checkForNonPublic, (Class<T> c, Method m) -> System.out.println(m));
	}

	private static boolean checkForNonPublic(Method method) {
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

	private static <T> void printImplementedInterfaces(Class<T> cl) {
		for (Class<?> interf : cl.getInterfaces()) {
			System.out.printf("     class %s implements interface %s%n", cl.getName(), interf.toString());
		}
	}

	private static void printAncestorClasses(Class<?> cl) {
	}

	public static <T> void initializePrivateFields(T obj) {
		// TODO
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
			var testObject = new TestClass();
			callMethodsWithNoArgs(testObject);

			System.out.println("______________________Task 3_________________________");
			printMethodSignaturesWithFinal(testObject);

			System.out.println("______________________Task 4_________________________");
			printNonPublicMethods(BigDecimal.class);

			System.out.println("______________________Task 5_________________________");
			printAncestorsAndImplementedInterfaces(LinkedHashMap.class);

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

}

class TestClass {
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
