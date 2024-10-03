package org.jepetto.webchat;

import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.jepetto.util.PropertyReader;
import org.json.simple.JSONObject;

@ClientEndpoint(
    encoders = {ChatMessageEncoder.class},
    decoders = {ChatMessageDecoder.class}
)
public class Broker {

    private Session session;

    public Broker(URI uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to server");
    }

    @OnMessage
    public void onMessage(JSONObject message) {
        //System.out.println("Received message: " + message);
    }

    public void sendMessage(JSONObject message) throws Exception {
        //session.getBasicRemote().sendObject(message);
    }

    @OnClose
    public void onClose() {
        System.out.println("Connection closed");
    }

    static PropertyReader reader = PropertyReader.getInstance();
    
	static String mode				= reader.getProperty("justia.mode");
	static String host				= reader.getProperty("justia.host");
	static String port				= reader.getProperty("justia.port");
	static String wsProtocol		= reader.getProperty("justia.wsProtocol");
	static String httpProtocol		= reader.getProperty("justia.httpProtocol");
	static String webApp			= reader.getProperty("justia.webapp"); 			// "webchat";
	static String chat				= reader.getProperty("justia.chat");			//dial";
	
	static String wsUrl	= wsProtocol	+ "://"	+ host + ":" + port + "/" + webApp + "/" + chat;
	
    public static void main(String[] args) {
        //URI uri = URI.create("ws://localhost:8080/chat");
    	URI uri = URI.create(wsUrl);
        Broker client = new Broker(uri);
        try {
            //client.sendMessage(new ChatMessage("John", "Hello, world!"));
            client.sendMessage(new JSONObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
