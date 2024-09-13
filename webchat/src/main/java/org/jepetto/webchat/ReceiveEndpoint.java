package org.jepetto.webchat;

import java.nio.ByteBuffer;

import javax.websocket.OnMessage;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/receive")
public class ReceiveEndpoint {
	   @OnMessage
	   public void textMessage(Session session, String msg) {
	      System.out.println("Text message: " + msg);
	   }
	   @OnMessage
	   public void binaryMessage(Session session, ByteBuffer msg) {
	      System.out.println("Binary message: " + msg.toString());
	   }
	   @OnMessage
	   public void pongMessage(Session session, PongMessage msg) {
	      System.out.println("Pong message: " + 
	                          msg.getApplicationData().toString());
	   }
	
}
