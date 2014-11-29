package proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

public class ProxyServer {

	public static HashMap<String,Long> authenticated = new HashMap<String,Long>();
	public static HashSet<String> login = new HashSet<String>();
	public static HashMap<String,Integer> priorityMap = new HashMap<String,Integer>();
	public static Prioritization buffer;
	public static LruCache cache = new LruCache(1500);
	public static boolean[] switches = new boolean[6];
	// doAuthenticate = switches[0];
	// doBlockIP = switches[1];
	// doAdBlock = switches[2];
	// doContentFilter = switches[3];
	// doSiteBlock = switches[4];
	// doPrioritize = switches[5];

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/proxy";
	static final String USER = "root";
	static  final String PASS = "Nahiyaad";

	// The main driver routine for the proxy server
	public static void main(String[] args) throws IOException {

		Connection conn = null;
		Statement stmt = null;
		//MySQL code
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			//STEP 4: Execute a query
			stmt = conn.createStatement();
			String sql;
			sql = "SELECT * FROM login";
			ResultSet rs = stmt.executeQuery(sql);

			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				String id = rs.getString("id");
				String pass = rs.getString("password");

				//Display values
				login.add(id+":"+pass);
			}
			//STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try


		ServerSocket serverSocket = null;
		boolean listening = true;
		int port = 12000;	//default

		try{
			// Start Ad blocker
			Adblock.init();   

			//read proxy.config file
			BufferedReader br = null;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader("/tmp/proxy.config"));
				while ((sCurrentLine = br.readLine()) != null) {
					if (sCurrentLine.contains("Compression")){
						if (sCurrentLine.contains(": on")){
							
						}
					}
					if (sCurrentLine.contains("Caching")){
						if (sCurrentLine.contains(": on")){
							
						}
					}
					if (sCurrentLine.contains("Blocked")){
						if (sCurrentLine.contains(": on")){
							switches[4] = true;
						}
					}
					if (sCurrentLine.contains("Adblock")){
						if (sCurrentLine.contains(": on")){
							switches[2] = true;
						}
					}
					if (sCurrentLine.contains("IP")){
						if (sCurrentLine.contains(": on")){
							switches[1] = true;
						}
					}
					if (sCurrentLine.contains("Content")){
						if (sCurrentLine.contains(": on")){
							switches[3] = true;
						}
					}
					if (sCurrentLine.contains("Authentication")){
						if (sCurrentLine.contains(": on")){
							switches[0] = true;
						}
					}
					if (sCurrentLine.contains("Prioritization")){
						if (sCurrentLine.contains(": on")){
							switches[5] = true;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			

//			// Add a few login credentials
//			login.add("shyam:jvs");
//			login.add("paramdeep:singh");
//			login.add("royal:jain");
//			login.add("pratyaksh:sharma");

			if(switches[5]){
				// Add a few request service priorities
				priorityMap.put("192.168.1.9", 100);	
			}

			// Start the thread for proxy's queue(buffer)
			buffer = new Prioritization(100);
			buffer.start();

			// Start IP Blocker
			BlockedIp.readFile("blocked_ips");

			// Start content-filtering object
			new data();
		}
		catch (Exception e){
			System.err.format("ProxyServer: Error while initializing the proxy");
			e.printStackTrace();
			System.exit(-1);
		}

		try {

			try {
				port = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out.println("No port specified. Using default port.");
			}
			serverSocket = new ServerSocket(port);
			System.out.println("Started the proxy on: " + port);

			while (listening){
				System.out.println("===================LISTENING===================");
				ProxyThread pt = new ProxyThread(serverSocket.accept(), switches);
				if(priorityMap.containsKey(pt.get_client_address()))
					buffer.add(new QElement(pt, priorityMap.get(pt.get_client_address())));
				else
					buffer.add(new QElement(pt, 10));

			}
			serverSocket.close();
		}
		catch (IOException e) {
			System.err.println("Could not listen on port: " + port);
			e.printStackTrace();
			System.exit(-1);
		}

	}

}