package proxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import javax.net.ssl.*;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.net.ssl.HttpsURLConnection;

public class ProxyThread extends Thread {
	private Socket socket = null;
	//    private HTTPReqHeader request = null;

	private final String USER_AGENT = "Mozilla/5.0";
	private static final int BUFFER_SIZE = 32768;
	private HttpServletRequest  request = null;
	public ProxyThread(Socket socket) {
		super("ProxyThread");
		this.socket = socket;
	}

	public void run() {
		//get input from user
		//send request to server
		//get response from server
		//send response to user

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


				System.out.println("Jha.2 " + inputLine);
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
						System.out.println("OHH " + tokens[1]);
						urlToCall = tokens[1];
					}
					else urlToCall = "https://"+tokens[1];
					//can redirect this to output log
					System.out.println("Request for : " + urlToCall);
				}

				cnt++;
			}
			//end get request from client
			///////////////////////////////////


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



			try {

				//URL obj = new URL("https://www.google.com/");
				URL obj = new URL(urlToCall);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();


				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", USER_AGENT);

				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'GET' request to URL : " + urlToCall);
				System.out.println("Response Code : " + responseCode);


				InputStream is = con.getInputStream();
				System.out.println("Response Code : " + responseCode);


				byte by[] = new byte[ BUFFER_SIZE ];
				int index = is.read( by, 0, BUFFER_SIZE );
				System.out.println("Response Code3 : " + responseCode);

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

						//                	System.out.println("index = " + index);

						out.write(by,0,index);

						for(int i=0 ;i<index;i++)

							temp[ctr++] = by[i];

						//                	out.write( by, 0, index );

						index = is.read( by, 0, BUFFER_SIZE );
						//                  System.out.println("index afer read = " + index);
					}
					System.out.println("ctr = " + ctr);
					ctr = 0;
					System.out.println("Response Code : " + responseCode);

					out.flush();

					System.out.println("Finish");

					out.close();

					
					System.out.println("Finish");
					//end send response to client
					///////////////////////////////////


				

			}
			catch(Exception e)
			{

				e.printStackTrace();

				urlToCall = "http:"+urlToCall.substring(5);	
				System.out.println("ERRYRYR " + urlToCall);
				URL obj = new URL(urlToCall);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();


				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", USER_AGENT);

				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'GET' request to URL : " + urlToCall);
				System.out.println("Response Code : " + responseCode);


				InputStream is = con.getInputStream();

				byte by[] = new byte[ BUFFER_SIZE ];
				int index = is.read( by, 0, BUFFER_SIZE );
				while ( index != -1 )
				{
					out.write( by, 0, index );
					index = is.read( by, 0, BUFFER_SIZE );
				}
				out.flush();

				out.close();
				//end send response to client
				///////////////////////////////////
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
		
		catch (IOException e) {
			e.printStackTrace();
		}        		


	}	
}