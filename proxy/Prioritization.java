package proxy;

import java.util.PriorityQueue;
import java.util.Queue;

// This class represents the thread for queue/buffer of requests at the proxy server
public class Prioritization extends Thread {
	
	private static int cur_queue_size;
	private static Queue<QElement> queue = new PriorityQueue<QElement>();
	private int max_queue_size;
	
	// Constructor for the class
	public Prioritization(int queue_size) {
		max_queue_size=queue_size;
		cur_queue_size=0;
	}
		
	// This keeps running indefinitely within the proxy server
	public void run(){
		while(true){
			try{
				if( cur_queue_size < max_queue_size ) {
					queue.poll().getproxythread().start();
					cur_queue_size++;
				}
			}
			catch(Exception e){
				//System.err.format("Prioritization: Error in the thread for the proxy's buffer");
				//e.printStackTrace();
			}
		}
	}
	
	// Method to add an entry to the queue
	public void add(QElement q){
			queue.add(q);
	}
	
	// Method to reduce the cur_queue_size by 1
	public void reduce_queue_size(){
		cur_queue_size--;
	}

}