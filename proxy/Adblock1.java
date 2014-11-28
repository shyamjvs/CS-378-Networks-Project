package proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Adblock1 {
	public static ArrayList<String> list = new ArrayList<String>();
	//!	static Process p = null;
	//!	static ProcessBuilder builder = null; 
	//!	static BufferedReader in = null;
	//!	static OutputStream out = null;
	private int lcs(String x, String y) {
		return 0;
	}
	public static boolean filter(String url) {
		return true;
		

		//!		String ret = "True";
		//!		try {
		//!			out = p.getOutputStream();
		//!			out.write(url.getBytes());
		//!			System.out.println("Potty");
		//!			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
		//!			
		//!			ret = new String(in.readLine());
		//!			System.out.println("Tatti");
		//!		} catch (IOException e) {
		//!			// TODO Auto-generated catch block
		//!			e.printStackTrace();
		//!		}
		//!		if (ret.equals("True"))
		//!			return true;
		//!		else
		//!			return false;
		//!	}
	}
	public static void init() {
		BufferedReader br = null;
		try{
			String s;
			br = new BufferedReader(new FileReader("easylist.txt"));
			while ((s = br.readLine()) != null) {
				list.add(s);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			System.out.println("fsdjfdsf");
		}
	}
}