package org.jepetto.webchat;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.jepetto.util.PropertyReader;
import org.jepetto.util.Util;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * 질의에 답을 하게 되면 아래와 같은 순서로 실행됨
 * 
 * ChatMessageDecoder
 * 	0. init
 * 	1. willDecode
 * 	2. decoding 
 * JustiaEndPoint
 *	3. message 
 * 순서로 실행
 * 
 */
public class ChatMessageDecoder implements Decoder.Text<JSONObject> {

	//private TextToPDFConverter converter = new TextToPDFConverter();
	
	private EndpointConfig config;
	
	private static PropertyReader reader	= PropertyReader.getInstance();
	
	
	private static final String repos 		= reader.getProperty("justia.repository");		
	
	/**
	 * chatbot web server connection method = post
	 */
	private static final String method		= reader.getProperty("justia.chatbot.method");
	
	/**
	 * chatbot server 주소
	 */
	private static final String chatbotHost		= reader.getProperty("justia.chatbot.host");
	/**
	 * chatbot server 접속할 수 있는 port 번호
	 */
	private static final String chatportPort		= reader.getProperty("justia.chatbot.port");
	
	/**
	 * generator server 주소
	 */
	private static final String generatorHost		= reader.getProperty("justia.generator.host");
	/**
	 * generator server 접속할 수 있는 port 번호
	 */
	private static final String generatorPort		= reader.getProperty("justia.generator.port");
	
	
	/**
	 * response
	 */
	private static final String app			= reader.getProperty("justia.chatbot.app");
	
	/**
	 * pdf 전환 시, 편집 불가하도록 할 비밀번호
	 */
	private String password					= reader.getProperty("justia.pdf.password");
	
	/**
	 * 전환된 pdf 파일 저장 공간
	 */
	private String repository				= reader.getProperty("justia.repository");
	
	private static final String encode 		= "UTF-8";
	
	public static final String error		= "error";
	
	public static final String INPUT		= "reply";
	
	// 고소장 작성 응답 내용 키
	public static final String INDEX		= "index";
	public static final String MODE			= "mode";
	public static final String USERNAME		= "username";
	public static final String REPLY		= "reply";
	public static final String LOCALE		= "locale";
	
	public static final String FILENAME		= "fileName";
	
	public static final String FILE			= "file";
		
	/**
	 * 초기화, 특별이 할 일은 없음 
	 */
    @Override
    public void init(EndpointConfig config) {
    	this.config = config;
    	Map<String,Object> map = config.getUserProperties();
        // Initialization logic if needed
    }
    
    /**
     * 고소장 작성의 json이 형식에 에러가 있는지 여부를 확인
     * 맞다면 true, 
     * 아니면 false
     */
    @Override
    public boolean willDecode(String s) {
    	JSONParser parser	= new JSONParser();
    	JSONObject json		= null;
		try {
			json			= (JSONObject)parser.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
    	String index		= (String)json.get(INDEX);
    	String reply		= (String)json.get(REPLY);
    	Map map				= config.getUserProperties();
    	int _index			= Integer.parseInt(index);
    	map.put(String.valueOf(_index), reply);
    	return true;
    }
	
	
	/**
	 * chatbot 서버에 접속해서 사용자의 답장을 받고, 다음 질의를 사용자에게 넘김
	 * 번역
	 */
    @Override
    public JSONObject decode(String message) throws DecodeException {   	
    	
    	JSONParser parser	= new JSONParser();
    	JSONObject json		= null;
    	String index		= null;
    	String mode			= null;
    	String username		= null;
    	String reply		= null;
    	String locale		= null;
    	String fileName		= null;
    	String file			= null;
    	
		try {
			// 인공지능 chatbot, generator  접속 객체
			Proxy proxy = null;
			
			// 고소장 응답 메세지
			json		= (JSONObject)parser.parse(message);
			index		= (String)json.get(INDEX);
			mode		= (String)json.get(MODE);
			username	= (String)json.get(USERNAME);
			reply		= (String)json.get(REPLY);
			locale		= (String)json.get(LOCALE);
			fileName	= (String)json.get(FILENAME);
			file		= (String)json.get(FILE);
			try {
				Util.base64ToFile(repos, fileName,file);
			}catch(Exception e) {
				
			}
			
			MessageBroker msg		= null;
			if(locale.equalsIgnoreCase(Locale.KOREAN.getLanguage())) {
				msg 				= new MessageBroker(Locale.KOREAN);
			}else {
				msg 				= new MessageBroker(Locale.ENGLISH);
			}
			
			json			= msg.getTemplate(Integer.parseInt(index));
			
			Map map 		= config.getUserProperties();
			Set keys		= map.keySet();
			Iterator iter	= keys.iterator();
			String key		= null;
			String value	= null;
			
			JSONObject beliefState	= null;
			Map<String,String> temp = new HashMap<String,String>();
			// 마지막 질의는 
			if( Integer.parseInt(index) < MessageBroker.questionSize + 1) {
				json.put(INPUT, reply);
				proxy = new Proxy(method,chatbotHost,chatportPort,app);
			}else {
				proxy = new Proxy(method,generatorHost,generatorPort,app);
			}
			while(iter.hasNext()) {
				key				= (String)iter.next();
				try {
					beliefState = (JSONObject)((JSONObject)json.get(MessageBroker.agentStateKey)).get(MessageBroker.beliefStateKey);
					value		= (String)map.get(key);
					temp.put(MessageBroker.prev_actions[Integer.parseInt(key)], value);
					beliefState.clear();
					beliefState.putAll(temp);
				}catch(java.lang.NullPointerException e) {
					//e.printStackTrace();
				}catch(java.lang.ClassCastException e) {
					//e.printStackTrace();
				}catch(java.lang.NumberFormatException e) {
					
				}
			}
			// chatbot server에게 사용자 입력값을 전달하고 응답을 받음
			System.out.println("request:");
			System.out.println(json);
			json = proxy.interact(json);
			System.out.println("--------------------------------------");
			System.out.println("reply:");
			System.out.println(json.toJSONString());
			
		} catch (ParseException e) {
			e.printStackTrace();
			json = new JSONObject();
			json.put(error, "parsing..error");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json = new JSONObject();
			json.put(error, "exception..error");
		}
		
    	return json;
    }

    /**
     * config 내 해당 UserProperties 초기화
     */
    @Override
    public void destroy() {
    	this.config.getUserProperties().clear();
    }
}
