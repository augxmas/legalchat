<%@page import="org.jepetto.webchat.EchoEndpoint"%>
<%@page contentType="text/html; charset=utf-8" %>

<%
/** 입장한 room 구분값 */
String roomName = request.getParameter("roomName");
/** 대화ID */
String id		= request.getParameter("id");
/** 
	mode = dev 개발환경, mode = prod
	개발,운영에 따른 wsurl 변경
*/
String mode		= request.getParameter("mode");
/** websockehttpt 접속 주소 */
String wsUrl = null;
String url = null;
if(mode == null){
	url		= "http://remote.monorama.kr:8000/webchat";
	wsUrl	= "ws://remote.monorama.kr:8000/webchat/chat/";
	//wsUrl	= "ws://moneyclub.monorama.kr:8080/webchat/chat/";
}else if(mode.equals("localhost")){
	url		= "http://localhost:8080/webchat";
	wsUrl	= "ws://localhost:8080/webchat/chat/";	
}else{
	url 	= "http://moneyclub.monorama.kr:8080/webchat";
	wsUrl	= "ws://moneyclub.monorama.kr:8080/webchat/chat/";
}
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
	const webSocket = new WebSocket(wsUrl+roomName);	

	webSocket.onopen = function () {
		alert("chatting 방에 입장하셨습니다");
	};

	
	webSocket.onmessage = function (event) {
		// roomName:id:message
		str = event.data;
		// 메세지를 구분자로 나눔		
		console.log(str);
		words = str.split(delim);
		dailog = document.getElementById("dailog").innerHTML;
		// id:message
		//dailog+="<img src='a.jpg'/>"+words[1]+delim+words[2]+"<br>";
		
		if("<%=id%>" == words[1]){
			words[2] += "오른쪽";
		}
		
		dailog+=words[1]+delim+words[2]+"<br>";
		document.getElementById("dailog").innerHTML = dailog;
	};

	
	webSocket.onclose = function () {
		document.getElementById("close").innerHTML = "<%=id%> 퇴장했습니다.";
	};

	
	webSocket.onerror = function (error) {
		document.getElementById("error").innerHTML = error;
	};

	function sendMessage() {
		const message = document.getElementById("message").value;
		webSocket.send(roomName+delim+id+delim+message);
		document.getElementById("message").value="";
	}
</script>

<body>
user#1 : <a href='<%=url%>/index.jsp?roomName=stock&id=박찬호'>입장하기</a>
user#2 : <a href='<%=url%>/index.jsp?roomName=stock&id=강간찬'>입장하기</a>
	<div>
		<input type="text" id="message" />
		<button type="button" onclick="sendMessage()">send</button>
	</div>
	<div id="dailog"></div>
	<div id="close"></div>
	<div id="error"></div>
</body>

</html>