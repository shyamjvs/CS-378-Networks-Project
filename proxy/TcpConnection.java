package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpConnection extends Thread {

	Socket i;
	Socket o;
	String urlToCall;
	InputStream in;
	OutputStream out;
	String path;
	String post=null;
	
	// Constructor for this class
	public TcpConnection(String path1,Socket in1,Socket out1, String url) {

		super("TcpConnection");
		urlToCall = url;
		path = path1;
		
		try {
			i = in1;
			o = out1;
			in = in1.getInputStream();
			out = out1.getOutputStream();
		} 
		catch (Exception e) {
			System.err.format("TcpConnection: Unable to obtain I/O streams");
			e.printStackTrace();
		}
	}

	// This method runs when the thread is started
	public void run() {

		try {

			byte[] by = new byte[1];
			int index = -1;

			if (!i.isClosed()) 
				index = in.read(by, 0, 1 );

			while ( index != -1 ){
				out.write(by,0,1);
				if (!i.isClosed())
					index = in.read( by, 0, 1 );
				else
					index = -1;
			}

			in.close();
			out.close();
			i.close();
			o.close();
			
		}
		catch (IOException e) {
			System.err.format("TcpConnection: Error while reading -> writing webpage data");
			e.printStackTrace();
		}	
	}


}