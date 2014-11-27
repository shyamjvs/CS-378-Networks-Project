package proxy;

import java.net.*;
import java.io.*;

public class ProxyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        int port = 11000;	//default
//        try {
//            port = Integer.parseInt(args[0]);
//        } catch (Exception e) {
//            //ignore me
//        }

        
        
        
        new data();
        
        Run.trainData();
        
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