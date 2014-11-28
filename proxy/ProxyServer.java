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
public static HashMap<String,Integer> priorityMap = new HashMap<String,Integer>();

public static void main(String[] args) throws IOException {
login.add("paramdeep:singh");
priorityMap.put("192.168.1.9", 100);
ServerSocket serverSocket = null;
boolean listening = true;
BlockedIp.readFile("src/proxy/blocked_ips");
int port = 7940;
Prioritization prior = new Prioritization(10);
prior.start();
//default
// try {
// port = Integer.parseInt(args[0]);
// } catch (Exception e) {
// //ignore me
// }
try {
serverSocket = new ServerSocket(port);
System.out.println("Started on: " + port);
while (listening) {
	ProxyThread pt=new ProxyThread(serverSocket.accept());
	if(priorityMap.containsKey(pt.clien_addr))
		prior.queue.add(new QElement(pt, priorityMap.get(pt.clien_addr)));
	else
		prior.queue.add(new QElement(pt, 10));
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
