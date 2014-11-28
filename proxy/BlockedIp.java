package proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class BlockedIp {
	static Multimap<Integer, String> records = ArrayListMultimap.create();
	public static void readFile(String filename)
	{
	  try
	  {
	    BufferedReader reader = new BufferedReader(new FileReader(filename));
	    String line;
	    while ((line = reader.readLine()) != null)
	    {	
	    	String word[] = line.split(" ");
	    	for(int j=0; j<word.length; j++){
	    	String y = "00000000";
	    	String ipandmask[] = word[j].split("/");
	    	String[] ipAddressInArray = ipandmask[0].split("\\.");
	    	 
	    	String ip="";
	    	for (int i = 0; i < ipAddressInArray.length; i++) {
			String x = Integer.toBinaryString(Integer.parseInt(ipAddressInArray[i]));
			x = y.substring(x.length()) + x;
			ip+=x;
	    	}
			records.put(Integer.parseInt(ipandmask[1]), ip);
//			System.out.println(ip);
	    	}
	    }
	    reader.close();
	    return;
	  }
	  catch (Exception e)
	  {
	    System.err.format("Exception occurred trying to read '%s'.", filename);
	    e.printStackTrace();
	    return;
	  }
	}
	
	public static boolean isBlocked(String clientip){
		String y = "00000000";
    	String[] ipAddressInArray = clientip.split("\\.");
    	 
    	String ip="";
    	for (int i = 0; i < ipAddressInArray.length; i++) {
		String x = Integer.toBinaryString(Integer.parseInt(ipAddressInArray[i]));
		x = y.substring(x.length()) + x;
		ip+=x;
    	}
    	for(int i=1; i<=32; i++){
    		Collection<String> ips = records.get(i);
    		Iterator itr = ips.iterator();
    		while(itr.hasNext()){
    			String blck=itr.next().toString();
    			if(blck.substring(0, i).equals(ip.substring(0, i)))
    				return true;
    		}
    	}
    	return false;
    }
}
