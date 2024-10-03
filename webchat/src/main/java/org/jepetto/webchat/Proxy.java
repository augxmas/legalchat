package org.jepetto.webchat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jepetto.util.Util;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * chatbot측 연결 통로
 */
public class Proxy {

	private String chatbotHost;
	private String chatbotPort;
	private String app;
	private String method;
	
	private static final String contentType		= "Content-Type";
	private static final String applicationJson = "application/json;charset=UTF-8";
	private static final String urlDelemiter	= "/";	
	private static final String portDelemiter	= ":";
	//private static final String error			= "error";
	private static final String httpErrorCode	= "500";
	
	public Proxy(String method, String chatbotHost, String chatbotPort, String app) {
		this.method			= method;
		this.chatbotHost	= chatbotHost;
		this.chatbotPort	= chatbotPort;
		this.app			= app;
	}
	
	/**
	 * chatbot 서버에 reply 값을 전달하고 결과값을 받아옴
	 * 결과값은 새로운 질의가 될 수 있음
	 * @return 
	 * @throws Exception 
	 */
	public JSONObject interact(JSONObject json) throws Exception {
		/*
		JSONParser parser = new JSONParser();
		Map<String,String> map = new HashMap<String,String>();
		map.put(contentType, applicationJson);
		String str = Util.send(method, chatbotHost+portDelemiter+chatbotPort+urlDelemiter+ app, map,json.toJSONString());
		System.out.println(str);
		json = (JSONObject)parser.parse(str);
		if(str.indexOf(httpErrorCode) > 0) {
			throw new Exception("Server Internal Error");
		}
		//*/
		return json;
	}
	
	/**
	 * chatbot 서버에 reply 값을 전달하고 결과값을 받아옴
	 * 결과값은 새로운 질의가 될 수 있음
	 * @return 
	 */
	public JSONObject interact(int index, String message, String locale) {
		MessageBroker msg		= new MessageBroker(Locale.ENGLISH); 
		JSONObject json = null;
		try {
			JSONParser parser = new JSONParser();
			json = (JSONObject)parser.parse(msg.getReply(index));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	
}
