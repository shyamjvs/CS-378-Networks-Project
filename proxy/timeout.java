package proxy;
 
import java.text.SimpleDateFormat;
import java.util.Date;
 
public class timeout {
 
	public static boolean is_timed_out(long t1, long t2) {

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
 
		try {
 
			long diff = t2 - t1;
 
			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
            
			System.out.print("Time diff ");
			System.out.print(t2);
			System.out.print(t1);
            //System.out.println(60*60*60*diffDays + 60*60*diffHours + 60*diffMinutes + diffSeconds);
            
            if(60*60*60*diffDays + 60*60*diffHours + 60*diffMinutes + diffSeconds < 10)
                return false;
                
            else return true;
                
        
		} catch (Exception e) {
			e.printStackTrace();
		}
        return true;
 
	}
 
}
