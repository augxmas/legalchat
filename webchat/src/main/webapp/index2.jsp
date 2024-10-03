<%@page import="org.jepetto.webchat.EchoEndpoint"%>
<%@page import="org.jepetto.util.PropertyReader" %>
<%@page import="java.util.Locale" %>
<%@page import="java.util.ResourceBundle" %>
<%@page contentType="text/html; charset=utf-8" %>

<%
	/** 입장한 room 구분값 */
	String roomName = request.getParameter("roomName");
	/** 대화ID */
	String id		= request.getParameter("id");
	
	ResourceBundle messages = ResourceBundle.getBundle("MessagesBundle", Locale.ENGLISH);
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

	
	webSocket.onmessage = function (event) {
		
		// roomName:id:message
		str = event.data;
		
		try{
			const talk = JSON.parse(str);
			
			reply = talk.input; 								// 사용자 답변
			action_history = talk.agent_state.action_history; 	// 질의 리스트
			domain = talk.agent_state.domain;					// 고소장 영역(예: 성범죄)
			prev_action = talk.agent_state.prev_action[0];		// 현재의 질의
			reject_count =talk.agent_state.reject_count;		// 응답을 이해하지 못 한 횟수
			
			/*		
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
			
		}catch(e){
			console.log(e);
			document.getElementById("dailog").innerHTML += str + "<br>";
		}
	};

	
	webSocket.onclose = function () {
		document.getElementById("dailog").innerHTML = "<%=messages.getString("closing")%>"; //"세션이 종료되었습니다.";
	};

	
	webSocket.onerror = function (error) {
		document.getElementById("error").innerHTML = error;
	};

	function sendMessage() {
		const message = document.getElementById("message").value;
		
		const talk = {
			"input" : message,
		    "agent_state": {
		        "action_history": [
		            "범죄 수법",
		            "피고소인 신분",
		            "고소인 신분",
		            "피고소인을 알게된 경위",
		            "날짜",
		            "장소",
		            "거래 방법",
		            "거짓말의 내용",
		            "재산 마련 방법",
		            "재산의 처분 방법",
		            "거짓임을 깨닫게 된 계기",
		            "다른 피해사실",
		            "고소 이유"
		        ],
		        "belief_state": {
		            "거래 방법": "10만원을 보내주면 우리집에 와서 서비스 해주기러 했어요",
		            "거짓말의 내용": "돈을 먼저 보내주면 집으로 온대요",
		            "거짓임을 깨닫게 된 계기": "돈을 줘도 안왔어요",
		            "고소 이유": "벌받게 하고싶어요",
		            "고소인 신분": "대학생",
		            "날짜": "2024-9-12-",
		            "다른 피해사실": "아니요",
		            "범죄 수법": "성매매 유도 사기",
		            "장소": "우리집입니다",
		            "재산 마련 방법": "아르바이트",
		            "재산의 처분 방법": "계좌이체",
		            "피고소인 신분": "대학생",
		            "피고소인을 알게된 경위": "카카오톡에서 만났습니다"
		        },
		        "domain": "성매매 유도 사기",
		        "prev_action": [
		            "다른 민형사"
		        ],
		        "reject_count": 0
		    }
		}
		
		//webSocket.send(roomName+delim+id+delim+message);
		webSocket.send(JSON.stringify(talk));
		document.getElementById("message").value="";
	}
</script>

<body>


<script async defer crossorigin="anonymous" src="https://connect.facebook.net/en_US/sdk.js"></script>	

	<div>
		username : <input type="text" id="username" value="James" />
		inquiry : <input type="text" id="username" value="James" />		
		reply : <input type="text" id="message" />
		<button type="button" onclick="sendMessage()">send</button>
	</div>
	<div id="dailog"></div>
	<div id="close"></div>
	<div id="error"></div>
</body>

</html>