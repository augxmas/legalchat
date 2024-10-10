package org.jepetto.webchat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jepetto.util.PropertyReader;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

public class GoogleTranslator extends Translator {

	PropertyReader reader = PropertyReader.getInstance();

	public GoogleTranslator() {
		System.setProperty("GOOGLE_API_KEY", reader.getProperty("justia.google_api_key"));
	}

	public String ko(String eng) {
		Translate translate = TranslateOptions.getDefaultInstance().getService();
		Translation translation = translate.translate(eng, Translate.TranslateOption.sourceLanguage(EN),
				Translate.TranslateOption.targetLanguage(KO));
		return translation.getTranslatedText();
	}

	public String en(String kor) {
		Translate translate = TranslateOptions.getDefaultInstance().getService();
		Translation translation = translate.translate(kor, Translate.TranslateOption.sourceLanguage(KO),
				Translate.TranslateOption.targetLanguage(EN));
		return translation.getTranslatedText();
	}
	
	public static void main(String args[]) {
		/*
		Translator t = new GoogleTranslator();
		System.out.println(t.en("너랑 함 빠구리하고 싶다"));
		System.out.println(t.ko("i'd like to have sex with you"));
		//*/
		
        Class<?> clazz;
		try {
			clazz = Class.forName("org.jepetto.webchat.GoogleTranslator");
			Object googleTranslator = clazz.getDeclaredConstructor().newInstance();
			
			/*
			Method koMethod = clazz.getMethod("ko", String.class);
            String resultKo = (String) koMethod.invoke(googleTranslator, "Can i have sex with you");
            System.out.println(resultKo);			
			//*/
			
			Method koMethod = clazz.getMethod("en", String.class);
            String resultKo = (String) koMethod.invoke(googleTranslator, "너랑 자고 싶다,함 주라");
            System.out.println(resultKo);			
            
			
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
