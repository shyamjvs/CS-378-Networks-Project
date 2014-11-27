package proxy;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

import sun.misc.BASE64Decoder;



public class ProxyThread extends Thread {

	private Socket socket = null;
	private String clien_addr;
	//private String server_addr;
	private boolean Https = false;


	public ProxyThread(Socket socket) {

		super("ProxyThread");
		this.socket = socket;
		clien_addr= socket.getRemoteSocketAddress().toString().split(":")[0].substring(1);
		//server_addr= socket.getRemoteSocketAddress().toString().split(":")[0].substring(1);
		System.out.println("chotttttttttti "+clien_addr);
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

				System.out.println("Jha.2 " + inputLine);
				System.out.println("Gooooooooooooo "+cnt);
				System.out.println(inputLine);

				input += inputLine + "\r\n";
				
				
				if(inputLine.startsWith("Proxy-Authorization: Basic")){
			        System.out.println("Password is "+inputLine.substring(27).split("\n")[0]+" qqqqqqq");
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

					System.out.println("Tok "  +   tokens[0]);

					if(tokens[0].equals("GET")||tokens[0].equals("POST"))
					{
						System.out.println("Got it");
						Https= false;
						urlToCall = tokens[1].substring(7);
					}
					else if(tokens[0].equals("CONNECT"))
					{
						System.out.println("WTF");
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


			System.out.println("Param Satya 3 ");
			Date cur_dt=new Date();
			if(!ProxyServer.authenticated.containsKey(clien_addr) || timeout.is_timed_out(ProxyServer.authenticated.get(clien_addr).longValue(),cur_dt.getTime())){
				System.out.println("Param Satya 4 ");
				System.out.println("Input\n"+input);
				out.writeBytes("HTTP/1.1 407 Proxy Authentication Required\r\nProxy-Authenticate: Basic realm=\"proxy\"\r\n");
				out.flush();
				out.close();
				return;
			}


			
			System.out.println("Input\n"+input);
			System.out.println("URL : " + urlToCall);

			if(Https)
			{

				System.out.println("HTTPS Protocol " + urlToCall);


				Socket clientSocket = new Socket(urlToCall.split(":")[0], 443);
				String server_addr=clientSocket.getRemoteSocketAddress().toString().split(":")[0].split("/")[1];
				System.out.println("address "+server_addr);
				if(BlockedIp.isBlocked(server_addr)){
					out.writeBytes("Your proxy firewall has denied access");
					out.flush();
					out.close();
					clientSocket.close();
					return;
				}
				System.out.println("reached here");

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

			else
			{

				System.out.println("HTTP Protocol " + urlToCall);


				urlToCall = input.split("\r\n")[1].split(" ")[1];

				Socket clientSocket = new Socket(urlToCall, 80);  
				
				String server_addr=clientSocket.getRemoteSocketAddress().toString().split(":")[0].split("/")[1];
				System.out.println("address "+server_addr);
				if(BlockedIp.isBlocked(server_addr)){
					out.writeBytes("Your proxy firewall has denied access");
					out.flush();
					out.close();
					clientSocket.close();
					return;
				}
				
				System.out.println("reached here 2");

				OutputStream outToServer = clientSocket.getOutputStream();  

				System.out.println("Connected To " + clientSocket.getRemoteSocketAddress());
				System.out.println("Connected To " + socket.getRemoteSocketAddress());


				if(!socket.isClosed() && !clientSocket.isClosed())System.out.println("Mission is a go");

				TcpConnection serverToClient = new TcpConnection("S2C",clientSocket,socket);
				serverToClient.start();

				System.out.println("MMMMMMMMMMMM");

				TcpConnection clientToServer = new TcpConnection("C2S",socket,clientSocket);
				outToServer.write(input.getBytes());
				clientToServer.start();

			} 

		}

		catch (Exception e)
		{
			e.printStackTrace();
		}        		


	}	
}