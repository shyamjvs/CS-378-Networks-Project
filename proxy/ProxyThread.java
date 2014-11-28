package proxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.StringTokenizer;
// import javax.servlet.http.HttpServletRequest;

public class ProxyThread extends Thread {
	private Socket socket = null;
	//    private HTTPReqHeader request = null;

	private final String USER_AGENT = "Mozilla/5.0";
	private static final int BUFFER_SIZE = 32768;
	// private HttpServletRequest  request = null;
	public ProxyThread(Socket socket) {
		super("ProxyThread");
		this.socket = socket;
	}

	public void run() {
		//get input from user
		//send request to server
		//get response from server
		//send response to user
		System.out.println(socket.getRemoteSocketAddress().toString());

		try {
			DataOutputStream out =
					new DataOutputStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));

			String inputLine, outputLine;
			
			int cnt = 0;
			String urlToCall = "";
			///////////////////////////////////
			//begin get request from client
			while ((inputLine = in.readLine()) != null) {

				try {
					StringTokenizer tok = new StringTokenizer(inputLine);
					tok.nextToken();
				} catch (Exception e) {
					break;
				}
				//parse the first line of the request to find the url
				if (cnt == 0) {
					String[] tokens = inputLine.split(" ");

					if(tokens[1].startsWith("http")||tokens[1].startsWith("https"))
					{
						urlToCall = tokens[1];
					}
					else urlToCall = "https://"+tokens[1];
					//can redirect this to output log
				}

				cnt++;
			}
			
			//end get request from client
			///////////////////////////////////
			
			
			//Adblock check
			if (Adblock.filter(urlToCall)){
//				System.out.println("AdBlocked! " + urlToCall);
				out.close();
				in.close();
				socket.close();
				return;
			}
//			System.out.println("Not blocked :( " + urlToCall);
			
			if(BlockList.isBlocked(socket, urlToCall)){
				System.out.println("=============== SITE IS BLOCKED ==================== ");
				out.close();
				in.close();
				socket.close();
				return;
			}

			BufferedReader rd = null;
			//System.out.println("sending request
			//to real server for url: "
			//        + urlToCall);
			///////////////////////////////////

			/*            	
            	//begin send request to server, get response from server
                URL url = new URL(urlToCall);
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                //not doing HTTP posts
                conn.setDoOutput(false);
                //System.out.println("Type is: "
			//+ conn.getContentType());
                //System.out.println("content length: "
			//+ conn.getContentLength());
                //System.out.println("allowed user interaction: "
			//+ conn.getAllowUserInteraction());
                //System.out.println("content encoding: "
			//+ conn.getContentEncoding());
                //System.out.println("content type: "
			//+ conn.getContentType());

                // Get the response
                InputStream is = null;
                HttpURLConnection huc = (HttpURLConnection)conn;
                if (conn.getContentLength() > 0) {
                    try {
                        is = conn.getInputStream();
                        rd = new BufferedReader(new InputStreamReader(is));
                    } catch (IOException ioe) {
                        System.out.println(
				"********* IO EXCEPTION **********: " + ioe);
                    }
                }
                //end send request to server, get response from server
                ///////////////////////////////////
			 */

			if (ProxyServer.cache.Exists(urlToCall) && !urlToCall.endsWith("css") //&& !urlToCall.endsWith("jpg")) {
					){
				byte[] temp = ProxyServer.cache.Get(urlToCall);
				out.write(temp, 0, temp.length);
				out.flush();
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (socket != null) {
					socket.close();
				}
			}
			else{
				try {

					//URL obj = new URL("https://www.google.com/");
					URL obj = new URL(urlToCall);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();


					con.setRequestMethod("GET");
					con.setRequestProperty("User-Agent", USER_AGENT);

					int responseCode = con.getResponseCode();

					InputStream is = con.getInputStream();

					byte by[] = new byte[ BUFFER_SIZE ];
					int index = is.read( by, 0, BUFFER_SIZE );

					byte temp[] = new byte[ 10000000 ];


					int ctr = 0;

					//				String ConnectResponse = "HTTPS/1.1 200 Connection established\n" +
					//						"Proxy-agent: Netscape-Proxy/1.1\n" +
					//						"\r\n";
					//					
					//					out.write(ConnectResponse.getBytes(),0,ConnectResponse.getBytes().length);
					//					out.flush();


					while ( index != -1 )
					{
						
						out.write(by,0,index);
//						System.out.println("------------------------------------------------------");
						
						for(int i=0 ;i<index;i++)
							temp[ctr++] = by[i];
						index = is.read( by, 0, BUFFER_SIZE );
					}
					
					byte temp2[] = new byte[ctr];
					for(int i=0; i<ctr; i++)
						temp2[i] = temp[i];
					
					ProxyServer.cache.Insert(urlToCall, temp2);
					
					
//					StringBuilder sb = new StringBuilder();
//					Iterator<Entry<String, byte[]>> iter = ProxyServer.cache.entrySet().iterator();
//					while (iter.hasNext()) {
//						Entry<String, byte[]> entry = iter.next();
//						sb.append(entry.getKey());
//						sb.append('=').append('"');
//						sb.append(entry.getValue());
//						sb.append('"');
//						if (iter.hasNext()) {
//							sb.append(',').append(' ');
//						}
//					}
//					System.out.println("i = " + ProxyServer.count + sb.toString());

					ctr = 0;
					out.flush();
					out.close();

					//end send response to client
					///////////////////////////////////

				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				//close out all resources




				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (socket != null) {
					socket.close();
				}
			}
		} 

		catch (IOException e) {
			e.printStackTrace();
		}        		


	}	
}