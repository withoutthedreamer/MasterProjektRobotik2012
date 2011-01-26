package data;

import java.util.concurrent.ConcurrentHashMap;


public class Board
{
	ConcurrentHashMap<String,BoardObject> objectHm = null;

	public Board() {
		objectHm = new ConcurrentHashMap<String,BoardObject>();
	}
	
	/**
	 * 
	 * @param key
	 * @param newObject
	 * @return Previous @ref BoardObject or null if it was not found.
	 */
	public BoardObject addObject(String key, BoardObject newObject){
		return objectHm.put(key, newObject);
	}
	
	/**
	 * 
	 * @param key @ref BoardObject identifier.
	 * @return Previous @ref BoardObject or null if it was not found.
	 */
	public BoardObject removeObject(String key) {
		return objectHm.remove(key);
	}
	
	/**
	 * Empties the internal data structure.
	 */
	public void clear() {
		objectHm.clear();
	}
	
}
