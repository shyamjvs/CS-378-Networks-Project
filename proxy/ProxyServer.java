package proxy;
import java.net.*;
import java.io.*;
import java.lang.*;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;
public class ProxyServer {
public static HashMap<String,Long> authenticated = new HashMap<String,Long>();
public static HashSet<String> login = new HashSet<String>();

public static void main(String[] args) throws IOException {
login.add("paramdeep:singh");
ServerSocket serverSocket = null;
boolean listening = true;
BlockedIp.readFile("src/proxy/blocked_ips");
int port = 7940; //default
// try {
// port = Integer.parseInt(args[0]);
// } catch (Exception e) {
// //ignore me
// }
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
// System.err.println("Could not listen on port: " + args[0]);
System.exit(-1);
}
}
}
