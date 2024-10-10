package org.jepetto.webchat;

public abstract class Translator {

	public static final String KO = "ko";
	
	public static final String EN = "en";
	
	public abstract String ko(String eng);
	
	public abstract String en(String kor);
	
}
