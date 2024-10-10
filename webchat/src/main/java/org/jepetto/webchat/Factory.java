package org.jepetto.webchat;

import java.lang.reflect.InvocationTargetException;
import org.jepetto.util.PropertyReader;

public class Factory {

	private PropertyReader reader = PropertyReader.getInstance();
	
	private static Factory instance = new Factory();

	private Factory() {
	}

	public static Factory getInstance() {
		return instance;
	}

	public Translator getTranslator() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		String className = reader.getProperty("justia.class.name");
		Class<?> clazz = Class.forName(className);
		Translator translator = (Translator)clazz.getDeclaredConstructor().newInstance();
		return translator;
	}

}
