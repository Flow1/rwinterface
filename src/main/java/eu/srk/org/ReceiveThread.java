/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Receives messages from RW and submits to StringToXML
 * StringToXML sends them off to JMS Topic
 *
 * History
 *
 */

package eu.srk.org;

import java.io.*;
import java.lang.Thread;
import java.util.Properties;

import javax.jms.JMSException;

import eu.srk.org.jms.JMSSender;

class ReceiveThread extends Thread {
	DataInputStream is;

	final boolean MESSAGING = true;

	boolean error = false;
	JMSSender sender;
	static LoggerObject logs;
	ServerSocketManagement socket;

	public ReceiveThread() {
	}

	public void prepareSender() {
		logs = LoggerObject.getInstance();

		try {
			Properties prop = new Properties();
			InputStream inStream = new FileInputStream(
					"src/main/resources/jms.properties");
			prop.load(inStream);
			sender = new JMSSender(prop);

		} catch (JMSException e) {
			logs.logError(e.toString());
			System.exit(0);
		} catch (FileNotFoundException e) {
			logs.logError(e.toString());
			System.exit(0);
		} catch (IOException e) {
			logs.logError(e.toString());
			System.exit(0);
		}

	}

	public void run() {
		logs = LoggerObject.getInstance();
		StatusRequest sr = StatusRequest.getInstance();
		sr.getRequest();

		socket = ServerSocketManagement.getInstance();
		prepareSender();
		error = false;

		// Signal connect
		String res = sendInfoConnection();
		if (MESSAGING) {
			try {
				sender.sendMessage(res);
			} catch (JMSException e) {
				logs.logError(e.toString());
			}
		}
		
		while (!error) {

			is = socket.getDataInputStream();

			error = (is == null);

			while (!error) {
				logs.logInfo("Ready for receiving next data block");
				is = socket.getDataInputStream();
				int command = waitNextSerie(is);
				if (is != null) {
					String result = "";
					try {
						if (command == 101)
							result = processPositionReport(is);
						// if (command == 102)
						// result = processZichtReport(is);
						if (command == 105)
							result = processReisGeselekteerd(is);
						if (result != "") {
							String r = XMLInterface.stringToXML(result);
							if (MESSAGING) {
								try {
								sender.sendMessage(r);
							} catch (JMSException e) {
								logs.logError(e.toString());
							}
							}
						}
						// If coming from incoming request: obsolete
//						if (sr.getRequest()) {
//							sr.setRequest(false);
//							String res = sendInfoConnection().get(0);
//							if (MESSAGING) {
//								try {
//									sender.sendMessage(result);
//								} catch (JMSException e) {
//									logs.logError(e.toString());
//								}
//							}
//						}
					} catch (IOException e) {
						// Signal to reconnect
						disconnect();
					}
				}
			}
		}
		
		// Signal disconnect
		res = sendInfoConnection();
		if (MESSAGING) {
			try {
				sender.sendMessage(res);
			} catch (JMSException e) {
				logs.logError(e.toString());
			}
		}
	}

	
	// Disconnect connection
	public void disconnect() {
		is = null;
		error = true;
		socket.disconnect();
	}

	// Test Routine to dump incoming bytestream
	void displayIncoming(DataInputStream is) {
		while (true) {
			try {
				long starttime = System.currentTimeMillis();

				byte b = is.readByte();

				long endtime = System.currentTimeMillis();
				long difference = endtime - starttime;
				System.out.print(difference + " msec, byte=");
				System.out.format("%02X ", b);
				System.out.println();
			} catch (Exception e) {
				logs.logError(e.toString());
				disconnect();
			}
		}
	}

	// Read a 32 bit word
	byte[] getWord(DataInputStream is) throws IOException {
		byte[] r = new byte[4];
		r[0] = ' ';
		r[1] = ' ';
		r[2] = ' ';
		r[3] = ' ';
		try {
			r[0] = is.readByte();
			r[1] = is.readByte();
			r[2] = is.readByte();
			r[3] = is.readByte();
		} catch (Exception e) {
			logs.logError("Error reading RW Stream");
			disconnect();
		}
		return r;
	}

