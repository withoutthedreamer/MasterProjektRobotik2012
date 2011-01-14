package core;

import java.io.*;

public class OSCommand implements Runnable{
	
	protected String[] command = null;
	protected Process process = null;
	
	// Every class of this type has it's own thread
	protected Thread thread = new Thread ( this );
	
	protected boolean isRunning = false;
	private boolean isTerminated = false;

	public OSCommand (String[] cmd) {
		command = cmd;
		// Automatically start own thread in constructor
		thread.start();
		
		Logger.logActivity(false, "Running", this.toString(), -1, thread.getName());
	}

	public String exec (String[] cmd) {
		StringBuffer result = new StringBuffer();
		String s = null;
		try {
			isRunning = true;
			process = Runtime.getRuntime().exec(cmd);
			isRunning = false;
			// Process console output handling
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			while ( (s = in.readLine()) != null ) {
				result.append(s + "\n");
			}
			in.close();
			// Error output
			in = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ( (s = in.readLine()) != null ) {
				result.append(s + "\n");
			}
			in.close();
			
		} catch (IOException e) {
			// Do nothing here because exception occures regularly when process is orderly terminated
			if (isTerminated != true) {
				// Something unexpected has happened
				e.printStackTrace();
			}
		}
		return result.toString();
	}

	@Override
	public void run() {
		Logger.logActivity(false, exec(command), this.toString(), -1, thread.getName());
	}
	public void terminate() {
		// orderly termination
		isTerminated = true;
		// TODO return command result
		if (process != null) {
			process.destroy();
		}
		Logger.logActivity(false, "Terminating", this.toString(), -1, thread.getName());
	}
	public boolean isRunning () {
		return isRunning;
	}
	/**
	 * Blocks until the underlying process is finished or interrupted.
	 */
	public void waitFor() {
		if (process != null) {
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//			e.printStackTrace();
			}
		}
	}
}
