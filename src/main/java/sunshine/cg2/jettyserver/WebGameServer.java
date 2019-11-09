package sunshine.cg2.jettyserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class WebGameServer extends Server {

	public WebGameServer(int port,String resourceBase)
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
		HandlerList handlerList=new HandlerList();
		Handler[] handlers;
		if(resourceBase==null)handlers=new Handler[]{chandler,new DefaultHandler()};
		else
		{
			ResourceHandler rhandler=new ResourceHandler();
			rhandler.setResourceBase(resourceBase);
			handlers=new Handler[]{chandler,rhandler,new DefaultHandler()};
		}
		handlerList.setHandlers(handlers);
		setHandler(handlerList);
	}
	
	public static final int DEFAULT_PORT=7788;
	
	public static void main(String[] args) throws Exception
	{
		String resourceBase=args.length>0?args[0]:null;
		WebGameServer server=new WebGameServer(DEFAULT_PORT,resourceBase);
		server.addLifeCycleListener(new LifeCycle.Listener()
			{
				public void lifeCycleStarting(LifeCycle event){}
		        public void lifeCycleStarted(LifeCycle event){System.out.println("@server-started");}
		        public void lifeCycleFailure(LifeCycle event,Throwable cause){}
		        public void lifeCycleStopping(LifeCycle event){}
		        public void lifeCycleStopped(LifeCycle event){}
			});
		server.start();
		server.join();
	}
}
