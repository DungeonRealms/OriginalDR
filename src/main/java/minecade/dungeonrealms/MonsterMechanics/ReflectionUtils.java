package minecade.dungeonrealms.MonsterMechanics;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionUtils {
	
	/**
	 * Gets the value of a field from an object.
	 * 
	 * @param src The class where the field is defined.
	 * @param name The name of the field.
	 * @param type The type of the field.
	 * @param from The object to get the field value from.
	 * 
	 * @return The value of the field.
	 * @throws NoSuchFieldException If the field could not be found
	 * @throws SecurityException If the field could not be made accessible
	 */
	public static <T> T getFieldValue(Class<?> src, String name, Class<T> type, Object from) throws SecurityException, NoSuchFieldException {
		Field field = src.getDeclaredField(name);
		field.setAccessible(true);
		
		try {
			return type.cast(field.get(from));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Sets the value of a field for an object. Note that this will attempt to
	 * override final fields as well, so be careful where it's used. For a static
	 * field the <i>in</i> parameter can be null.
	 * 
	 * @param src The class where the field is defined.
	 * @param name The name of the field.
	 * @param in The object to set the value in.
	 * @param value The value to set for the field.
	 * @throws NoSuchFieldException If the field could not be found
	 * @throws SecurityException If the field could not be made accessible
	 */
	public static void setFieldValue(Class<?> src, String name, Object in, Object value) throws SecurityException, NoSuchFieldException {
		Field field = src.getDeclaredField(name);
		field.setAccessible(true);
		
		if(Modifier.isFinal(field.getModifiers())) {
			Field modifiers = Field.class.getDeclaredField("modifiers");
			modifiers.setAccessible(true);
			
			try {
				modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			field.set(in, value);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Invokes a method of an object.
	 * 
	 * @param src The class where the field is defined.
	 * @param name The name of the method.
	 * @param returnType The type returned by the method.
	 * @param in The object to set the value in.
	 * @param args The argument types for the method.
	 * @param params The arguments to pass to the method.
	 * @return The value returned by the method or null.
	 * @throws NoSuchMethodException If the method could not be found
	 * @throws SecurityException If the method could not be made accessible
	 */
	public static <T> T invokeMethod(Class<?> src, String name, Class<T> returnType, Object in, Class<?>[] args, Object[] params) throws SecurityException, NoSuchMethodException {
		Method method = src.getDeclaredMethod(name, args);
		method.setAccessible(true);
		
		try {
			return returnType.cast(method.invoke(in, params));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
