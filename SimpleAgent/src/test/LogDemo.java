package test;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogDemo
{
	private static final Logger logger = Logger.getLogger( "Logging" );
	
	public static void main (String[] args) throws IOException
	{
	
//        System.setProperty("java.util.logging.config.file","/Users/sebastian/robotcolla/SimpleAgent/logging.properties");

        ConsoleHandler ch = new ConsoleHandler();
		
		logger.addHandler(ch);
		
		logger.setLevel(Level.FINEST);
		ch.setLevel(Level.FINEST);
		System.err.println("log level: "+logger.getLevel().toString());
		
		logger.fine(" Dann mal Los.");
		logger.info("info");
		logger.finer("finer");
		logger.severe("severe");
		logger.warning("warning");
		logger.config("config");
	}
}
