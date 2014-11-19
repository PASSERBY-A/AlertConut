package util.jxl;

import java.util.HashMap;

public class Dto<K,V> extends HashMap<K, V>{

	private static final long serialVersionUID = 5937630817102419832L;
	
	
	public Dto<K,V> add(K key,V value)
	{
		this.put(key, value);
		
		return this;
		
	}
	
	
	
}
