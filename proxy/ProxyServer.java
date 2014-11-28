package proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.HashSet;

public class ProxyServer {
	
	public static HashMap<String,Long> authenticated = new HashMap<String,Long>();
	public static HashSet<String> login = new HashSet<String>();
	public static HashMap<String,Integer> priorityMap = new HashMap<String,Integer>();
	public static Prioritization prior;
	public static LruCache cache = new LruCache(15000);
	
    public static void main(String[] args) throws IOException {
    	Adblock.init();   
    	
    	ServerSocket serverSocket = null;
        boolean listening = true;
        login.add("paramdeep:singh");
        priorityMap.put("192.168.1.9", 100);
        BlockedIp.readFile("proxy/blocked_ips");
        
        int port = 12000;	//default

        prior = new Prioritization(10);
        prior.start();

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
            	
            	ProxyThread pt=new ProxyThread(serverSocket.accept());
            	if(priorityMap.containsKey(pt.clien_addr))
            		prior.queue.add(new QElement(pt, priorityMap.get(pt.clien_addr)));
            	else
            		prior.queue.add(new QElement(pt, 10));
            	
     //       	System.out.println("ServerSocket "+serverSocket.accept().toString());
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