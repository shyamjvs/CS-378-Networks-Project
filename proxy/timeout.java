package proxy;
 
// Class for maintaining timeout for authentication
public class timeout {
 
	public static boolean is_timed_out(long t1, long t2) {

		long timeout_limit=10;
		try {
 
			long diff = t2 - t1;
 
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
            
			System.out.println("Time diff ");
			System.out.println(t2);
			System.out.println(t1);
            
            if(60*60*24*diffDays + 60*60*diffHours + 60*diffMinutes + diffSeconds < timeout_limit)
                return false;
                
            else return true;
                
		} catch (Exception e) {
			e.printStackTrace();
		}
        return true;
 
	}
 
}