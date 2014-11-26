package proxy;

import java.net.*;
import java.io.*;

public class ProxyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        boolean listening = true;

        int port = 10000;	//default
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            System.out.println("No port specified. Using default port.");
        }

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Started on: " + port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }

        while (listening){
            new ProxyThread(serverSocket.accept()).start();
			System.out.println("\nNew client connected");
        }
        serverSocket.close();
    }
}
