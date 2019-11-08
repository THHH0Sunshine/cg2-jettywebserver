package sunshine.cg2.jettyserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebGameServer extends Server {

	public WebGameServer(int port)
	{
		super(port);
		ContextHandler chandler=new ContextHandler();
		chandler.setContextPath("/websocket");
		chandler.setHandler(new WebSocketHandler()
			{
				@Override public void configure(WebSocketServletFactory fact)
				{
					fact.setCreator(new GameRoom());
				}
			});
		HandlerList handlers=new HandlerList();
		handlers.setHandlers(new Handler[]{chandler,new DefaultHandler()});
		setHandler(handlers);
	}
	
	public static final int DEFAULT_PORT=7788;
	
	public static void main(String[] args) throws Exception
	{
		new WebGameServer(DEFAULT_PORT).start();
	}
}
