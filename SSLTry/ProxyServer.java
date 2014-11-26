package proxy;

import java.io.IOException;
import java.net.ServerSocket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class ProxyServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        String ksName = "herong.jks";
        char ksPass[] = "HerongJKS".toCharArray();
        char ctPass[] = "My1stKey".toCharArray();

        boolean listening = true;

        int port = 10000;	//default
        try {
//            port = Integer.parseInt(args[0]);
        } catch (Exception e) {
            //ignore me
        }
        System.out.println("dfdsf");
        try {
    //        serverSocket = new ServerSocket(port);
//              KeyStore ks = KeyStore.getInstance("JKS");
//               ks.load(new FileInputStream(ksName), ksPass);
//               KeyManagerFactory kmf = 
//               KeyManagerFactory.getInstance("SunX509");
//               kmf.init(ks, ctPass);
  //             SSLContext sc = SSLContext.getInstance("TLS");
 //              sc.init(kmf.getKeyManagers(), null, null);
 //              SSLServerSocketFactory ssf = sc.getServerSocketFactory();
  //             SSLServerSocket s 
  //                = (SSLServerSocket) new SSLServerSocket(port);
        
               SSLServerSocketFactory sslserversocketfactory =
                       (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
               SSLServerSocket sslserversocket =
                       (SSLServerSocket) sslserversocketfactory.createServerSocket(port);
               printServerSocketInfo(sslserversocket);

//               SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();
            
               System.out.println("Server started:");
               // Listening to the port
               //SSLSocket c = ;
            
            
            System.out.println("Started on: " + port);
            while (listening) {
            	System.out.println("fsdfkjdsjf");
                new ProxyThread(sslserversocket.accept()).start();
            }
            serverSocket.close();
        } 
        catch (Exception e) {
        	e.printStackTrace();
//            System.err.println("Could not listen on port: " + args[0]);
            System.exit(-1);
        }

        
    }
    
    private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("Server socket class: "+s.getClass());
        System.out.println("   Socker address = "
           +s.getInetAddress().toString());
        System.out.println("   Socker port = "
           +s.getLocalPort());
        System.out.println("   Need client authentication = "
           +s.getNeedClientAuth());
        System.out.println("   Want client authentication = "
           +s.getWantClientAuth());
        System.out.println("   Use client mode = "
           +s.getUseClientMode());
     } 
}