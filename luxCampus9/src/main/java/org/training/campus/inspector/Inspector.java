package org.training.campus.inspector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public final class Inspector {

	private Inspector() {
	}

	public static <T> T spawnObject(Class<T> cl, Class<?>[] argTypes, Object... args)
			throws ReflectiveOperationException {
		return cl.getDeclaredConstructor(argTypes).newInstance(args);
	}

	public static <T> void callMethodsWithNoArgs(T obj) {
		// TODO
	}

	public static <T> void printMethodSignaturesWithFinal(T obj) {
		// TODO
	}

	public static <T> void printNonPublicMethods(Class<T> cl) {
		// TODO
	}

	public static <T> void printAncestorsAndImplementedInterfaces(Class<T> cl) {
		// TODO
	}

	public static <T> void initializePrivateFields(T obj) {
		// TODO
	}

	public static void main(String[] args) {
		try {
			var num = spawnObject(BigInteger.class, new Class[] { String.class, int.class }, new Object[] { "FF", 16 });
			System.out.printf("created object of BigInteger: value = %s%n", num.toString());
			var decimal = spawnObject(BigDecimal.class,
					new Class[] { char[].class, int.class, int.class, MathContext.class },
					new Object[] { "12345".toCharArray(), 0, 5, MathContext.DECIMAL32 });
			System.out.printf("created object of BigDecimal: value = %s%n", decimal.toString());
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}

}
