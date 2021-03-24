/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Data logger, Singleton
 *
 * History
 *
 */
package eu.srk.org;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class LoggerObject {

	static final Logger LOG = Logger.getLogger("RW Interface");;

	private static LoggerObject instance = null;

	protected LoggerObject() {
	}

	public static LoggerObject getInstance() {
		if (instance == null) {
			instance = new LoggerObject();
		}
		return instance ;
	}

	//hallo
	
	public void logDebug(String text) {
		if (LOG.isInfoEnabled())
			LOG.info("Debug: " + text);
	}
	
	public void logInfo(String text) {
		if (LOG.isInfoEnabled())
			LOG.info("Info: " + text);
	}

	public void logError(String text) {
		if (LOG.isEnabledFor(Level.ERROR))
			LOG.error("Error: " + text);
	}
}
