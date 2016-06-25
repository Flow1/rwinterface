/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Start class of application
 *
 * History
 *
 */

package eu.srk.org;

public class Main {

	public Main() {
	}

	// #####################################################################################################################
	// Main
	// #####################################################################################################################

	public static void main(String[] args) {
	
		PropertiesObject props = PropertiesObject.getInstance();
		props.loadProperties("src/main/resources/config.properties");

		LoggerObject logs = LoggerObject.getInstance();

		String server = props.getProperty("server");
		String portListen = props.getProperty("portlisten");
		String portWrite = props.getProperty("portwrite");

		logs.logInfo("Start application: " + server + ":" + portListen + " "
				+ portWrite);

		if (server == null) {
			logs.logError("Servername in properties not found");
			System.exit(0);
		}

		if (portListen == null) {
			logs.logError("Listen port in properties not found");
			System.exit(0);
		}

		if (portWrite == null) {
			logs.logError("Write port in properties not found");
			System.exit(0);
		}

		Engine t = new Engine(server, portListen, portWrite);
		try {
			t.run();
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}