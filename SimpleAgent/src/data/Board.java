package data;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Board
{
	ConcurrentHashMap<String,BoardObject> objectHm = null;
	/**
	 * Expire time in ms after that a @see BoardObject will be obsolete.
	 */
	long timeout = 3600000;

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
		removeIfObsolete();
		if (key != null && newObject != null)
			return objectHm.put(key, newObject);
		else
			return null;
	}
	
	/**
	 * 
	 * @param key
	 * @return The @ref BoardObject for the key or null if not found on the board.
	 */
	public BoardObject getObject(String key) {
		removeIfObsolete();
		return objectHm.get(key);
	}
	
	/**
	 * 
	 * @param key @ref BoardObject identifier.
	 * @return Previous @ref BoardObject or null if it was not found.
	 */
	public BoardObject removeObject(String key) {
		removeIfObsolete();
		return objectHm.remove(key);
	}
	
	/**
	 * Empties the internal data structure.
	 */
	public void clear() {
		objectHm.clear();
		// TODO block access methods
	}
	
	public Iterator<Entry<String, BoardObject>> getIterator () {
		removeIfObsolete();
		return objectHm.entrySet().iterator();
	}
	public Set<Entry<String,BoardObject>> getSet() {
		removeIfObsolete();
		return objectHm.entrySet();
	}

	/**
	 * @return The timeout of a @see BoardObject in ms before it will be considered obsolete.
	 */
	public long getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout The timeout in ms of a @see BoardObject before it will be considered obsolete.
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	void removeIfObsolete(String key) {
		long bots = getObject(key).getTimestamp();
		long curTime = System.currentTimeMillis();
		
		/** Check for BoardObject and Board timeout */
		if ( ((bots + getObject(key).getTimeout()) < curTime) || ((bots + getTimeout()) < curTime) )
		{
			removeObject(key);
		}
	}
	void removeIfObsolete() {
		/** loop through board objects */
		Iterator<Entry<String, BoardObject>> it = getIterator();
		while (it.hasNext()) {
			Map.Entry<String, BoardObject> me = it.next();
			String key = me.getKey();
			removeIfObsolete(key);
		}
	}
}
