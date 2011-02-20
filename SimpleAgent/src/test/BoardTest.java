/**
 * 
 */
package test;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import data.Board;
import data.BoardObject;

/**
 * @author sebastian
 *
 */
public class BoardTest extends TestCase {

	static Board b;
	static BoardObject bo;
	
	/**
	 * Test method for {@link data.Board#Board()}.
	 */
	@Test
	public void testBoard() {
		b = new Board();
		assertNotNull(b);
	}

	/**
	 * Test method for {@link data.Board#addObject(java.lang.String, data.BoardObject)}.
	 */
	@Test
	public void testAddObject() {
		bo = new BoardObject();
		assertNotNull(bo);
		
		assertNull( b.addObject("test1", bo) );
	}

	/**
	 * Test method for {@link data.Board#getObject(java.lang.String)}.
	 */
	@Test
	public void testGetObject() {
		assertNotNull( b.getObject("test1"));
		
		assertNull( b.getObject("foo"));
	}

	/**
	 * Test method for {@link data.Board#removeObject(java.lang.String)}.
	 */
	@Test
	public void testRemoveObject() {
		assertNotNull( b.removeObject("test1") );
		
		assertNull( b.removeObject("foo") );
	}

		/**
	 * Test method for {@link data.Board#getIterator()}.
	 */
	@Test
	public void testGetIterator() {
		Iterator<Entry<String, BoardObject>> it = b.getIterator();
		
		assertNotNull(it);
	}

	/**
	 * Test method for {@link data.Board#getSet()}.
	 */
	@Test
	public void testGetSet() {
		Set<Entry<String, BoardObject>> set = b.getSet();
		
		assertNotNull(set);
	}

	/**
	 * Test method for {@link data.Board#clear()}.
	 */
	@Test
	public void testClear() {
		b.clear();
		
		testAddObject();
		
		testGetObject();
	}
}
