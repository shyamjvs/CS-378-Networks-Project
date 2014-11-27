package proxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

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

			InputStream in1 = socket.getInputStream();
			
			BufferedReader in = new BufferedReader(
					new InputStreamReader(in1));

			String inputLine, outputLine;
			int cnt = 0;
			String urlToCall = "";
			///////////////////////////////////
			//begin get request from client

			String input = "";

			while ((inputLine = in.readLine()) != null) {


				input += inputLine + "\r\n";
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
			System.out.println("################################URL : " + urlToCall);
			//end get request from client
			///////////////////////////////////


			BufferedReader rd = null;

			String user = socket.getLocalSocketAddress().toString(); 


			try {

				
				System.out.println("I\n"+input);
				
				  Socket clientSocket = new Socket(urlToCall.split(":")[1].substring(2), 443);  
				  
				  OutputStream outToServer = clientSocket.getOutputStream();  

				  InputStream inFromServer = clientSocket.getInputStream();  

				  
					out.write(("HTTP/1.1 200 OK\r\n" + 
							"Content-Type: text/xml; charset=utf-8\r\n" + 
							"Content-Length: length\r\n" + 
							"\r\n").getBytes());
					
					
					System.out.println("Connected To " + clientSocket.getRemoteSocketAddress());
					System.out.println("Connected To " + socket.getRemoteSocketAddress());
					
					if(!socket.isClosed() && !clientSocket.isClosed())System.out.println("Mission is a go");
					

					
					TcpConnection clientToServer = new TcpConnection("C2S",socket,clientSocket);
					TcpConnection serverToClient = new TcpConnection("S2C",clientSocket,socket);

					if(!socket.isClosed() && !clientSocket.isClosed())System.out.println("Mission is a go now");
					
					clientToServer.start();
					serverToClient.start();

			}
			catch(Exception e)
			{

				e.printStackTrace();

				urlToCall = "http:"+urlToCall.substring(5);	
				System.out.println("ERRYRYR " + urlToCall);


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



				while ( index != -1 )
				{

					for(int i=0 ;i<index;i++)

						temp[ctr++] = by[i];
					index = is.read( by, 0, BUFFER_SIZE );

				}

				System.out.println("ctr = " + ctr);
				System.out.println("Response Code : " + responseCode);


				if(data.Exceptions.containsKey(user))
				{
					String[] exc = data.Exceptions.get(user);


					boolean found = false;
					for(String str : exc)
					{
						if(str.equals(urlToCall));
						found = true;
					}	

					if(found)
					{
						out.write(temp,0,ctr);		
						out.flush();
						out.close();
						System.out.println("Finish");
					}	


				}



				String output =  Run.nb.predict(temp.toString());

				//	if(output.equals("Pure"))
				{
					out.write(temp,0,ctr);		
					out.flush();							
					out.close();
					System.out.println("Finish");
				}	
			}



			//close out all resources

		} 

		catch (IOException e) {
			e.printStackTrace();
		}        		


	}	
}