package proxy;

import java.io.*;
import java.util.*;
import java.net.*;

public class ProxyThread extends Thread{

    private Socket socket = null;
	private final String USER_AGENT = "Mozilla/5.0";
    private static final int BUFFER_SIZE = 16384;
    public ProxyThread(Socket socket){
        super("ProxyThread");
        this.socket = socket;
    }

    public void run(){

		// The following phases are involved in the proxy's operation:
		// Phase 1 : obtain the URL from the proxy's client
		// Phase 2 : forward this request to web server (internet)
		// Phase 3 : obtain the webpage from the web server
		// Phase 4 : give back the webpage to the proxy's client

        try{
			// Get the I/O streams for communication with the client
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
            int lineNumber = 0;
            String clientHTTPRequest;
            String urlRequested = "";

            // Phase 1 : Wait for the client to give a GET request
            while ((clientHTTPRequest = inFromClient.readLine()) != null){
                // Get the URL from the first line of request
                try {
                    StringTokenizer t = new StringTokenizer(clientHTTPRequest);
                    t.nextToken();
                } catch (Exception e){
                    break;
				}
                
                if (lineNumber == 0){
                    String[] tokens = clientHTTPRequest.split(" ");
                    System.out.println("URL requested by client: " + tokens[1] + " through: " + tokens[0]);

                    if(tokens[1].startsWith("http://") || tokens[1].startsWith("https://")) 
						urlRequested = tokens[1];
                    else
						urlRequested = "https://"+tokens[1];

                    System.out.println("URL sent to web server : " + urlRequested);
                }
				lineNumber++;
            }
            // We now have the URL asked by the client ready with us

			BufferedReader readFromWebServer = null; // Read stream for data from web server
            try{

				// Phase 2 : Send a GET request to the web server for the URL through HTTPS first
                System.out.println("Sending GET request (HTTPS) to the web server for: " + urlRequested);
                URL url = new URL(urlRequested);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        		conn.setRequestMethod("GET");
        		conn.setRequestProperty("User-Agent", USER_AGENT);
       
        		int responseCode = conn.getResponseCode();
        		System.out.println("Response Code for HTTPS: " + responseCode);

				// If request fails, then send through HTTP
				if(responseCode != 200){
	         		System.out.println("Request for HTTPS failed");
					urlRequested = "http" + urlRequested.substring(5);	
					System.out.println("Sending GET request (HTTP) to the web server for: " + urlRequested);
					
                    url = new URL(urlRequested);
                	conn = (HttpURLConnection) url.openConnection();
             	
					conn.setRequestMethod("GET");
					conn.setRequestProperty("User-Agent", USER_AGENT);
          
					responseCode = conn.getResponseCode();
    	     		System.out.println("Response Code for HTTP: " + responseCode);
				}					
				// GET request sent by the proxy

                // Phase 3 : Obtain the response from the web server
                InputStream inFromWebServer = conn.getInputStream();
				readFromWebServer = new BufferedReader(new InputStreamReader(inFromWebServer), BUFFER_SIZE);
				// Response received from the server and stored into buffer

				// Phase 4 : Send the output received from the web server to the client
				int value = 0;
                while ((value = readFromWebServer.read()) != -1){
					outToClient.write(value);
                }
                outToClient.flush();
                // Sending to client finished

            } catch (Exception e){
                System.err.println("Exception while Phase 2-4: " + e);
                outToClient.write("",0,0);
            }

            // Close all the I/O streams
            if (readFromWebServer != null) {
                readFromWebServer.close();
            }
            if (outToClient != null) {
                outToClient.close();
            }
            if (inFromClient != null) {
                inFromClient.close();
            }
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
