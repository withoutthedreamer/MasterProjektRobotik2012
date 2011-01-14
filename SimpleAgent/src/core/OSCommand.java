package core;

import java.io.*;

public class OSCommand implements Runnable{
	
	protected String[] command = null;
	protected Process process = null;
	
	// Every class of this type has it's own thread
	protected Thread thread = new Thread ( this );
	
	protected boolean isRunning = false;

	public OSCommand (String[] cmd) {
		command = cmd;
		// Automatically start own thread in constructor
		thread.start();
	}

	public String exec (String[] cmd) {
		StringBuffer result = new StringBuffer();
//		String s = null;
		try {
			isRunning = true;
			process = Runtime.getRuntime().exec(cmd);
			isRunning = false;
			// Process console output handling
//			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//			while ( (s = in.readLine()) != null ) {
//				result.append(s);
//			}
//			in.close();
		} catch (IOException e) {
			// TODO Nicer way to kill a process would be much appreciated
			// Do nothing here because exception occures regularly when process is orderly terminated
			// e.printStackTrace();
		}
		return result.toString();
	}

	@Override
	public void run() {
		exec(command);
	}
	public void terminate() {
		// TODO return command result
		if (process != null) {
			process.destroy();
		}
	}
	public boolean isRunning () {
		return isRunning;
	}
}