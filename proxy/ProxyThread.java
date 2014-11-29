package proxy;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sun.misc.BASE64Decoder;


public class ProxyThread extends Thread {


	private final String USER_AGENT = "Mozilla/5.0";
	private Socket socket = null;
	private String client_addr;
	private boolean Https = false;
	private static final int BUFFER_SIZE = 32768;

	private boolean doAuthenticate = false;
	private boolean doBlockIP = false;
	private boolean doAdBlock = false;
	private boolean doContentFilter = false;
	private boolean doSiteBlock = false;

	// Constructor for the class
	public ProxyThread(Socket socket, boolean[] switches) {
		super("ProxyThread");
		this.socket = socket;
		client_addr= socket.getRemoteSocketAddress().toString().split(":")[0].substring(1);

		doAuthenticate = switches[0];
		doBlockIP = switches[1];
		doAdBlock = switches[2];
		doContentFilter = switches[3];
		doSiteBlock = switches[4];
	}

	// This process is invoked when the thread spawns
	@SuppressWarnings("unused")
	public void run() {

		try {

			// Initiate the I/O streams for communication with client
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			InputStream in1 = socket.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(in1));
			String inputLine;

			// Initiate some variables to read the input
			int line_number = 0;
			String urlToCall = "";
			BASE64Decoder decoder = new BASE64Decoder();
			String input = "";

			// Read the input from client line by line
			while ((inputLine = in.readLine()) != null) {

				input += inputLine + "\r\n";

				// Check if Proxy Authentication is required & do it
				if(doAuthenticate && inputLine.startsWith("Proxy-Authorization: Basic")){
					if(ProxyServer.login.contains(new String(decoder.decodeBuffer(inputLine.substring(27).split("\n")[0])))){
						Date dt=new Date();
						ProxyServer.authenticated.put(client_addr,dt.getTime());
					}
				}

				// String tokenize the input line
				try {
					StringTokenizer tok = new StringTokenizer(inputLine);
					tok.nextToken();
				} catch (Exception e) {
					//System.err.format("ProxyThread: Problem in StringTokenizer "+inputLine);
					break;
				}

				// Check on the first line, the type of request 
				try{
					if (line_number == 0) {
						String[] tokens = inputLine.split(" ");				
						if(tokens[0].equals("GET")||tokens[0].equals("POST")){
							Https= false;
							urlToCall = tokens[1].substring(7);
							System.out.println("GET/POST request : "+urlToCall);
						}
						else if(tokens[0].equals("CONNECT")){
							Https = true;
							urlToCall = tokens[1];
							System.out.println("CONNECT request : "+urlToCall);
						}	
						else{	
							urlToCall = tokens[1].substring(7);
							System.out.println("Unknown request type: "+tokens[0]+" to: "+urlToCall);
						}
					}
				}
				catch (Exception e){
					System.err.format("ProxyThread: Index out of bounds while reading tokens[]");
					e.printStackTrace();
				}

				line_number++;
			}

			// Send back a proxy authentication required message to client & close connection 
			Date cur_dt=new Date();
			if(doAuthenticate && (!ProxyServer.authenticated.containsKey(client_addr) || 
					timeout.is_timed_out(ProxyServer.authenticated.get(client_addr).longValue(),cur_dt.getTime()))){
				try{
					out.writeBytes("HTTP/1.1 407 Proxy Authentication Required\r\nProxy-Authenticate: Basic realm=\"proxy\"\r\n");
					out.flush();
					out.close();
					in.close();
					socket.close();
					ProxyServer.buffer.reduce_queue_size();
				}
				catch (Exception e){
					System.err.format("ProxyThread: Error in HTTP 407 Proxy Authentication");
					e.printStackTrace();
				}
				return;
			}
			System.out.println("Crossed proxy authentication part");

			// Check if the request is made by an Adware & block if yes
			if (doAdBlock && Adblock.filter(urlToCall)){
				try{
					System.out.println("=============== AD BLOCKED BY PROXY ==================== ");
					out.close();
					in.close();
					socket.close();
					ProxyServer.buffer.reduce_queue_size();
				}
				catch (Exception e){
					System.err.format("ProxyThread: Error in Ad-Blocking");
					e.printStackTrace();
				}
				return;
			}
			System.out.println("Crossed Ad block part");

			//Check if the request is among user's list of blocked keywords & block if yes
			if(doSiteBlock && BlockList.isBlocked(socket, urlToCall)){
				try{
					System.out.println("=============== SITE IS BLOCKED ======================== ");
					out.close();
					in.close();
					socket.close();
					ProxyServer.buffer.reduce_queue_size();
				}
				catch (Exception e){
					System.err.format("ProxyThread: Error in Site Blocking");
					e.printStackTrace();
				}
				return;
			}
			System.out.println("Crossed Site block part");


			// If request is HTTPS, then go into this block
			if(Https){

				Document doc;
				System.out.println("Entered HTTPS");

				try {

					if(doContentFilter){
						// Check if document returned, contains explicit content using NB trainer
						doc = Jsoup.connect("https://"+urlToCall).get();
						String title = doc.title();
						Elements links = doc.select("a[href]");

						float badCount = 0;
						float count = 0;
						for (Element link : links) {
							count++;
							if(data.nb_link.predict(link.attr("href")).equals("explicitContent")
									|| data.nb_text.predict(link.text()).equals("explicitContent")){
								badCount++;
							}		
						}

						if((badCount/count)>0.3){
							out.writeBytes("HTTP/1.1 403 Forbidden \r\n"+"Connection: close\r\n"+"\r\n"+
									"<h1>Webpage Content not related to your academic needs !</h1>");
							out.flush();
							out.close();
							//in.close();
							//socket.close();
							ProxyServer.buffer.reduce_queue_size();
							return;
						}	
					}

				} 				
				catch (Exception e){
					System.err.format("ProxyThread: Error while using naive bayes on document in HTTPS");
					e.printStackTrace();
				}

				System.out.println("Reached HTTPS second phase");

				// Create a HTTPS socket with the web server
				Socket clientSocket = new Socket(urlToCall.split(":")[0], 443);
				String server_addr = clientSocket.getRemoteSocketAddress().toString().split(":")[0].split("/")[1];

				// If the web server is among the black-list of servers, then block
				if(doBlockIP && BlockedIp.isBlocked(server_addr)){
					try{
						out.writeBytes("HTTP/1.1 403 Forbidden \r\n"+"Connection: close\r\n"+"\r\n"+"<h1>Host is among your blacklist !</h1>");
						out.flush();
						out.close();
						in.close();
						clientSocket.close();
						socket.close();
						ProxyServer.buffer.reduce_queue_size();
					}
					catch (Exception e){
						System.err.format("ProxyThread: Error while IP blocking");
					}
					return;
				}

				// Send HTTP OK message to client
				out.write(("HTTP/1.1 200 OK\r\n" +
						"Content-Type: text/xml; charset=utf-8\r\n" + 
						"Content-Length: length\r\n" + 
						"\r\n").getBytes());
				
				System.out.println("making tunnel for https");
				TcpConnection clientToServer = new TcpConnection("C2S", socket,clientSocket,urlToCall);
				TcpConnection serverToClient = new TcpConnection("S2C", clientSocket,socket,urlToCall);

				clientToServer.start();
				serverToClient.start();
				
				//out.close();
				//in.close();
				//clientSocket.close();
				//socket.close();
				System.out.println("ProxyThread: Closed the HTTPS connection");
			}

			// Else the request is HTTP, go to this block
			else{

				System.out.println("Request is http");
				urlToCall = "http://" + urlToCall;
				BufferedReader rd = null;

				URL obj = new URL(urlToCall);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
				

				con.setRequestMethod("GET");
				con.setRequestProperty("User-Agent", USER_AGENT);

				int responseCode = con.getResponseCode();
				
				System.out.println("done making connection");
				
				System.out.println("\nSending 'GET' request to URL : " + urlToCall);
				System.out.println("Response Code : " + responseCode);


				InputStream is = con.getInputStream();
				System.out.println("Response Code : " + responseCode);


				byte by[] = new byte[ BUFFER_SIZE ];
				int index = is.read( by, 0, BUFFER_SIZE );
				System.out.println("Response Code3 : " + responseCode);

				byte temp[] = new byte[ 10000000 ];
				int ctr = 0;

				while ( index != -1 ){
					out.write(by,0,index);
					for(int i=0 ;i<index;i++)
						temp[ctr++] = by[i];

					index = is.read( by, 0, BUFFER_SIZE );
				}
				
				System.out.println("ctr = " + ctr);
				ctr = 0;
				System.out.println("Response Code : " + responseCode);

				out.flush();
				System.out.println("ProxyThread: Closing I/O streams for http: "+urlToCall);
				out.close();
				in.close();
				socket.close();

				System.out.println("Finish");
				//end send response to client

				
				/*//COMMENTED HTTP TUNNEL
				Document doc;
				System.out.println("Entered HTTP");

				if (false){}
				else{

					try {

						if(doContentFilter){
							// Check if document returned, contains explicit content using NB trainer
							doc = Jsoup.connect("http://"+urlToCall).get();
							System.out.print(doc);
							String title = doc.title();
							Elements links = doc.select("a[href]");

							float badCount = 0;
							float count = 0;
							for (Element link : links){
								count++ ;
								if(data.nb_link.predict(link.attr("href")).equals("explicitContent")
									|| 	data.nb_text.predict(link.text()).equals("explicitContent")){
									badCount++ ;
								}		
							}

							if((badCount/count)>0.3){
								out.writeBytes("HTTP/1.1 403 Forbidden \r\n"+"Connection: close\r\n"+"\r\n"+
										"<h1>Webpage Content not related to your academic needs !</h1>");
								out.flush();
								out.close();
								ProxyServer.buffer.reduce_queue_size();
								return;
							}
						}

					} 
					catch (Exception e){
						System.err.format("ProxyThread: Error while using naive bayes on document in HTTPS");
						e.printStackTrace();
					}

					System.out.println("Reached HTTP second phase");

					// Create a HTTP socket with the web server
					System.out.println("urlCall is: "+urlToCall.split(":")[0]);
					Socket clientSocket = new Socket(urlToCall.split(":")[0], 80);  
					String server_addr=clientSocket.getRemoteSocketAddress().toString().split(":")[0].split("/")[1];

					// If the web server is among the black-list of servers, then block
					if(doBlockIP && BlockedIp.isBlocked(server_addr)){
						out.writeBytes("HTTP/1.1 403 Forbidden \r\n"+"Connection: close\r\n"+"\r\n"+"<h1>Host is among your blacklist !</h1>");
						out.flush();
						out.close();
						clientSocket.close();
						ProxyServer.buffer.reduce_queue_size();
						return;
					}

					OutputStream outToServer = clientSocket.getOutputStream();  

					System.out.println("Reached HTTP third phase");
					TcpConnection serverToClient = new TcpConnection("S2C",clientSocket,socket,urlToCall);
					serverToClient.start();
					TcpConnection clientToServer = new TcpConnection("C2S",socket,clientSocket,urlToCall);

					outToServer.write(input.getBytes());
					clientToServer.start();
				}
				 */
			}

			// Since the connection (HTTP/HTTPS) is closed, reduce queue_size by 1
			ProxyServer.buffer.reduce_queue_size();
		}
		catch (Exception e){
			e.printStackTrace();
		}        		

	}

	// Getter method for client_address
	public String get_client_address() {
		return client_addr;
	}

}