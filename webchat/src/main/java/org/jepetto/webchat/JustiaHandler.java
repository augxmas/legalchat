package org.jepetto.webchat;

import javax.websocket.MessageHandler;

public class JustiaHandler implements MessageHandler.Whole<String> {

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		
		System.out.println("handler..........." + message);
		
	}
	

}
