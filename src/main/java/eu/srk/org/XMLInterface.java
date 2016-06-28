/**
 * @author Theo den Exter, ARS
 * Date: June 25th 2016
 * Version 1.0
 *
 * Conversion to and from CMD 1.0.2 without ramping on/off
 *
 * History
 *
 */

// CMD issue (RWv1.0.2.xsd): 
// Note: RIS messages are missing
// Note: change of data is only 1 record instead of all records
// Note: discern track identification/change data is missing
// Note: Video mode select is missing
// Note: Request for status on connection is now implemented as an empty RW_Message

// Onramp has to be implemented

package eu.srk.org;

public class XMLInterface {

	public XMLInterface() {

	}

	public static String stringToXML(String input) {
		LoggerObject logs;
		logs = LoggerObject.getInstance();
		logs.logDebug("Receiving message: " + input);
		return input;
	}

	public static String xmlToString(String input) {

		LoggerObject logs;
		logs = LoggerObject.getInstance();
		logs.logDebug("Intended Sending message: " + input);
		return input;
	}

	public static void main(String[] args) {


	}
}