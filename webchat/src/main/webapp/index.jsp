<%@page import="org.jepetto.webchat.EchoEndpoint"%>
<%@page import="org.jepetto.util.PropertyReader" %>
<%@page import="java.util.Locale" %>
<%@page import="java.util.ResourceBundle" %>
<%@page import="org.jepetto.webchat.MessageBroker" %>
<%@page import="org.jepetto.webchat.ChatMessageDecoder" %>

<%@page contentType="text/html; charset=utf-8" %>

<%
	/** 입장한 room 구분값 */
	String roomName = request.getParameter("roomName");
	/** 대화ID */
	String id		= request.getParameter("id");
	
	String locale	= request.getParameter("locale");
	
	//ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", Locale.ENGLISH);
	ResourceBundle messages = null;
	try{
		if(locale.equalsIgnoreCase("ko")){
	messages = ResourceBundle.getBundle("MessagesBundle", Locale.KOREAN);
	locale = Locale.KOREAN.getDisplayName();
		}else{
	messages = ResourceBundle.getBundle("MessagesBundle", Locale.ENGLISH);
	locale = Locale.ENGLISH.getDisplayName();
		}
	}catch(NullPointerException e){
		messages = ResourceBundle.getBundle("MessagesBundle", Locale.ENGLISH);
		locale = Locale.KOREAN.getDisplayName();
	}

	PropertyReader reader	= PropertyReader.getInstance();
	String mode				= reader.getProperty("justia.mode");
	String host				= reader.getProperty("justia.host");
	String port				= reader.getProperty("justia.port");
	String wsProtocol		= reader.getProperty("justia.wsProtocol");
	String httpProtocol		= reader.getProperty("justia.httpProtocol");
	String webApp			= reader.getProperty("justia.webapp"); 			// "webchat";
	String chat				= reader.getProperty("justia.chat");			//dial";
	
	/** websockehttpt 접속 주소 */
	String url		= httpProtocol	+ "://"	+ host + ":" + port + "/" + webApp ;
	String wsUrl	= wsProtocol	+ "://"	+ host + ":" + port + "/" + webApp + "/" + chat + "/"+roomName;	
	
	int index = 0;
	String prev_action = messages.getString(MessageBroker.prev_action);
	String prev_actions[] = prev_action.split(MessageBroker.delimieter);
	
	String question = messages.getString(MessageBroker.questionsKey);
	String questions[] = question.split(MessageBroker.delimieter);

	
%>
<!DOCTYPE html>
<html lang="utf-8">

<head>
	<meta charset="UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0" />
	<title>Chat...</title>
