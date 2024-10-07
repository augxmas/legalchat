package org.jepetto.webchat;

import java.io.IOException;
import java.util.Map;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.Logger;
import org.jepetto.util.PropertyReader;
import org.json.simple.JSONObject;

@ServerEndpoint(
	value = "/dial/{room-name}",
	encoders = {ChatMessageEncoder.class},
	decoders = {ChatMessageDecoder.class}
)
public class JustiaEndpoint {

	private static final Logger logger		= Logger.getLogger("JustiaEndPoint");
	public	static final String delim		= ":";
	public	static final String chatRoom	= "chatRoom";

	static PropertyReader reader = PropertyReader.getInstance();


	@OnOpen
	public void open(Session session, EndpointConfig config, @PathParam("room-name") String roomName) {
		// managed state
		Map<String,Object> user = config.getUserProperties();
		user.put("sessionid", session.getId());
		/*
		try {
			session.getBasicRemote().sendText("Welcome !!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//*/
	}

	@OnMessage
	public void message(Session session, JSONObject json) {
		String msg = json.toJSONString();
		//String error = (String)json.get(ChatMessageDecoder.error);
		try {
			session.getBasicRemote().sendText(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnClose
	public void close(Session session, CloseReason closeReason) {
	}

	@OnError
	public void error(Session session, Throwable thr) {
		thr.printStackTrace();
	}


}
