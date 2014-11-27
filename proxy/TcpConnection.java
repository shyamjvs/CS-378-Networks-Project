package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpConnection extends Thread{
	

		
		Socket i;
		Socket o;
		
		InputStream in;
		OutputStream out;
		String path;
		String post=null;
		public TcpConnection(String path1,Socket in1,Socket out1) {
			
			super("TcpConnection");
			System.out.println("YUPZ");
			path = path1;
			try 
			{
				i = in1;
				o = out1;
				in = in1.getInputStream();
				out = out1.getOutputStream();
				
				if(i.isClosed() || o.isClosed())System.out.println("FUCK");
				else System.out.println("DoubleFUCK");
					
			
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		public void run() {
		
			try 
			{
				if (i.isClosed()) System.out.println("YUCK1");
				else if (o.isClosed())System.out.println("YUCK2");
				else System.out.println("DoubleYUCK");
			
				byte[] by = new byte[1];
				
				int index = -1;
				
				if (!i.isClosed()) 
				index = in.read(by, 0, 1 );
		
				while ( index != -1 )
				{
			//		System.out.println("byte " + path + " " + by[0]);
					
					out.write(by,0,1);
					if (!i.isClosed())
					index = in.read( by, 0, 1 );
					else
					{
						index = -1;
					}	
				}
				
				}
				catch (IOException e) 
				{
						// TODO Auto-generated catch block
						e.printStackTrace();
				}	
		}


	}

