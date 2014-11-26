
package proxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ProxyThread extends Thread {
	private Socket socket = null;
	private static final int BUFFER_SIZE = 32768;
	public ProxyThread(Socket socket) {
		super("ProxyThread");
		this.socket = socket;
	}

	public void run() {
		ProxyServer.counter ++;
		System.out.println("value of counter is " + Integer.toString(ProxyServer.counter));
		//get input from user
		//send request to server
		//get response from server
		//send response to user
		
		try {
			DataOutputStream out =
					new DataOutputStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));


			String inputLine;
			int cnt = 0;
			String urlToCall = "";
			///////////////////////////////////
			//begin get request from client
			while ((inputLine = in.readLine()) != null) {

				//parse the first line of the request to find the url
				if (cnt == 0) {
					String[] tokens = inputLine.split(" ");
					System.out.println(tokens);
					urlToCall = tokens[1];
					//can redirect this to output log
					System.out.println("Request for : " + urlToCall);
				}

				cnt++;
			}
			//end get request from client
			///////////////////////////////////

			if (ProxyServer.cache.containsKey(urlToCall)) {
				System.out.println("in cache");
				InputStream is = ProxyServer.cache.get(urlToCall);
				byte by[] = new byte[ BUFFER_SIZE ];
				int index = is.read( by, 0, BUFFER_SIZE );
				while ( index != -1 )
				{
					out.write( by, 0, index );
					index = is.read( by, 0, BUFFER_SIZE );
				}
				System.out.println(out);
				out.flush();
			}


			else{
				try {

					System.out.println("yyoy 2");
					System.out.println("sending request to real server for url: "+ urlToCall);
					///////////////////////////////////
					//begin send request to server, get response from server
					URL url = new URL(urlToCall);
					URLConnection conn = url.openConnection();
					conn.setDoInput(true);
					//not doing HTTP posts
					conn.setDoOutput(false);

					System.out.println("Type is: "
							+ conn.getContentType());
					System.out.println("content length: "
							+ conn.getContentLength());
					System.out.println("allowed user interaction: "
							+ conn.getAllowUserInteraction());
					System.out.println("content encoding: "
							+ conn.getContentEncoding());
					System.out.println("content type: "
							+ conn.getContentType());

					// Get the response

					InputStream is = null;
					//                HttpURLConnection huc = (HttpURLConnection)conn;
					if (conn.getContentLength() > 0) {
						try {
							is = conn.getInputStream();
							System.out.println("_______________________PRINTING INPUTSTREAM___________________");

							//                        rd = new BufferedReader(new InputStreamReader(is));
//							System.out.printl??n(rd);
						} catch (IOException ioe) {
							System.out.println(
									"********* IO EXCEPTION **********: " + ioe);
						}
					}
					//end send request to server, get response from server
					///////////////////////////////////

					///////////////////////////////////
					//begin send response to client

					ProxyServer.cache.put(urlToCall, is);
					byte by[] = new byte[ BUFFER_SIZE ];
					int index = is.read( by, 0, BUFFER_SIZE );
					while ( index != -1 )
					{
						out.write( by, 0, index );
						index = is.read( by, 0, BUFFER_SIZE );
					}
					System.out.println(out);
					out.flush();

					//end send response to client
					///////////////////////////////////
				} catch (Exception e) {
					//can redirect this to error log

					System.err.println("Encountered exception: " + e);
					//encountered error - just send nothing back, so
					//processing can continue
					out.writeBytes("");
				}
			}

			//close out all resources
//			if (rd != null) {
//				rd.close();
//			}
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (socket != null) {
				socket.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}