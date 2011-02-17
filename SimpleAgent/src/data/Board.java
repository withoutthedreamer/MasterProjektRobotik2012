package data;

import java.util.ArrayList;
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
	/**
	 * Not public methods must be called here!
	 * Due to recursion.
	 */
	void removeIfObsolete(String key) {
		long bots = objectHm.get(key).getTimestamp(); /** Board Object TimeStamp */
		long curTime = System.currentTimeMillis();
		
		/** Check for BoardObject and Board timeout */
		if ( ((bots + objectHm.get(key).getTimeout()) < curTime) || ((bots + getTimeout()) < curTime) )
		{
			objectHm.remove(key);
		}
	}
	/**
	 * Not public methods must be called here!
	 * Due to recursion.
	 */
	void removeIfObsolete() {
		/** loop through board objects */
		Iterator<Entry<String, BoardObject>> it = objectHm.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, BoardObject> me = it.next();
			String key = me.getKey();
			removeIfObsolete(key);
		}
	}
	/**
	 * @param topic String identifying the topic.
	 * @return An @see ArrayList of all board keys belonging to the given topic.
	 * Usually the topic is the Object class name.
	 */
	public ArrayList<String> getTopicList (String topic) {
		removeIfObsolete();
		
		ArrayList<String> topicKeys = new ArrayList<String>();
		
		Iterator<Entry<String, BoardObject>> it = getIterator();
		
		while (it.hasNext()) {
			Entry<String, BoardObject> entry = it.next();
			
			String boTopic = entry.getValue().getName(); 
			
//			if (boTopic.equals(topic) == true);
			if (boTopic.equalsIgnoreCase(topic) == true)
				topicKeys.add(entry.getKey());
		}
		
		return topicKeys;
	}
}
