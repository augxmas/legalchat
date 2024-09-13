package org.jepetto.webchat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.NamingException;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jepetto.sql.XmlConnection;
import org.jepetto.sql.XmlTransfer;

import java.util.HashMap;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.jepetto.bean.Facade;
import org.jepetto.logger.DisneyLogger;
import org.jepetto.proxy.HomeProxy;
import org.jepetto.util.PropertyReader;

@ServerEndpoint("/dial/{room-name}")
public class JustiaEndpoint {

	public static final String delim = ":";
	public static final String chatRoom = "chatRoom";

	static PropertyReader reader = PropertyReader.getInstance();
	DisneyLogger cat = new DisneyLogger(JustiaEndpoint.class.getName());
	HomeProxy proxy = HomeProxy.getInstance();
	Facade remote = proxy.getFacade();
	HashMap<String, String> dummy = new HashMap<String, String>();

	public static final String dataSource = reader.getProperty("moneyclub_mysql_datasource");
	public static final String QUERY_FILE = reader.getProperty("moneyclub_query_file");
	public static final String xpath = "//recordset/row";
	public static final String saveMsgKey = "saveMsg";
	public static final String getMsgKey = "getMsg";

	// public static final String roomTitle = "stock";

	@OnOpen
	public void open(Session session, EndpointConfig config, @PathParam("room-name") String roomName) {
		try {
			session.getBasicRemote().sendText("Welcome !!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnMessage
	public void message(Session session, String msg) {
		try {
			for (Session sess : session.getOpenSessions()) {
				if (sess.isOpen()) {
					sess.getBasicRemote().sendText(msg);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
