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
			//BufferedWriter outToClient = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
			
            int lineNumber = 0;			// Counter for reading through input request lines from client
			int URLType = 1;			// 1 for HTTPS; 0 for HTTP;
			String requestLine;			// represents a line of request (we read lines one by one)
			//StringBuffer sbuf = new StringBuffer();	// Buffer for generating request string line by line as we read
            List<String> clientHTTPRequest = new ArrayList<String>(); // represents the whole request string
            String urlRequested1 = "";	//  URL in HTTPS form
            String urlRequested2 = "";	//  URL in HTTP form
			String requestType = "";	// "GET" or "POST"
			String urlParameters = "";	// Parameters for POST request

            // Phase 1 : Wait for the client to give a GET request
            while ((requestLine = inFromClient.readLine()) != null){

                try {
					System.out.println("Request line prev: "+requestLine);
					StringTokenizer t = new StringTokenizer(requestLine);
                    t.nextToken();
					System.out.println("Request line: "+requestLine);
					if(lineNumber==0){
						//sbuf.append(requestLine);
						clientHTTPRequest.add(requestLine);
					}
					else{
						//sbuf.append(System.getProperty("line.separator"));
						//sbuf.append(requestLine);
						clientHTTPRequest.add(requestLine);
					}
                } catch (Exception e){
                    break;
				}
				lineNumber++;
				//System.out.println("Looping: "+lineNumber);
            }
			System.out.println("Came out of while loop");

			//clientHTTPRequest = sbuf.toString();
			URLParse parse = new URLParse(clientHTTPRequest);
			String[] tokens = parse.getRequestTriple();
			requestType = tokens[0];
			System.out.println("URL requested by client: " + tokens[1] + " through: " + requestType);

			if(tokens[1].startsWith("https://")) urlRequested1 = tokens[1];
			else if(tokens[1].startsWith("http://")) urlRequested2 = tokens[1];
			else{
				urlRequested1 = "https://"+tokens[1];
				urlRequested2 = "http://"+tokens[1];
			}
            // We now have the URL asked by the client ready with us (along with the parsed request)

			BufferedReader readFromWebServer = null; // Read stream for data from web server
			int responseCode = 0;

            try{

				// Phase 2 : Send a GET/POST request to the web server for the URL
				HttpURLConnection conn = null;

				// First try HTTPS, if URL available
				parse.setRequestURL(urlRequested1);
				if(!urlRequested1.equals("")){
					System.out.println("Attempting for HTTPS URL");

					if(requestType.equals("GET")){
						conn = sendGet(parse);
					}
					else if(requestType.equals("POST")){
						conn = sendPost(parse);
					}
					else{
						System.out.println("Unknown/Unsupported request Type: "+requestType);
						return;
					}
       
	        		responseCode = conn.getResponseCode();
    	     		System.out.println("Response Code for HTTPS: " + responseCode + " for request: " + requestType);
	         		if(responseCode != 200) System.out.println("Request for HTTPS failed");
				}
				// Next try HTTP, if URL available and HTTPS failed/URL unavailable
				parse.setRequestURL(urlRequested2);
				if(!urlRequested2.equals("") && responseCode != 200){
					System.out.println("Attempting for HTTP URL");

					if(requestType.equals("GET")){
						conn = sendGet(parse);
					}
					else if(requestType.equals("POST")){
						conn = sendPost(parse);
					}
					else{
						System.out.println("Unknown/Unsupported request Type: "+requestType);
						return;
					}

					responseCode = conn.getResponseCode();
    	     		System.out.println("Response Code for HTTP: " + responseCode + " for request: " + requestType);
	         		if(responseCode != 200) System.out.println("Request for HTTP failed");
				}					
				// GET/POST request sent by the proxy

                // Phase 3 : Receive the response from the web server
                InputStream inFromWebServer = conn.getInputStream();
				readFromWebServer = new BufferedReader(new InputStreamReader(inFromWebServer), BUFFER_SIZE);
				// Response received from the server and stored into buffer

				// Phase 4 : Output the input received from the web server to the client
				/*
				int value = 0;
                while ((value = readFromWebServer.read()) != -1){
					outToClient.write(value);
                }
                outToClient.flush();
				*/
        		byte buffer[] = new byte[ BUFFER_SIZE ];
                int index = inFromWebServer.read(buffer, 0, BUFFER_SIZE);
                while (index != -1){
					outToClient.write(buffer, 0, index);
					index = inFromWebServer.read(buffer, 0, BUFFER_SIZE);
                }
                outToClient.flush();
                // Sending to client finished

            } catch (Exception e){
                System.err.println("Exception while Phase 2-4: " + e);
                //outToClient.write("",0,0);
				outToClient.writeBytes("");
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

	private HttpURLConnection sendGet(URLParse parse) throws Exception {

		HttpURLConnection conn = null;
		try{
			String urlRequested = parse.getRequestURL();
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

	private HttpURLConnection sendPost(URLParse parse) throws Exception {

		HttpURLConnection conn = null;
		try{
			String urlRequested = parse.getRequestURL();
			String urlParameters = parse.getURLParameters();
			
			System.out.println("Sending POST request to the web-server for: " + urlRequested);
			URL url = new URL(urlRequested);
			conn = (HttpURLConnection) url.openConnection();

			System.out.println("Yo1");
			// Setting parameters to default values
   			conn.setRequestMethod("POST");
			conn.setRequestProperty("User-Agent", USER_AGENT);
/*
			conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5"); 
			conn.setRequestProperty("Content-Language", "en-US");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			conn.setUseCaches (false);
*/
			conn.setDoInput(true);
			conn.setDoOutput(true);

			//System.out.println("Yo2");
			// Overwrite with parameters provided in the POST request
			Iterator it = parse.getPostParameters().entrySet().iterator();
			while (it.hasNext()){
				Map.Entry pairs = (Map.Entry)it.next();
				conn.setRequestProperty((String)pairs.getKey(),(String)pairs.getValue());
				System.out.println(pairs.getKey() + ": " + pairs.getValue());
			}
			//System.out.println("Yo2.5");
			System.out.println("yo : "+urlParameters);
			//System.out.println("Yo MANNN!! : "+urlParameters.length());
			//conn.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			//System.out.println("Yo3");
	
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			System.out.println("Yo4");
		} catch (Exception e){
				System.err.println("Exception in sendPost(): " + e);
		}
		return conn;
	}

}
