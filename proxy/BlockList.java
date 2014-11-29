package proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class BlockList{
	
	// List of blocked keywords
	static ArrayList<String> list = new ArrayList<String>();
	
	// Initializes the list with keywords read from a file (for each user individually)
	private static boolean init(Socket socket) {
		
		BufferedReader br = null;
		list = new ArrayList<String>();
		
		try{
			// Get the IP address of the client(user)
			String ip = socket.getRemoteSocketAddress().toString().split(":")[0].substring(1);
			
			String s = null;
			// Read for keywords from the file corresponding to 'ip'
			br = new BufferedReader(new FileReader("/tmp/" + ip));
			while ((s = br.readLine()) != null){
				list.add(s);
			}
		} 
		catch (IOException e){
			System.err.format("BlockList: Error while reading from /tmp/ip");
			e.printStackTrace();
		} finally{
			try {
				if (br != null)
					br.close();
			} 
			catch (IOException ex){
				ex.printStackTrace();
				System.err.format("BlockList: Unable to close file bufferedReader");
			}
		}
		return true;
	}
	
	// Function to check whether the given url is to be blocked or not
	public static boolean isBlocked(Socket socket, String url){
		try{
			init(socket);
			for (int i = 0; i < list.size(); i++){
				String b = list.get(i);
				if (b.length() > 1 && url.contains(b)){
					return true;
				}
			}
			return false;
		}
		catch (Exception e){
			System.err.format("BlockList: Error in function isBlocked");
			e.printStackTrace();
		}
		return false;
	}
	
}