/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Class for interfacing received message from JMS to RW
 *
 * Note: currently contains teststub
 *
 * History
 *
 */

package eu.srk.org;

import java.util.Random;
import java.lang.Thread;

class SubmitTestRequests extends Thread {

	static LoggerObject logs;

	public SubmitTestRequests() {
	}

	public void run() {
		logs = LoggerObject.getInstance();

		Queue qu = Queue.getInstance();

		// Here we insert for testing reasons String messages (see IRS
		// documentation),
		// however CDM XML is required with completion of
		// XMLInterface.XMLToString()
		Random randomGenerator = new Random();

		int randomInt = randomGenerator.nextInt(100);

		String travelID = "10516001";
		String dpID = "45";

		String exampleVideoMode1 = "Video Mode Select;" + travelID + ";" + dpID
				+ ";0";
		String exampleVideoMode2 = "Video Mode Select;" + travelID + ";" + dpID
				+ ";1";
		String exampleChangeTravel1 = "Change of travel data;1;" + travelID
				+ ";NEEL;20;15;124;12;true;false;true;true;false";
		String exampleChangeTravel2 = "Change of travel data Extended;1;"
				+ travelID
				+ ";NEEL;NEELTJE JANS;20;15;124;12;true;false;true;true;false";
		String exampleTrackID1 = "Track Identification Extended;" + travelID
				+ ";NEEL;NEELTJE JANS;20;15;124;12;true;false;true;true;false";
		String exampleTrackID2 = "Track Identification;" + travelID
				+ ";NEEL;20;15;124;12;true;false;true;true;false";
		String exampleTrackID3 = "Track Identification RIS;" + travelID
				+ ";NEEL;NEELTJE JANS VERY LONG NAME;20;15;124;100;200;300;12;true;false;true;true;false";
		String exampleChangeTravel3 = "Change of travel data RIS;1;"
				+ travelID
				+ ";NEEL;NEELTJE JANS VERY LONG NAME;20;15;124;100;200;300;12;true;false;true;true;false";
		String exampleConnection = "InfoConnectionRequest";
		
		
		while (true) {
			randomInt = randomGenerator.nextInt(9);
			if (randomInt == 1)
				qu.putQueue(exampleVideoMode1);
			if (randomInt == 2)
				qu.putQueue(exampleVideoMode2);
			if (randomInt == 3)
				qu.putQueue(exampleChangeTravel1);
			if (randomInt == 4)
				qu.putQueue(exampleChangeTravel2);
			if (randomInt == 5)
				qu.putQueue(exampleTrackID1);
			if (randomInt == 6)
				qu.putQueue(exampleTrackID2);
			if (randomInt == 7)
				qu.putQueue(exampleTrackID3);
			if (randomInt == 8)
				qu.putQueue(exampleChangeTravel3);
			if (randomInt == 9)
				qu.putQueue(exampleConnection);
			try {
				Thread.sleep(randomInt * 5000);
			} catch (InterruptedException ie) {
				;
			}
		}
	}

}