	// Wait for next bytestream
	int waitNextSerie(DataInputStream is) {
		boolean next = false;
		byte b = 1;
		try {
			while (!next) {
				boolean ready = false;
				while (!ready) {
					long starttime = System.currentTimeMillis();
					b = is.readByte();
					long endtime = System.currentTimeMillis();
					long difference = endtime - starttime;
					// System.out.print(difference+" msec, byte=");
					// System.out.format("%02X ",b);
					// System.out.println();
					if (difference > 200L)
						ready = true;
				}
				if (b == 0) {
					b = is.readByte();
					if (b == 0) {

						b = is.readByte();
						if (b == 0) {
							b = is.readByte();

							if ((b == 101) || (b == 102) || (b == 105)) {
								next = true;
							} else {
								logs.logInfo("Invalid command received: " + b);
								// System.out.print("Invalid command received:
								// ");
								// System.out.format("%02X ", b);
								// System.out.println();
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logs.logError("Error reading RW Stream");
			disconnect();
		}

		int i = b;
		return i;
	}

	// Process Incoming Position Report
	String processPositionReport(DataInputStream is) throws IOException {

		Binary t = new Binary();

		// PositionReport;3;5;-120;467;0;4;-546;6700;0;5;8900;347;1

		String result = "PositionReport";

		byte[] r = new byte[4];
		r = getWord(is);
		int nelem = t.fromByteArray1(r);
		result = result + ";" + Integer.toString(nelem);

		for (int i = 0; i < nelem; i++) {
			// reisID
			r = getWord(is);
			int k = t.fromByteArray1(r);
			result = result + ";" + Integer.toString(k);

			// XPos
			r = getWord(is);
			k = t.fromByteArray1(r);
			result = result + ";" + Integer.toString(k);

			// YPos
			r = getWord(is);
			k = t.fromByteArray1(r);
			result = result + ";" + Integer.toString(k);

			// Bijkomende reisinfo
			r = getWord(is);
			result = result + ";" + Integer.toString((int) r[3]);
		}
		return result;
	}

	// Process Incoming ZichtReport
	String processZichtReport(DataInputStream is) throws IOException {

		// SightMeter;2;B;0;G; Fog 2+3+4;99.0

		String result = "SightReport";
		byte[] r = new byte[4];

		byte b1;
		byte b2;
		byte b3;

		r = getWord(is);
		b1 = r[0];
		b2 = r[1];

		result = result + ";" + Integer.toString((int) r[3]);

		byte[] r1 = new byte[4];
		r1 = getWord(is);

		result = result + ";" + Integer.toString((int) r1[1]);
		result = result + ";" + Integer.toString((int) r[2]);
		result = result + ";" + Integer.toString((int) r1[0]);

		if (r1[2] == '0')
			result = result + ";No fog";
		if (r1[2] == '1')
			result = result + ";Fog 1";
		if (r1[2] == '2')
			result = result + ";Fog 2";
		if (r1[2] == '4')
			result = result + ";Fog 3";
		if (r1[2] == '8')
			result = result + ";Fog 4";
		if (r1[2] == '3')
			result = result + ";Fog 1+2";
		if (r1[2] == '5')
			result = result + ";Fog 1+3";
		if (r1[2] == '7')
			result = result + ";Fog 1+2+3";
		if (r1[2] == '9')
			result = result + ";Fog 1+4";
		if (r1[2] == ':')
			result = result + ";Fog 2+4";
		if (r1[2] == ';')
			result = result + ";Fog 1+2+4";
		if (r1[2] == '<')
			result = result + ";Fog 3+4";
		if (r1[2] == '=')
			result = result + ";Fog 1+3+4";
		if (r1[2] == '>')
			result = result + ";Fog 2+3+4";
		if (r1[2] == '?')
			result = result + ";Fog 1+2+3+4";
		b3 = r1[3];

		result = result + ";" + processZicht(b1, b2, b3);
		return result;
	}

	String processZicht(byte b1, byte b2, byte b3) {
		if (b1 == 'D' & b2 >= '0' & b2 <= '9' & b3 >= '0' & b3 <= '9')
			return "0." + (char) b2 + (char) b3 + "";
		if (b1 >= '0' & b1 <= '9' & b2 == 'D' & b3 >= '0' & b3 <= '9')
			return "" + (char) b1 + "." + (char) b3 + "";
		if (b1 >= '0' & b1 <= '9' & b2 >= '0' & b2 <= '9' & b3 == 'D')
			return "" + (char) b1 + (char) b2 + "";
		if (b1 == 'E' & b2 == 'E')
			return "";
		return "";
	}

	// Process Incoming Reis Geselecteerd (Journey Selected)
	String processReisGeselekteerd(DataInputStream is) throws IOException {

		Binary t = new Binary();
		// TravelSelected;345;50
		String result = "TravelSelected";

		byte[] r = new byte[4];
		// reisID
		r = getWord(is);
		int k = t.fromByteArray1(r);
		result = result + ";" + Integer.toString((int) k);

		// DPId
		r = getWord(is);
		k = t.fromByteArray1(r);
		result = result + ";" + Integer.toString((int) k);
		return result;
	}

	// sendInfoConnection
	public String sendInfoConnection() {
		StatusRequest sr = StatusRequest.getInstance();
		sr.getRequest();

		String status = "";
		if (error) {
			status = "disconnected";
		} else {
			status = "connected";
		}
		String reply = "ReplyConnectionRequest;" + status;
		return reply;
	}
}
