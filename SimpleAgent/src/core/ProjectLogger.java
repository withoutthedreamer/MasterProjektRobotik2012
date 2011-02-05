package core;

import java.util.logging.Logger;

import device.Device;

// TODO wrap message service
public class ProjectLogger
{
    // Logging support
    private static Logger logger = Logger.getLogger (ProjectLogger.class.getName ());

	/**
	 * Log an event.
	 * @param isError
	 * @param event
	 * @param objName
	 * @param id
	 * @param thread
	 */
	public static synchronized void logActivity(boolean isError, String event, String objName, int id, String thread) {
		
		String otherId = "";
		final String otherThread = "";
		String threadName = null;

		if (id >= 0) {
			otherId = " of device " + new Integer(id).toString(); 
		} else {
			otherId = "";
		}
		if (thread == null) {
			threadName = otherThread;
		} else {
			threadName = " in " + thread;
		}
		String logMsg = event + " of " + objName + otherId + threadName;

		if (isError == true) {
			logger.warning(logMsg);
		} else {
			logger.info(logMsg);
		}
	}
	/**
	 * Log an event a given device object is taken part in.
	 * @param isError
	 * @param event
	 * @param objName
	 */
	public static synchronized void logDeviceActivity (boolean isError, String event, Device objName) {
		if (event != null && objName != null) {
			String threadName = objName.getThreadName();
			if (threadName == null) {
				threadName = new String ("no thread");
			}
				
			String logMsg = event + " of "
			+ objName.toString()
			+ " @[" + objName.getHost() + "," + objName.getPort() + "," + objName.getDeviceNumber() + "]"
			+ " in " + threadName;

			if (isError == true) {
				logger.warning(logMsg);
			} else {
				logger.info(logMsg);
			}
		}
	}
}
