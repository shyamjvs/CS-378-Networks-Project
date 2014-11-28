package proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class BlockList {
	static ArrayList<String> list = new ArrayList<String>();
	
	private static boolean init(Socket socket) {
		String ip = socket.getRemoteSocketAddress().toString().split(":")[0].substring(1);
		BufferedReader br = null;
		list = new ArrayList<String>();
		
		try {
			
			String s = null;
			br = new BufferedReader(new FileReader("/tmp/" + ip));
//			System.out.println("hellp " + ip);
			
			while ((s = br.readLine()) != null) {
//				System.out.println("abcdef " + s);
				list.add(s);
//				System.out.println("in loop");
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
		return true;
	}
	
	public static boolean isBlocked(Socket socket, String url){
		init(socket);
		System.out.println("sdfjdsfhdskfh");
		for (int i = 0; i < list.size(); i++) {
			String b = list.get(i);
			if (b.length() > 1 && url.contains(b)) {
				return true;
			}
		}
		return false;
	}
	
}
