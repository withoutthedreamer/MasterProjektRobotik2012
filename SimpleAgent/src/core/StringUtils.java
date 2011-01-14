package core;

import java.util.Iterator;

public class StringUtils {
	/**
	 * Taken from http://stackoverflow.com/questions/794248/a-method-to-reverse-effect-of-java-string-split/3629711#3629711
	 * @param elements Strings to put together
	 * @param separator Character to put between Strings
	 * @return Joined string.
	 */
	public static String join(Iterable<? extends Object> elements, CharSequence separator) 
	{
	    StringBuilder builder = new StringBuilder();

	    if (elements != null)
	    {
	        Iterator<? extends Object> iter = elements.iterator();
	        if(iter.hasNext())
	        {
	            builder.append( String.valueOf( iter.next() ) );
	            while(iter.hasNext())
	            {
	                builder
	                    .append( separator )
	                    .append( String.valueOf( iter.next() ) );
	            }
	        }
	    }

	    return builder.toString();
	}
}
