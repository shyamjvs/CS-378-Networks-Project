package proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Adblock{
	
	public static ArrayList<String> list = new ArrayList<String>();
	static Process p = null;
	static ProcessBuilder builder = null; 
	static BufferedReader inp = null;
	static BufferedWriter out = null;
	
	// Initializes python script for filtering
	public static void init() {
		try {
			Process p = Runtime.getRuntime().exec("python filter.py");
			inp = new BufferedReader(new InputStreamReader(p.getInputStream()));
			out = new BufferedWriter( new OutputStreamWriter(p.getOutputStream()) );
		} catch (IOException e) {
			System.err.format("Adblock: Error while initializing 'filter.py'");
			e.printStackTrace();
		}
	}
	
	// This function returns whether url is to be blocked or not
	public static boolean filter(String url) {
		
		String ret = "True";
		try {
			out.write(url + "\n");
			out.flush();
			ret = inp.readLine();
		} catch (IOException e) {
			System.err.format("Adblock: Error while working with 'filter.py'");
			e.printStackTrace();
		}
		if (ret.equals("True"))
			return true;
		else			
			return false;
	}
	
}