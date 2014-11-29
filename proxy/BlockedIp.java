package proxy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

public class BlockedIp {

	// Hash set for storing blocked IP prefixes
	static HashSet<String> records = new HashSet<String>();

	// Read in the list of blocked IPs from the file & add them to 'records'
	public static void readFile(String filename){
		try{

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					BlockedIp.class.getClassLoader().getResourceAsStream(filename)));
			String line;

			try{
				// Iterate over lines of the file
				while ((line = reader.readLine()) != null){

					String word[] = line.split(" ");

					// Iterate over blocked IP entries within a line
					for(int j=0; j<word.length; j++){
						String y = "00000000";
						String ipandmask[] = word[j].split("/");
						String[] ipAddressInArray = ipandmask[0].split("\\.");

						String ip=""; // String for the IP prefix for blocked subnets in binary
						for (int i = 0; i < ipAddressInArray.length; i++) {
							String x = Integer.toBinaryString(Integer.parseInt(ipAddressInArray[i]));
							x = y.substring(x.length()) + x;
							ip += x;
						}
						// Add the entry of mask versus ip into the multiMap
						records.add(ip.substring(0,Integer.parseInt(ipandmask[1])));
					}
				}
			}
			catch (Exception e){
				System.err.format("BlockedIP: error while filling blocked IPs");
				e.printStackTrace();
			}
			reader.close();
			return;
		}
		catch (Exception e){
			System.err.format("BlockedIP: Exception occurred trying to read '%s'.", filename);
			e.printStackTrace();
			return;
		}
	}

	// Function for checking if a given IP is blocked
	public static boolean isBlocked(String clientip){
		try{
			String y = "00000000";
			String[] ipAddressInArray = clientip.split("\\.");
	
			String ip="";
			// Find out the IP address in binary form
			for (int i = 0; i < ipAddressInArray.length; i++) {
				String x = Integer.toBinaryString(Integer.parseInt(ipAddressInArray[i]));
				x = y.substring(x.length()) + x;
				ip+=x;
			}
	
			// Check if any of its prefix is in the hashset 
			for(int i=2; i<=32; i++){
				if(records.contains(ip.substring(i))){
					return true;
				}
			}
			return false;
		}
		catch(Exception e){
			System.err.format("BlockedIP: Exception occurred while checking isBlocked");
			e.printStackTrace();
			return false;
		}
	}
}