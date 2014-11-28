package proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Adblock {
	public static ArrayList<String> list = new ArrayList<String>();
	static Process p = null;
	static ProcessBuilder builder = null; 
	static BufferedReader inp = null;
	static BufferedWriter out = null;
	private int lcs(String x, String y) {
		return 0;
	}
	public static boolean filter(String url) {
		String ret = "True";
		try {
			out.write(url + "\n");
			out.flush();
			ret = inp.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (ret.equals("True"))
			return true;
		else			
			return false;
	}

	public static void init() {
		try {
			Process p = Runtime.getRuntime().exec("python filter.py");
			inp = new BufferedReader(new InputStreamReader(p.getInputStream()));
			out = new BufferedWriter( new OutputStreamWriter(p.getOutputStream()) );
//			out.write("http://static.adzerk.net/reddit/ads.php\n");
//			out.flush();
//			String ret = inp.readLine();
//
//			System.out.println("response : " + ret);
//			out.write("http://static.adzerk.net/reddit/ads.php\n");
//			out.flush();
//			ret = inp.readLine();
//			System.out.println("next : " + ret);
//			//			inp.close();
//			//			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}