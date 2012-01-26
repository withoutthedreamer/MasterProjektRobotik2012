/**
 * 
 */
package test.data;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import data.BlobfinderBlob;

/**
 * @author sebastian
 *
 */
public class BlobfinderBlobTest extends TestCase
{
	BlobfinderBlob blob;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before public void setUp() throws Exception
	{
		blob = new BlobfinderBlob(0xFF0000, 12, 5, 5, 4, 6, 4, 6, 2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link data.BlobfinderBlob#BlobfinderBlob(int, int, int, int, int, int, int, int, float)}.
	 */
	@Test public void testBlobfinderBlob()
	{
		assertNotNull(blob);
	}

	/**
	 * Test method for {@link data.BlobfinderBlob#toString()}.
	 */
	@Test public void testToString()
	{
		System.out.println(""+blob);
	}

	/**
	 * Test method for {@link data.BlobfinderBlob#getAngle(double, int)}.
	 */
	@Test public void testGetAngle()
	{
		System.out.println(""+blob.getAngle(Math.PI, 100));
	}

	/** To use JUnit  test suite */
    public static JUnit4TestAdapter suite()
    { 
       return new JUnit4TestAdapter(BlobfinderBlobTest.class); 
    }
}
