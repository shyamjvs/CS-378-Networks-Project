package proxy;

import java.util.HashMap;
import java.util.Map;

public class LruCache {
	private Map<String, byte[]> cache = new HashMap<String, byte[]>();
	private Map<String, Integer> lru = new HashMap<String, Integer>();
	private HashMapWithMin<Integer, String> reverse = new HashMapWithMin<Integer, String>();
	private int CACHE_SIZE = 16;
	private int counter = 0;
	private int TIME_OUT = 10;
	
	public LruCache(int size) {
		this.CACHE_SIZE = size;
	}
	
	public LruCache(int size, int timeout) {
		this.CACHE_SIZE = size;
		this.TIME_OUT = timeout;
	}
	public void Insert(String key, byte[] value) {
		System.out.println("============================="+ counter + " " + cache.size() + "===============================================");
		cache.put(key, value);
		if (lru.containsKey(key)) {
			Integer t = lru.get(key);
			reverse.remove(t);
		}
		lru.put(key, counter++);
		
		reverse.put(counter, key);
		if (counter > TIME_OUT) {
			Free();
		}
	}
	public boolean Exists(String key) {
		return cache.containsKey(key);
	}
	public byte[] Get(String key){
		return cache.get(key);
	}
	private void Free(){
		Integer s = cache.size();
		System.out.println("===============================FREEING===========================================");
		while(s > CACHE_SIZE) {
			Integer t = reverse.Min();
			String toRemove = reverse.get(t);
			cache.remove(toRemove);
			lru.remove(toRemove);
			reverse.remove(t);	
			s = cache.size();
		}		
	}
	
}

class HashMapWithMin<K, V extends Comparable> extends HashMap<K, V> {    
    V lowestValue;
    K lowestValueKey;    
    public V put(K k, V v) {
      if (v.compareTo(lowestValue) < 0) {
        lowestValue = v; 
        lowestValueKey = k;
      }
      return super.put(k, v);
    }
    K Min () { return lowestValueKey; }
}
