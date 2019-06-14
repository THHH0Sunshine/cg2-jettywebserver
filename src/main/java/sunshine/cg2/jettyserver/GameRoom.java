package sunshine.cg2.jettyserver;

import java.util.Arrays;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WriteCallback;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;

import sunshine.cg2.core.io.Client;
import sunshine.cg2.core.io.Room;

class GameRoom extends Room implements WebSocketCreator
{
	private class GameClient extends WebSocketAdapter implements Client
	{
		private class GameWriteCallback implements WriteCallback
		{
			private static final int ABSTRACT_LENGTH=60;
			
			private final String s;
			
			GameWriteCallback(String s)
			{
				this.s=s.length()<=ABSTRACT_LENGTH?s:(s.substring(0,ABSTRACT_LENGTH)+"...");
			}
			
			@Override
			public void writeFailed(Throwable t)
			{
				System.out.println(name+"failed to send:"+s);
			}
			
			@Override
			public void writeSuccess()
			{
				System.out.println(name+"sent:"+s);
			}
		}
		
		private final String name="["+hashCode()+"]:";
		private boolean inRoom=false;
		
		@Override
		public void send(String s)
		{
			getRemote().sendString(s,new GameWriteCallback(s));
		}
		
		@Override
		public void onWebSocketBinary(byte[] msg,int offset,int len)
		{
			if(isConnected())
			{
				if(msg.length<=0)return;
				byte[] rmsg=Arrays.copyOfRange(msg,offset,offset+len);
				if(!inRoom)
				{
					if(rmsg[0]!=-1)return;
					//join
					if(join(this))
					{
						inRoom=true;
						send("{join:true}");
					}
					else send("{join:false}");
				}
				else
				{
					if(rmsg[0]<0)
					{
						switch(rmsg[0])
						{
						case -2://leave
							send("{leave:false}");
							break;
						case -3://start
							if(!start(this))send("{start:false}");
							break;
						default:
							return;
						}
					}
					else postMessage(this,rmsg);
				}
				System.out.print(name);
				for(byte b:msg)System.out.print(b+",");
				System.out.println(";offset="+offset+",len="+len);
			}
		}
		
		@Override
		public void onWebSocketClose(int statusCode,String reason)
		{
			super.onWebSocketClose(statusCode,reason);
			leave(this);
			System.out.println(name+"Client left. statusCode="+statusCode+",reason="+(reason==null?null:("`"+reason+"`")));
		}
	}
	
	@Override
	public Object createWebSocket(ServletUpgradeRequest req,ServletUpgradeResponse res)
	{
		return new GameClient();
	}
}
