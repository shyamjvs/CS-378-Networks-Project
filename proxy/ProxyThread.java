package proxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sun.misc.BASE64Decoder;




public class ProxyThread extends Thread {

	private Socket socket = null;
	public String clien_addr;
	//private String server_addr;
	private boolean Https = false;


	public ProxyThread(Socket socket) {

		super("ProxyThread");
		this.socket = socket;
		clien_addr= socket.getRemoteSocketAddress().toString().split(":")[0].substring(1);
		//server_addr= socket.getRemoteSocketAddress().toString().split(":")[0].substring(1);
//		System.out.println("chotttttttttti "+clien_addr);
	}

	public void run() {

		try {

			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			InputStream in1 = socket.getInputStream();

			BufferedReader in = new BufferedReader(new InputStreamReader(in1));

			String inputLine;



			int cnt = 0;
			String urlToCall = "";

			BASE64Decoder decoder = new BASE64Decoder();

			String input = "";

			while ((inputLine = in.readLine()) != null) 
			{

				//			System.out.println("Jha.2 " + inputLine);
				//			System.out.println("Gooooooooooooo "+cnt);
				//			System.out.println(inputLine);

				input += inputLine + "\r\n";


				if(inputLine.startsWith("Proxy-Authorization: Basic")){
//					System.out.println("Password is "+inputLine.substring(27).split("\n")[0]+" qqqqqqq");
					if(ProxyServer.login.contains(new String(decoder.decodeBuffer(inputLine.substring(27).split("\n")[0])))){
						Date dt=new Date();
						ProxyServer.authenticated.put(clien_addr,dt.getTime());
					}
				}


				try {
					StringTokenizer tok = new StringTokenizer(inputLine);
					tok.nextToken();
				} catch (Exception e) {
					break;
				}

				if (cnt == 0) 
				{
					String[] tokens = inputLine.split(" ");				

//					System.out.println("Tok "  +   tokens[0]);

					if(tokens[0].equals("GET")||tokens[0].equals("POST"))
					{
//						System.out.println("Got it");
						Https= false;
						urlToCall = tokens[1].substring(7);
					}
					else if(tokens[0].equals("CONNECT"))
					{
//						System.out.println("WTF");
						Https = true;
						urlToCall = tokens[1];
					}	
					else
					{	
						urlToCall = tokens[1].substring(7);
					}
				}


				cnt++;
			}

//			System.out.println("Param Satya 3 ");
			Date cur_dt=new Date();
			if(!ProxyServer.authenticated.containsKey(clien_addr) || timeout.is_timed_out(ProxyServer.authenticated.get(clien_addr).longValue(),cur_dt.getTime())){
//				System.out.println("Param Satya 4 ");
				out.writeBytes("HTTP/1.1 407 Proxy Authentication Required\r\nProxy-Authenticate: Basic realm=\"proxy\"\r\n");
//				System.out.println("Param Satya 5 ");
				out.flush();
				out.close();
				ProxyServer.prior.cur_queue_size--;
				return;
			}


//			System.out.println("YAHA");		
//			System.out.println("Input\n"+input);
//			System.out.println("YAHA1");
//			System.out.println("URL : " + urlToCall);
//			System.out.println("YAHA2");		

			//Adblock check
			System.out.println("BEFORE!!!!!!!!");
			if (Adblock.filter(urlToCall)){
				System.out.println("=============== AD BLOCKED ==================== ");
				//				System.out.println("AdBlocked! " + urlToCall);
				out.close();
				in.close();
				socket.close();
				return;
			}
			System.out.println("AFTER!!!!!!!!");
//
//			//Check if in blocked list
//			if(BlockList.isBlocked(socket, urlToCall)){
//				System.out.println("=============== SITE IS BLOCKED ==================== ");
//				out.close();
//				in.close();
//				socket.close();
//				return;
//			}




			if(Https)
			{

//				System.out.println("HTTPS Protocol " + urlToCall);

				Document doc;
				try {

					doc = Jsoup.connect("https://"+urlToCall).get();

					String title = doc.title();
//					System.out.println("title : " + title);

					Elements links = doc.select("a[href]");

					float badCount = 0;
					float count = 0;
					for (Element link : links) 
					{

						count = count +1 ;
						if(data.nb_link.predict(link.attr("href")).equals("explicitContent")
								|| 	data.nb_text.predict(link.text()).equals("explicitContent"))
						{
							badCount = badCount + 1;
						}		

//						System.out.println("\nlink : " + link.attr("href"));
//						System.out.println("text : " + link.text());

					}

					if((badCount/count)>0.3)
					{
//						System.out.println("Pahuch Gaya");
						out.writeBytes("HTTP/1.1 403 Forbidden \r\n" + 
								"Connection: close\r\n"+
								"\r\n"+"<h1> ABC</h1>");
						out.flush();
						out.close();
						ProxyServer.prior.cur_queue_size--;
						return;
					}	

				} 

				catch (IOException e)
				{
					e.printStackTrace();
				}


//				System.out.println("HTTPS Protocol " + urlToCall);


				Socket clientSocket = new Socket(urlToCall.split(":")[0], 443);
				String server_addr=clientSocket.getRemoteSocketAddress().toString().split(":")[0].split("/")[1];
//				System.out.println("address "+server_addr);
				if(BlockedIp.isBlocked(server_addr)){
					out.writeBytes("HTTP/1.1 403 Forbidden \r\n" + 
							"Connection: close\r\n"+
							"\r\n");
					out.flush();
					out.close();
					clientSocket.close();
					ProxyServer.prior.cur_queue_size--;
					return;
				}
//				System.out.println("reached here");

				out.write(("HTTP/1.1 200 OK\r\n" + 
						"Content-Type: text/xml; charset=utf-8\r\n" + 
						"Content-Length: length\r\n" + 
						"\r\n").getBytes());


//				System.out.println("Connected To " + clientSocket.getRemoteSocketAddress());
//				System.out.println("Connected To " + socket.getRemoteSocketAddress());

//				if(!socket.isClosed() && !clientSocket.isClosed())
//					System.out.println("Mission is a go");

				TcpConnection clientToServer = new TcpConnection("C2S",socket,clientSocket,urlToCall);
				TcpConnection serverToClient = new TcpConnection("S2C",clientSocket,socket,urlToCall);

//				if(!socket.isClosed() && !clientSocket.isClosed())System.out.println("Mission is a go now");

				clientToServer.start();
				serverToClient.start();

			}

			else
			{
				urlToCall = input.split("\r\n")[1].split(" ")[1];
//				System.out.println("HTTP Protocol " + urlToCall);

				//Check if in cache
//				if (ProxyServer.cache.Exists(urlToCall) && !urlToCall.endsWith("css") //&& !urlToCall.endsWith("jpg")) {
//						){
//					byte[] temp = ProxyServer.cache.Get(urlToCall);
//					out.write(temp, 0, temp.length);
//					out.flush();
//					if (out != null) {
//						out.close();
//					}
//					if (in != null) {
//						in.close();
//					}
//					if (socket != null) {
//						socket.close();
//					}
//				}
				
				if (false){
				
				}

				else{


					Document doc;
					try {

						doc = Jsoup.connect("http://"+urlToCall).get();

						String title = doc.title();
//						System.out.println("title : " + title);

						Elements links = doc.select("a[href]");

						float badCount = 0;
						float count = 0;
						for (Element link : links) 
						{

							count = count +1 ;
							if(data.nb_link.predict(link.attr("href")).equals("explicitContent")
									|| 	data.nb_text.predict(link.text()).equals("explicitContent"))
							{
								badCount = badCount + 1;
							}		

//							System.out.println("\nlink : " + link.attr("href"));
//							System.out.println("text : " + link.text());

						}

						if((badCount/count)>0.3)
						{

							out.writeBytes("This site is not related to your academic needs");
							out.flush();
							out.close();
							ProxyServer.prior.cur_queue_size--;
							return;
						}	

					} 
					catch (IOException e)
					{
						e.printStackTrace();
					}


					Socket clientSocket = new Socket(urlToCall, 80);  

					String server_addr=clientSocket.getRemoteSocketAddress().toString().split(":")[0].split("/")[1];
//					System.out.println("address "+server_addr);
					if(BlockedIp.isBlocked(server_addr)){
						out.writeBytes("Your proxy firewall has denied access");
						out.flush();
						out.close();
						clientSocket.close();
						ProxyServer.prior.cur_queue_size--;
						return;
					}

//					System.out.println("reached here 2");

					OutputStream outToServer = clientSocket.getOutputStream();  

//					System.out.println("Connected To " + clientSocket.getRemoteSocketAddress());
//					System.out.println("Connected To " + socket.getRemoteSocketAddress());
//
//
//					if(!socket.isClosed() && !clientSocket.isClosed())System.out.println("Mission is a go");

					TcpConnection serverToClient = new TcpConnection("S2C",clientSocket,socket,urlToCall);
					serverToClient.start();

//					System.out.println("MMMMMMMMMMMM");

					TcpConnection clientToServer = new TcpConnection("C2S",socket,clientSocket,urlToCall);
					outToServer.write(input.getBytes());
					clientToServer.start();

				} 

			}
		}

		catch (Exception e)
		{
			e.printStackTrace();
		}        		


		ProxyServer.prior.cur_queue_size--;

	}	
}