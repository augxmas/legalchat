package org.jepetto.webchat;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.jepetto.util.PropertyReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * 작성자(고소인)와 AI Chatbot과 오고가는 json 메세지를 담당하는 클래스
 * 
 */
public class MessageBroker {
	
	/**
	 * i18N을 적용하여 국문, 영어를 분리하여 Resource로 관리할 수 있도록 함
	 */
	private ResourceBundle msg = null;
	
	private static PropertyReader reader					= PropertyReader.getInstance();

	// 질의 갯수
	public static final int questionSize					= Integer.parseInt(reader.getProperty("justia.questions.length"));	 
	private Map<Integer,String> indexes				= new java.util.LinkedHashMap<Integer, String>(questionSize);
	
	/**
	 *  message 파일명,
	 *  resources 디렉토리에 위치시킴 (/src/main/resources)
	 *  영문		: MessageBundle.properties
	 *  한국어	: MessageBundle_ko.properties
	 *  
	 *  key
	 *  questions 		: 고소장 작성자에게 묻는 질의들
	 *  domain			: 법죄유형 
	 *  prev_actions	: 질의유형, 이 질의유형에 따른 question이 questions에 정의되어 있음
	 *  error.500		: chatserver측 에러 메세지
	 */
	private static final String MessagesBundle		= "MessagesBundle";
	
	public static final String delimieter 			= ",";
	public static final String prev_action			= "prev_actions";
	
	private String replyTempltes[]					= new String[questionSize];
	public static String prev_actions[]				= null;
	
	public MessageBroker(Locale lang) {
		// resource 내에 MessageBundle.properties 파일로 영문 message를 관리함
		msg = ResourceBundle.getBundle(MessagesBundle, lang);
		int index = 0;
		String _prev_action = msg.getString(prev_action);
		prev_actions = _prev_action.split(delimieter);
		
		for(int i = 0 ; i < prev_actions.length ; i++) {
			indexes.put(new Integer(i), prev_actions[i]);
		}
	}
	
	public String[] getPrevActions() {
		return this.prev_actions;
	}
	
	private void init() {
	}

	/**
	 * 메세지로서의 json를 구성하는데 필요한 key 값들
	 */
	public static final String inputKey				= "input";
	public static final String titleKey				= "title";
	public static final String agentStateKey		= "agent_state";
	public static final String actionHistoryKey		= "action_history";
	public static final String beliefStateKey		= "belief_state";
	public static final String domainKey			= "domain";
	public static final String prevActionKey		= "prev_action";
	public static final String rejectCountKey		= "reject_count";
	
	public static final String questionsKey			= "questions";
	
	private int rejectCount = 0;
		
	public JSONObject getTemplate(int index) {
		
		JSONObject master				= new JSONObject();
		JSONObject agentState			= new JSONObject();
			JSONArray	actionHistory	= new JSONArray();
			JSONObject	beliefState		= new JSONObject();
			JSONArray	prevAction		= new JSONArray();
		
		String title					=  msg.getString(titleKey);
		String domain					=  msg.getString(domainKey);
		
		if(index == 0 ) { // 범죄유형 선택
			master.put(inputKey, title);
			master.put(agentStateKey, agentState);
		}
		else if(index >= 14 ) {
			try {
				for(int i = 0 ; i < index ; i++) {
					beliefState.put(prev_actions[i], "%beliefState[" + i + "]%");
				}			
			}catch(Exception e) {
				
			}
		}else { //(index >= 1 ) { 
			
			master.put(inputKey, title);
			master.put(agentStateKey, agentState);
				// array
				agentState.put(actionHistoryKey, actionHistory);
					for(int i = 0 ; i <=  index ; i++) {
						actionHistory.add(prev_actions[i]);
						//beliefState.put(questions[index-1], "%beliefState[" + i + "]%");
					}
					
				agentState.put(beliefStateKey, beliefState);
					for(int i = 0 ; i < index ; i++) {
						beliefState.put(prev_actions[i], "%beliefState[" + i + "]%");
					}
					
				agentState.put(domainKey, domain);
				
				agentState.put(prevActionKey, prevAction);
						prevAction.add(prev_actions[index]);
				
				agentState.put(rejectCountKey, rejectCount);
			
		}		
		
		return master;
	}
	
	
	
	public String getReply(int index) {
		
		JSONObject master				= new JSONObject();
		JSONObject agentState			= new JSONObject();
			JSONArray actionHistory		= new JSONArray();
			JSONObject beliefState		= new JSONObject();
			JSONArray prevAction		= new JSONArray();
		
		String title					=  msg.getString(titleKey);
		String domain					=  msg.getString(domainKey);
		
		if(index == 0 ) { // 범죄유형 선택
			master.put(inputKey, title);
			master.put(agentStateKey, agentState);
		}
		
		if(index >= 1 ) { 
			
			master.put(inputKey, title);
			master.put(agentStateKey, agentState);
				// array
				agentState.put(actionHistoryKey, actionHistory);
					for(int i = 0 ; i < index ; i++) {
						actionHistory.add(prev_actions[i]);
						//beliefState.put(questions[index-1], "%beliefState[" + i + "]%");
					}
					
				agentState.put(beliefStateKey, beliefState);
					for(int i = 0 ; i < index ; i++) {
						beliefState.put(prev_actions[i], "%beliefState[" + i + "]%");
					}
					
				agentState.put(domainKey, domain);
				
				agentState.put(prevActionKey, prevAction);
						prevAction.add(prev_actions[index-1]);
				
				agentState.put(rejectCountKey, rejectCount);
			
		}
		
		
		return master.toJSONString();
	}
	
	
}
