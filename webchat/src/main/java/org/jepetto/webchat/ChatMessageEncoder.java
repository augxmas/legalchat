package org.jepetto.webchat;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.json.simple.JSONObject;


public class ChatMessageEncoder implements Encoder.Text<JSONObject> {

    @Override
    public String encode(JSONObject message) throws EncodeException {
    	System.out.println("encoding................ " + message);
        // Convert the ChatMessage object into a JSON string
        //JSONObject jsonObject = new JSONObject();
        //jsonObject.put("user", message.getUser());
        //jsonObject.put("message", message.getMessage());
        //return jsonObject.toString();
    	return message.toJSONString();
    }

    @Override
    public void init(EndpointConfig config) {
        // Initialization logic if needed
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
