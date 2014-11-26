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
			
            int lineNumber = 0;			// Counter for reading through input request lines from client
			int URLType = 1;			// 1 for HTTPS; 0 for HTTP;
            String clientHTTPRequest;
            String urlRequested1 = "";	//  URL in HTTPS form
            String urlRequested2 = "";	//  URL in HTTP form
			String requestType = "";	// "GET" or "POST"
			String urlParameters = "";	// Parameters for POST request

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
					requestType = tokens[0];
                    System.out.println("URL requested by client: " + tokens[1] + " through: " + requestType);

					if(tokens[1].startsWith("https://")) urlRequested1 = tokens[1];
					else if(tokens[1].startsWith("http://")) urlRequested2 = tokens[1];
                    else{
						urlRequested1 = "https://"+tokens[1];
						urlRequested2 = "http://"+tokens[1];
					}
                }
				lineNumber++;
            }
            // We now have the URL asked by the client ready with us

			BufferedReader readFromWebServer = null; // Read stream for data from web server
			int responseCode = 0;

            try{

				// Phase 2 : Send a GET/POST request to the web server for the URL
				HttpURLConnection conn = null;

				// First try HTTPS, if URL available
				if(!urlRequested1.equals("")){
					System.out.println("Attempting for HTTPS URL");

					if(requestType.equals("GET")){
						conn = sendGet(urlRequested1);
					}
					else if(requestType.equals("POST")){
						conn = sendPost(urlRequested1, urlParameters);
					}
					else{
						System.out.println("Unknown/Unsupported request Type: "+requestType);
					}
       
	        		responseCode = conn.getResponseCode();
	        		System.out.println("Response Code for HTTPS: " + responseCode);
	         		if(responseCode != 200) System.out.println("Request for HTTPS failed");
				}
				// Next try HTTP, if URL available and HTTPS failed/URL unavailable
				if(!urlRequested2.equals("") && responseCode != 200){
					System.out.println("Attempting for HTTP URL");

					if(requestType.equals("GET")){
						conn = sendGet(urlRequested2);
					}
					else if(requestType.equals("POST")){
						conn = sendPost(urlRequested2, urlParameters);
					}
					else{
						System.out.println("Unknown/Unsupported request Type: "+requestType);
					}

					responseCode = conn.getResponseCode();
    	     		System.out.println("Response Code for HTTP: " + responseCode);
	         		if(responseCode != 200) System.out.println("Request for HTTP failed");
				}					
				// GET/POST request sent by the proxy

                // Phase 3 : Receive the response from the web server
                InputStream inFromWebServer = conn.getInputStream();
				readFromWebServer = new BufferedReader(new InputStreamReader(inFromWebServer), BUFFER_SIZE);
				// Response received from the server and stored into buffer

				// Phase 4 : Output the input received from the web server to the client
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

            // Close all the I/O streams and writers
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

	private HttpURLConnection sendGet(String urlRequested) throws Exception {

		HttpURLConnection conn = null;
		try{
			System.out.println("Sending GET request to the web-server for: " + urlRequested);
			URL url = new URL(urlRequested);
			conn = (HttpURLConnection) url.openConnection();

   			conn.setRequestMethod("GET");
   			conn.setRequestProperty("User-Agent", USER_AGENT);
		} catch (Exception e){
                System.err.println("Exception in sendGet(): " + e);
		}
		return conn;
	}

	private HttpURLConnection sendPost(String urlRequested, String urlParameters) throws Exception {

		HttpURLConnection conn = null;
		try{
			System.out.println("Sending POST request to the web-server for: " + urlRequested);
			URL url = new URL(urlRequested);
			conn = (HttpURLConnection) url.openConnection();
 	
   			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", USER_AGENT);
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5"); 
			conn.setRequestProperty("Content-Language", "en-US");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			conn.setUseCaches (false);
			conn.setDoInput(true);
			conn.setDoOutput(true);
	
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
		} catch (Exception e){
				System.err.println("Exception in sendPost(): " + e);
		}
		return conn;
	}

}
