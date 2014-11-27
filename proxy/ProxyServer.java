package proxy;

import java.io.IOException;
import java.net.ServerSocket;

public class ProxyServer {
	static LruCache cache = new LruCache(2048);
	
    public static void main(String[] args) throws IOException {
    	Adblock.init();    	
        ServerSocket serverSocket = null;
        
        boolean listening = true;

        int port = 7000;	//default
//        try {
//            port = Integer.parseInt(args[0]);
//        } catch (Exception e) {
//            //ignore me
//        }

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        
        while (listening) {
            new ProxyThread(serverSocket.accept()).start();
        }
        serverSocket.close();
    
        }
     catch (IOException e) {
        e.printStackTrace();
    	 //    System.err.println("Could not listen on port: " + args[0]);
            System.exit(-1);
        }

}
}