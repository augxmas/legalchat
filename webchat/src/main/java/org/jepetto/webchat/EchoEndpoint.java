package org.jepetto.webchat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.NamingException;
import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jepetto.bean.Facade;
import org.jepetto.logger.DisneyLogger;
import org.jepetto.proxy.HomeProxy;
import org.jepetto.sql.XmlConnection;
import org.jepetto.sql.XmlTransfer;
import org.jepetto.util.PropertyReader;



@ServerEndpoint("/chat/{room-name}")
public class EchoEndpoint { 

	public static final String delim = ":";
	public static final String chatRoom = "chatRoom";
	
	static PropertyReader reader = PropertyReader.getInstance();
	DisneyLogger cat = new DisneyLogger(EchoEndpoint.class.getName());
	HomeProxy proxy = HomeProxy.getInstance();
	Facade remote = proxy.getFacade();
	HashMap<String,String> dummy = new HashMap<String,String>();
	
	public static final String dataSource		= reader.getProperty("moneyclub_mysql_datasource");
	public static final String QUERY_FILE		= reader.getProperty("moneyclub_query_file");
	public static final String xpath			= "//recordset/row";
	public static final String saveMsgKey		= "saveMsg";
	public static final String getMsgKey		= "getMsg";
	
	//public static final String roomTitle		= "stock";
	
	@OnOpen
	public void open(Session session, EndpointConfig config, @PathParam("room-name") String roomName) {
		String arr[] = new String[] {roomName};
		//System.out.println(roomName);
		try {
			//dummy.put("dummy", "");
			Document doc = remote.executeQuery(dataSource, QUERY_FILE, getMsgKey,dummy,arr);
			Connection con = new XmlConnection(doc);
			XmlTransfer transfer = new XmlTransfer();
			System.out.println(transfer.transferDom2String(doc));
			PreparedStatement stmt = con.prepareStatement(xpath);
			ResultSet rset = stmt.executeQuery();
			String proj = null;
			String user = null;
			String msg = null;
			String date = null;
			while(rset.next()) {
				proj	= rset.getString("proj");
				user	= rset.getString("user");
				msg		= rset.getString("msg");
				date	= rset.getString("date");
				System.out.println(user+delim+msg+"/"+date);
				session.getBasicRemote().sendText(chatRoom+delim+user+delim+msg);
			}
			session.getBasicRemote().sendText(chatRoom+delim+" "+delim+date.replaceAll(":","."));
		} catch (SQLException | NamingException | JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(java.lang.IllegalArgumentException e) {
			e.printStackTrace();
		}catch(java.lang.NullPointerException e) {
			e.printStackTrace();
		}
		session.getUserProperties().put(chatRoom,roomName);
	}


	@OnMessage
	public void message(Session session, String msg) {
		
		String arr[] = msg.split(delim);
		session.getUserProperties().put(chatRoom, arr[0]);
		System.out.println(msg);
		//String _arr[] = (roomTitle+delim+msg).split(delim);
		
		try {
			int i = remote.executeUpdate(dataSource, QUERY_FILE, saveMsgKey, dummy, arr);
		} catch (SQLException | NamingException | JDOMException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		try {
			for (Session sess : session.getOpenSessions()) {
				if (sess.isOpen() && sess.getUserProperties().get(chatRoom).equals(arr[0])) {
					sess.getBasicRemote().sendText(msg);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}

	}
	
	

	@OnClose
	public void close(Session session, CloseReason closeReason) {	
		try {
			session.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnError
	public void error(Session session, Throwable thr) {
		thr.printStackTrace();
	}

}
