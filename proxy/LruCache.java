package proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class LruCache {
	private Map<String, byte[]> cache = new HashMap<String, byte[]>();
	private Map<String, Integer> lru = new HashMap<String, Integer>();
	private HashMap<Integer, String> reverse = new HashMap<Integer, String>();
	private TreeSet<Integer> set = new TreeSet<Integer>();
	private int CACHE_SIZE = 16;
	private int counter = 0;
	private int TIME_OUT = 10;
	
	public LruCache(int size) {
		this.CACHE_SIZE = size;
		TIME_OUT = 10;
	}
	
	public LruCache(int size, int timeout) {
		this.CACHE_SIZE = size;
		this.TIME_OUT = timeout;
	}
	public void Insert(String key, byte[] value) {
//		System.out.println("========INSERTING, cache size = " + cache.size() +  "tree size = " + set.size() + "===========================================");
//		System.out.println("============================="+ counter + " " + TIME_OUT + "===============================================");
		cache.put(key, value);
//		System.out.println(counter +"check 1");
		if (lru.containsKey(key)) {
			Integer t = lru.get(key);
			reverse.remove(t);
			set.remove(t);
		}
//		System.out.println(counter + "check 2");
		lru.put(key, counter);
//		System.out.println(counter + "check 3");
		reverse.put(Integer.valueOf(counter), key);
		set.add(counter);
//		System.out.println(counter + "check 4");
		if (counter % TIME_OUT == 0) {
//			System.out.println("---------------------------CONDITOIN IS TRUE -----------------------------");
			Free();
		}
		counter++;
//		System.out.println(counter + "check 5");
	}
	public boolean Exists(String key) {
		return cache.containsKey(key);
	}
	public byte[] Get(String key){
		return cache.get(key);
	}
	private void Free(){
		int s = cache.size();
//		System.out.println("========FREEING, cache size = " + s +  "tree size = " + set.size() + "===========================================");
		
		while(s > CACHE_SIZE) {
			Integer t = set.first();
			set.remove(t);
			String toRemove = reverse.get(t);
			cache.remove(toRemove);
			lru.remove(toRemove);
			reverse.remove(t);	
			s = cache.size();
		}		
//		System.out.println("Cache size after freeing = " + cache.size());
	}
	
}


