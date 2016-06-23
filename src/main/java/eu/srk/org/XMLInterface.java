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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;

import eu.srk.org.jms.*;

public class XMLInterface {

	public XMLInterface() {

	}

	public static String stringToXML(String input) {
		System.out.println("Receiving message: " + input);
		Properties defaultProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("src/main/resources/jms.properties");
			defaultProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JMSSender t;
		try {
			t = new JMSSender(defaultProps);
			t.sendMessage(input);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String xmlToString(String input) {
		System.out.println("Intended Sending message: " + input);
		Properties defaultProps = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("src/main/resources/jms.properties");
			defaultProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JMSConsumer t;
		try {
			t = new JMSConsumer(defaultProps);
			t.sendMessage(input);
		} catch (JMSException e) {
			e.printStackTrace();
		}	
		
		return input;
	}

}