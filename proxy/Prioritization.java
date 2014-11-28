package proxy;
import java.util.PriorityQueue;
import java.util.Queue;

public class Prioritization extends Thread {
public static int cur_queue_size;
public static Queue<QElement> queue = new PriorityQueue<QElement>();
private int max_queue_size;
	public Prioritization(int queue_size) {
		max_queue_size=queue_size;
		cur_queue_size=0;
	}
		
public void run(){
	while(true){
		try{
		if( cur_queue_size < max_queue_size ) {
			queue.poll().getproxythread().start();
			cur_queue_size++;
		}
		}
		catch(Exception e){
			
		}
	}
}
}
