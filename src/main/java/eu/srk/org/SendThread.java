/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Sender from IVS to RW, receives from queue
 *
 * History
 *
 */

package eu.srk.org;

import java.io.*;
import java.lang.Thread;

class SendThread extends Thread {
	DataInputStream is;
	DataOutputStream os;

	ClientSocketManagement socket;

	LoggerObject logs;

	boolean error = false;

	Queue qu;

	public SendThread() {
	}

	public void run() {
		logs = LoggerObject.getInstance();
		socket = ClientSocketManagement.getInstance();

		qu = Queue.getInstance();

		error = false;
		while (!error) {
			os = socket.getDataOutputStream();
			error = (os == null);

			if (!error) {
				if (qu.queueEmpty()) {
					// Unfortunately busy-waiting to enable quick reconnect
					// after a disconnect
					try {
						Thread.sleep(300);
					} catch (InterruptedException ie) {
						// Handle exception
					}
				} else {
					Object o = qu.getQueue();

					try {
						Thread.sleep(300);
					} catch (InterruptedException ie) {
						// Handle exception
					}
					if (o.getClass().getName() == "java.lang.String") {
						sendData((String) o);
					}
				}
			}
		}
	}

	// Disconnect connection
	public void disconnect() {
		os = null;
		error = true;
		socket.disconnect();
	}

	// Send test
	public void sendTest() {
		try {
			while (true) {
				for (int k = 0; k <= 255; k++) {
					byte r = (byte) k;
					logs.logInfo(r + " - " + (char) r);
					os.write(r);
				}
			}
		} catch (IOException e) {
			disconnect();
		}
	}

	public void sendData(String command) {
		logs.logDebug("Sending: " + command);
		String command1 = XMLInterface.xmlToString(command);
		String[] parts = command1.split(";");

		if (parts[0].equals("Track Identification")) {
			sendTrackInformation(parts);
		} else if (parts[0].equals("Track Identification Extended")) {
			sendTrackInformationExtended(parts);
		} else if (parts[0].equals("Track Identification RIS")) {
			sendTrackInformationRIS(parts);
		} else if (parts[0].equals("Change of travel data")) {
			sendWijzigenInformatie(parts);
		} else if (parts[0].equals("Change of travel data Extended")) {
			sendWijzigenInformatieExtended(parts);
		} else if (parts[0].equals("Change of travel data RIS")) {
			sendWijzigenInformatieRIS(parts);
		} else if (parts[0].equals("Video Mode Select")) {
			sendVideoMode(parts);
		} else if (parts[0].equals("InfoConnectionRequest")) {
			sendInfoConnection(parts);
		} else {
			logs.logInfo("Unknown command: " + parts[0]);
		}

	}

