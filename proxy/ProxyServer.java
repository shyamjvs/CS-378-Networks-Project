package proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;

public class ProxyServer {
	
	public static HashMap<String,Long> authenticated = new HashMap<String,Long>();
	public static HashSet<String> login = new HashSet<String>();

	
    public static void main(String[] args) throws IOException {
        
    	ServerSocket serverSocket = null;
        boolean listening = true;
        login.add("paramdeep:singh");
        BlockedIp.readFile("src/proxy/blocked_ips");
        
        int port = 12000;	//default

        //        try {
//            port = Integer.parseInt(args[0]);
//        } catch (Exception e) {
//            //ignore me
//        }

        
        
        
        new data();
        

        
        try 
        {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        
            while (listening) {
     //       	System.out.println("ServerSocket "+serverSocket.accept().toString());
            new ProxyThread(serverSocket.accept()).start();
        }
            
        serverSocket.close();
    
        }
        
        catch (IOException e) 
        {
        	e.printStackTrace();
    	 //    System.err.println("Could not listen on port: " + args[0]);
            System.exit(-1);
        }

}
}