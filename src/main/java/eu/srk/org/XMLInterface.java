/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Binary class for RW-interface
 *
 * History
 *
 */
package eu.srk.org;

public class XMLInterface {

	public XMLInterface() {

	}

	public static String stringToXML(String input) {
		System.out.println("Receiving message: " + input);
		return "";
	}

	public static String xmlToString(String input) {
		System.out.println("Intended Sending message: " + input);
		return input;
	}

}