	// sendTrackInformation
	public void sendTrackInformation(String[] k) {
		try {
			Binary t = new Binary();
			logs.logDebug("Physical send Identify Track");
			int b = 1;
			byte[] c = t.toByteArray1(b);
			os.write(c, 0, 4);

			int j = 1;

			// TravelID
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship Label
			String s = k[j++] + "   ";
			c[0] = (byte) s.charAt(0);
			c[1] = (byte) s.charAt(1);
			c[2] = (byte) s.charAt(2);
			c[3] = (byte) s.charAt(3);

			os.write(c, 0, 4);

			// Ship lenght
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship width
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship depth
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// DpID
			c[0] = 0;
			c[1] = 0;
			c[2] = t.intToByte(Integer.valueOf(k[j++]));

			// Attributes
			c[3] = 0;
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x10);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x08);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x04);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x02);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x01);
			os.write(c, 0, 4);

		} catch (IOException e) {
			disconnect();
		}

	}

	// sendTrackInformation
	public void sendTrackInformationExtended(String[] k) {
		try {
			Binary t = new Binary();
			logs.logDebug("Physical send Identify Track Extended");
			int b = 4;

			byte[] c = t.toByteArray1(b);
			os.write(c, 0, 4);

			// TravelID
			int j = 1;
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Shipslabel
			String s = k[j++] + "   ";
			c[0] = (byte) s.charAt(0);
			c[1] = (byte) s.charAt(1);
			c[2] = (byte) s.charAt(2);
			c[3] = (byte) s.charAt(3);

			os.write(c, 0, 4);

			// Shipsname
			String s1 = k[j++] + "               ";
			c[0] = (byte) s1.charAt(0);
			c[1] = (byte) s1.charAt(1);
			c[2] = (byte) s1.charAt(2);
			c[3] = (byte) s1.charAt(3);
			os.write(c, 0, 4);

			c[0] = (byte) s1.charAt(4);
			c[1] = (byte) s1.charAt(5);
			c[2] = (byte) s1.charAt(6);
			c[3] = (byte) s1.charAt(7);
			os.write(c, 0, 4);

			c[0] = (byte) s1.charAt(8);
			c[1] = (byte) s1.charAt(9);
			c[2] = (byte) s1.charAt(10);
			c[3] = (byte) s1.charAt(11);
			os.write(c, 0, 4);

			c[0] = (byte) s1.charAt(12);
			c[1] = (byte) s1.charAt(13);
			c[2] = (byte) s1.charAt(14);
			c[3] = (byte) s1.charAt(15);
			os.write(c, 0, 4);

			// Length
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Width
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Depth
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// DpID
			c[0] = 0;
			c[1] = 0;
			c[2] = t.intToByte(Integer.valueOf(k[j++]));

			// Attributes
			c[3] = 0;
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x10);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x08);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x04);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x02);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x01);
			os.write(c, 0, 4);

		} catch (IOException e) {
			logs.logInfo("Error in verzending track info");
			disconnect();
		}
	}

	// sendTrackInformationRis
	public void sendTrackInformationRIS(String[] k) {
		try {
			Binary t = new Binary();
			logs.logDebug("Physical send Identify Track Ris");
			int b = 6;
			byte[] c = t.toByteArray1(b);
			os.write(c, 0, 4);

			int j = 1;

			// TravelID
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship Label
			String s = k[j++] + "   ";
			c[0] = (byte) s.charAt(0);
			c[1] = (byte) s.charAt(1);
			c[2] = (byte) s.charAt(2);
			c[3] = (byte) s.charAt(3);
			os.write(c, 0, 4);

			// Ship Name
			char r = (char) 0;
			s = k[j++] + r + r + r + r;
			// Cut-off in multiples of 4
			int q = s.length() - s.length() % 4;

			int i1 = 0;
			for (int l = 0; l < q; l = l + 4) {
				i1++;
			}

			c = t.toByteArray1(i1);
			os.write(c, 0, 4);

			for (int l = 0; l < q; l = l + 4) {
				String w = s.substring(l, l + 4);
				c[0] = (byte) w.charAt(0);
				c[1] = (byte) w.charAt(1);
				c[2] = (byte) w.charAt(2);
				c[3] = (byte) w.charAt(3);
				os.write(c, 0, 4);
			}

			// Ship lenght
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship width
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship depth
			c = t.toByteArray1(Integer.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship MMSI
			c = t.toByteArray1Long(Long.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship IMO
			c = t.toByteArray1Long(Long.valueOf(k[j++]));
			os.write(c, 0, 4);

			// Ship Euro
			c = t.toByteArray1Long(Long.valueOf(k[j++]));
			os.write(c, 0, 4);

			// DpID
			c[0] = 0;
			c[1] = 0;
			c[2] = t.intToByte(Integer.valueOf(k[j++]));

			// Attributes
			c[3] = 0;
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x10);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x08);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x04);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x02);
			if (k[j++].equals("true"))
				c[3] = t.intToByte(t.byteToInt(c[3]) | 0x01);
			os.write(c, 0, 4);

		} catch (IOException e) {
			disconnect();
		}
	}

	// sendWijzigenInformatie
	public void sendWijzigenInformatie(String[] k) {
		try {
			Binary t = new Binary();
			logs.logDebug("Physical send Change Journey");

			// 1;5;NEEL;20;15;124;true;false;true;true;false";
			int b = 2;

			byte[] c = t.toByteArray1(b);
			os.write(c, 0, 4);

			// Number of entries
			int i = Integer.valueOf(k[1]);

			c = t.toByteArray1(i);
			os.write(c, 0, 4);

			int j = 2;

			for (int k1 = 0; k1 < i; k1++) {
				// TravelID
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// Ship label
				String s = k[j++] + "   ";
				c[0] = (byte) s.charAt(0);
				c[1] = (byte) s.charAt(1);
				c[2] = (byte) s.charAt(2);
				c[3] = (byte) s.charAt(3);

				os.write(c, 0, 4);

				// length
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// width
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// depth
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// DpID
				c[0] = 0;
				c[1] = 0;
				c[2] = t.intToByte(Integer.valueOf(k[j++]));

				// Attributes
				c[3] = 0;
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x10);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x08);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x04);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x02);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x01);
				os.write(c, 0, 4);
			}

		} catch (IOException e) {
			disconnect();
		}

	}

	// sendWijzigenInformatie extended
	public void sendWijzigenInformatieExtended(String[] k) {
		try {
			Binary t = new Binary();
			logs.logDebug("Physical send Change Journey Extended");
			int b = 5;
			byte[] c = t.toByteArray1(b);
			os.write(c, 0, 4);

			// Number of entries
			int i = Integer.valueOf(k[1]);

			c = t.toByteArray1(i);
			os.write(c, 0, 4);

			int j = 2;
			for (int k1 = 0; k1 < i; k1++) {

				// TravelID
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// Ships label
				String s = k[j++] + "   ";
				c[0] = (byte) s.charAt(0);
				c[1] = (byte) s.charAt(1);
				c[2] = (byte) s.charAt(2);
				c[3] = (byte) s.charAt(3);

				os.write(c, 0, 4);

				// Ships name
				String s1 = k[j++] + "               ";
				c[0] = (byte) s1.charAt(0);
				c[1] = (byte) s1.charAt(1);
				c[2] = (byte) s1.charAt(2);
				c[3] = (byte) s1.charAt(3);
				os.write(c, 0, 4);

				c[0] = (byte) s1.charAt(4);
				c[1] = (byte) s1.charAt(5);
				c[2] = (byte) s1.charAt(6);
				c[3] = (byte) s1.charAt(7);
				os.write(c, 0, 4);

				c[0] = (byte) s1.charAt(8);
				c[1] = (byte) s1.charAt(9);
				c[2] = (byte) s1.charAt(10);
				c[3] = (byte) s1.charAt(11);
				os.write(c, 0, 4);

				c[0] = (byte) s1.charAt(12);
				c[1] = (byte) s1.charAt(13);
				c[2] = (byte) s1.charAt(14);
				c[3] = (byte) s1.charAt(15);
				os.write(c, 0, 4);

				// lenght
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// width
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// depth
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// DpID
				c[0] = 0;
				c[1] = 0;
				c[2] = t.intToByte(Integer.valueOf(k[j++]));

				// Attributes
				c[3] = 0;
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x10);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x08);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x04);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x02);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x01);
				os.write(c, 0, 4);

			}

		} catch (IOException e) {
			disconnect();
		}
	}

	// sendWijzigenInformatieRis
	public void sendWijzigenInformatieRIS(String[] k) {
		try {
			Binary t = new Binary();
			logs.logDebug("Physical send Change Journey RIS");

			int b = 7;

			byte[] c = t.toByteArray1(b);
			os.write(c, 0, 4);

			// Number of entries
			int i = Integer.valueOf(k[1]);

			c = t.toByteArray1(i);
			os.write(c, 0, 4);

			// logs.logDebug(Integer.toString(i));

			int j = 2;

			for (int k1 = 0; k1 < i; k1++) {
				// TravelID
				// logs.logDebug(k[j]);
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// Ship label
				String s = k[j++] + "   ";
				c[0] = (byte) s.charAt(0);
				c[1] = (byte) s.charAt(1);
				c[2] = (byte) s.charAt(2);
				c[3] = (byte) s.charAt(3);

				os.write(c, 0, 4);

				// Ship Name
				char r = (char) 0;
				s = k[j++] + r + r + r + r;
				// Cut-off in multiples of 4
				int q = s.length() - s.length() % 4;

				int i1 = 0;
				for (int l = 0; l < q; l = l + 4) {
					i1++;
				}

				c = t.toByteArray1(i1);
				os.write(c, 0, 4);

				for (int l = 0; l < q; l = l + 4) {
					String w = s.substring(l, l + 4);
					c[0] = (byte) w.charAt(0);
					c[1] = (byte) w.charAt(1);
					c[2] = (byte) w.charAt(2);
					c[3] = (byte) w.charAt(3);
					os.write(c, 0, 4);
				}

				// length
				// logs.logDebug(k[j]);
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// width
				// logs.logDebug(k[j]);
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// depth
				// logs.logDebug(k[j]);
				c = t.toByteArray1(Integer.valueOf(k[j++]));
				os.write(c, 0, 4);

				// Ship MMSI
				// logs.logDebug(k[j]);
				c = t.toByteArray1Long(Long.valueOf(k[j++]));
				os.write(c, 0, 4);

				// Ship IMO
				c = t.toByteArray1Long(Long.valueOf(k[j++]));
				os.write(c, 0, 4);

				// Ship Euro
				// logs.logDebug(k[j]);
				c = t.toByteArray1Long(Long.valueOf(k[j++]));
				os.write(c, 0, 4);

				// DpID
				c[0] = 0;
				c[1] = 0;
				c[2] = 0;
				// c[2] = t.intToByte(Integer.valueOf(k[j++]));

				// Attributes
				c[3] = 0;
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x10);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x08);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x04);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x02);
				if (k[j++].equals("true"))
					c[3] = t.intToByte(t.byteToInt(c[3]) | 0x01);
				os.write(c, 0, 4);
			}

		} catch (IOException e) {
			disconnect();
		}

	}

	// sendVideoMode
	public void sendVideoMode(String[] k) {
		try {
			Binary t = new Binary();
			logs.logDebug("Physical send Set Video Mode");
			int b = 3;

			byte[] c = t.toByteArray1(b);
			os.write(c, 0, 4);

			c = t.toByteArray1(Integer.valueOf(k[1]));
			os.write(c, 0, 4);

			c[0] = 0;
			c[1] = 0;
			// DPiD
			c[2] = t.intToByte(Integer.valueOf(k[2]));
			// Video mode
			c[3] = Byte.valueOf(k[3]);
			os.write(c, 0, 4);

		} catch (IOException e) {
			disconnect();
		}

	}

	// sendInfoConnection
	public void sendInfoConnection(String[] k) {

		String status = "";
		if (error) {
			status = "disconnected";
		} else {
			status = "connected";
		}
		String reply = "ReplyConnectionRequest;" + status;

		String result = XMLInterface.stringToXML(reply);

	}
}
