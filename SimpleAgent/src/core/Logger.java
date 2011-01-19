package core;
// TODO wrap message service
public class Logger {
	//TODO log device
	public static synchronized void logActivity(boolean isError, String event, String objName, int id, String thread) {
		
		int otherId = -1;
		final String otherThread = "no thread";
		String threadName = null;

		if (id >= 0) { otherId = id; }
		if (thread == null) {
			threadName = otherThread;
		} else {
			threadName = thread;
		}
		String logMsg = event + " of " + objName + " of device " + otherId + " in " + threadName;

		if (isError == true) {
			System.err.println(logMsg);
		} else {
			System.out.println(logMsg);
		}
	}
}