</head>
<script>
	
	const roomName	= "<%=roomName%>";
	const delim		= "<%=EchoEndpoint.delim%>";
	const wsUrl		= "<%=wsUrl%>";
	const id		= "<%=id%>";
	//const webSocket = new WebSocket(wsUrl+roomName);	
	const webSocket = new WebSocket(wsUrl);

	webSocket.onopen = function () {
		document.getElementById("dailog").innerHTML = "<%=messages.getString("greeting")%>" + "<br>";
	};

	
	let prev_actions = new Array(<%=prev_actions.length%>);
	<%
	for( int i = 0 ; i < prev_actions.length ; i++){ %>
		prev_actions[<%=i%>] = "<%=prev_actions[i]%>";
	<%
	}
	%>
	
	let questions = new Array(<%=questions.length%>);
	<%
	for( int i = 0 ; i < prev_actions.length ; i++){ %>
		questions[<%=i%>] = "<%=questions[i]%>";
	<%
	}
	%>
	
	/**
	*
	* chatbot 서버로 부터 메세지를 받았을 때 호출되는 callback method
	* 사전에 정의되었던 질의가 대한 응답이 다 종료되었을 때 파일업로드, 서명, 결제, 공유 기능이 활성화되어야 함.
	* 'pdf전환' 이라는 button 하나를 더 추가시킴. 질의/응답이 종료 후에 역시 활성화. 클릭 시 우측 rich text area 부분에 서명을 추가한 pdf를 다운로드 해 줌.
	* Facebook 회원가입 추가. 사용자 이름을 facebook으로 부터 가져옴
	*
	*/
	
	webSocket.onmessage = function (event) {
		
		//dialog 초기화
		document.getElementById("dailog").innerHTML = "==================================================================" +"<br>";
		
		// json 형식의 수신 메세지 
		str = event.data;
		
		try{
			// parsing을 위해 json 객체로 변환
			const talk = JSON.parse(str);
			
			// backend에서 에러가 발생할 경우
			if(talk.error != null){
				alert("<%=messages.getString("error.500")%>");
				return;
			}

			console.log(str);
			
			// chatbot으로부터 받은 json parsing
			
			reply = talk.input; 								// 사용자 답변
			action_history = talk.agent_state.action_history; 	// 질의 리스트
			domain = talk.agent_state.domain;					// 고소장 영역(예: 성범죄)
			prev_action = "";
			try{
				prev_action = talk.agent_state.prev_action[0];		// 현재의 질의
			}catch(e){
				prev_action = talk.agent_state.prev_action;
			}
			reject_count =talk.agent_state.reject_count;		// 응답을 이해하지 못 한 횟수
			
			/*		
			* 응답 json을 parsing 할 때 참조할 것
			console.log(action_history);
			console.log(action_history.length);
			console.log(domain);
			console.log(prev_action);
			console.log(reject_count);
			console.log(talk.agent_state.belief_state.장소);
			//*/
			
			for( i = 0 ; i < action_history.length ; i++){
				key = action_history[i];
				value = talk.agent_state.belief_state[key];
				str = key + " : " + value;
				document.getElementById("dailog").innerHTML += str + "<br>";	
			}
			document.getElementById("dailog").innerHTML += "domain : " + domain + "<br>";
			document.getElementById("dailog").innerHTML += "prev_action :" + prev_action + "<br>";

			
		}catch(e){
			console.log(e);
			document.getElementById("dailog").innerHTML += str + "<br>";
		}
		
		// 다음 질의를 위한 index 초기화
		init();
	};

	/**
	* 서버에 종료 또는 기타의 이유를 접속이 종료되었을 때
	*/
	webSocket.onclose = function () {
		document.getElementById("dailog").innerHTML = "<%=messages.getString("closing")%>"; //"세션이 종료되었습니다.";
	};

	/**
	* 서버에 에러 발생 시
	*/
	webSocket.onerror = function (error) {
		document.getElementById("error").innerHTML = error;
	};
	
	// 질의 순서
	var idx = 0;
	
	/**
	* 메세지 보낼 때, 호출할 메소드
	*
	*/
	function sendMessage() {
		
		const mode		= document.getElementById("delator").value;
		// facebook 로그인 정보로 부터 가져옴
		const username	= document.getElementById("username").value;
		const index		= document.getElementById("index").value;
		// 고소인이 입력하는 답글
		reply			= document.getElementById("reply").value;
		
		// reply == "" 은 최초 고소장 domain을 선택할 때 임
		if(reply == ""){
			reply = "성매매 피해 사기";
		}
		
		const talk = {
			"mode"		: mode				,
			"id"		: username			,
			"index" 	: index.toString()	, 
			"reply" 	: reply				,
			// browser의 default 언어. 영/한으로 번환하면서 테스트
			//"locale"	: navigator.language
			"locale"	: "en"
		}
		//index++;
		webSocket.send(JSON.stringify(talk));
		document.getElementById("reply").value = "";
		idx++;
		
	}
	
	function init(){
		document.getElementById("question").value	= questions[idx];
		document.getElementById("index").value		= idx;
	}
</script>

<body onload="init()">


<script async defer crossorigin="anonymous" src="https://connect.facebook.net/en_US/sdk.js"></script>	

	<div>
		<table>
			<tr>
				<td>locale : </td><td><input type="text" id="locale" value="<%=locale %>" /></td>
			</tr>		
			<tr>
				<td>mode : </td><td><input type="text" id="delator" value="delator" /></td>
			</tr>
			<tr>
				<td>index : </td><td><input type="text" id="index" value="" /></td>
			</tr>
			<tr>
				<td>username : </td><td><input type="text" id="username" value="augxmas" /></td>
			</tr>
			<tr>
				<td>question :</td><td><input type="text" id="question" value="" /></td>
			</tr>
			<tr>
				<td>reply : </td><td><input type="text" id="reply" /></td>
			</tr>		
			<tr>
				<td></td><td><button type="button" onclick="sendMessage()">send</button></td>
			</tr>		
		</table>
	</div>
	<div id="dailog"></div>
	<div id="close"></div>
	<div id="error"></div>
</body>

</html>