package demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class SystemWebSocketHandler implements WebSocketHandler {
	public static Map<String,WebSocketSession> SocketUser;

	static {
		SocketUser = new HashMap<String,WebSocketSession>();
	}

	/**
	 * 拦截器拦截第一次连接后会调用这个方法 业务实现：登录后，将用户存入缓存
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		Map<String, Object> param = session.getAttributes();
		String loginKey = param.get(WebSocketInterceptor.KEY).toString();
		SocketUser.put(loginKey, session);
	}

	/**
     * 连接的用户，通过这个方法发送消息会调用这个方法 
     */
	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		//页面发送的消息，会传到该方法中。
		System.out.println("get message:"+message);
		//回传给页面
		session.sendMessage(new TextMessage(message.getPayload()+" received at server"));
		
		new Thread(new Runnable() {		
			@Override
			public void run() {
				try {
					int i=10;
					while(i-->0){
						Thread.sleep(3000);
						session.sendMessage(new TextMessage("auto when i>0:"+i));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();;
	}

	/**
     * 心跳检测发现连接不上会调用这个方法，之后会调用连接关闭的方法
     * 业务实现：删除session
     */
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		Map<String, Object> param = session.getAttributes();
    	String loginKey = param.get(WebSocketInterceptor.KEY).toString();
    	SocketUser.remove(loginKey, session);
	}

	/**
     * 关闭连接会调用这个方法 ,这个关闭会由两个地方触发，一个是浏览器主动退出，还有一个是心跳检测发现连接不上也会调用这个方法。
     * 业务实现：发送XX退出消息
     */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("afterConnectionClosed");
	}

	@Override
	public boolean supportsPartialMessages() {
		// TODO Auto-generated method stub
		return false;
	}

}
