/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Reading property files. Singleton
 *
 * History
 *
 */

package eu.srk.org;

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesObject {

	private static Properties defaultProps;

	private static PropertiesObject instance = null;

	protected PropertiesObject() {
	}

	public static PropertiesObject getInstance() {
		if (instance == null) {
			instance = new PropertiesObject();
		}
		return instance;
	}

	public static void loadProperties(String filename) {
		defaultProps = new Properties();

		try {
			FileInputStream in = new FileInputStream(filename);
			defaultProps.load(in);
			in.close();
		} catch (Exception e) {
			;
		}
	}

	public static String getProperty(String key) {
		return defaultProps.getProperty(key);
	}

}
