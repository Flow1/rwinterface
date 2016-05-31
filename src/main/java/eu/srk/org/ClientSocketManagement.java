/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Management class for client socket implementation.
 * Singleton class
 *
 * History
 *
 */

package eu.srk.org;

import java.io.*;
import java.lang.Thread;
import java.net.Socket;
import java.net.*;

public class ClientSocketManagement {

	private static ClientSocketManagement instance = null;

	protected ClientSocketManagement() {
	}

	public static ClientSocketManagement getInstance() {
		if (instance == null) {
			instance = new ClientSocketManagement();
		}
		return instance;
	}

	DataOutputStream os;
	DataInputStream is;

	Socket readSocket;
	Socket writeSocket;
	String server;
	String portListen;
	String portWrite;
	LoggerObject logs;

	boolean error = false;
	boolean supress = false;
	boolean busy = false;

	// List port is used in case of 1 connection for both communication
	// directions
	public void adminSocket(String server1, String portListen1,
			String portWrite1) {
		this.server = server1;
		this.portListen = portListen1;
		this.portWrite = portWrite1;
		os = null;
		is = null;
		readSocket = null;
		writeSocket = null;
		supress = false;
		logs = LoggerObject.getInstance();
	}

	public void disconnect() {
		logs.logInfo("Disconnect requested");
		try {
			os.close();
			os = null;
		} catch (Exception e) {
			;
		}
		try {
			is.close();
			is = null;
		} catch (Exception e) {
			;
		}
		try {
			readSocket.close();
			readSocket = null;
		} catch (Exception e) {
			;
		}
		try {
			writeSocket.close();
			writeSocket = null;
		} catch (Exception e) {
			;
		}
	}

	public void getConnectionSingle() {
		if (busy) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
				;
			}
		} else {

			busy = true;
			if (!supress) {
				logs.logInfo("Single socket connection to radar required");

			}
			try {
				os.close();
				os = null;
			} catch (Exception e) {
				;
			}
			try {
				is.close();
				is = null;
			} catch (Exception e) {
				;
			}
			try {
				readSocket.close();
				readSocket = null;
			} catch (Exception e) {
				;
			}

			// Server must exist
			try {
				if (!supress) {
					logs.logInfo("Connect to: " + server + ":" + portListen);
					supress = true;

				}
				readSocket = new Socket(server, Integer.parseInt(portListen));
			} catch (UnknownHostException e) {
				logs.logError("Don't know host: " + server);
				System.exit(0);
			} catch (IOException e) {
				;
			}

			try {
				if (readSocket != null) {
					logs.logInfo("Connected to radar !");
					is = new DataInputStream(readSocket.getInputStream());
					os = new DataOutputStream(readSocket.getOutputStream());
					supress=false;
					error = false;
				} else {
					error = true;
					// Just delay and retry
					is = null;
					os = null;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException ie) {
						;
					}
				}

			} catch (IOException e) {
				error = true;
			}
			busy = false;
		}
	}

	public void getConnectionDouble() {
		if (busy) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
				;
			}
		} else {

			busy = true;
			if (!supress) {
				logs.logInfo("Multiple socket connection to radar required");
			}
			try {
				os.close();
				os = null;
			} catch (Exception e) {
				;
			}
			try {
				is.close();
				is = null;
			} catch (Exception e) {
				;
			}
			try {
				readSocket.close();
				readSocket = null;
			} catch (Exception e) {
				;
			}
			try {
				writeSocket.close();
				writeSocket = null;
			} catch (Exception e) {
				;
			}
			// Server must exist
			try {
				if (!supress) {
					logs.logInfo("Connect to read port: " + server + ":"
							+ portListen);
				}
				readSocket = new Socket(server, Integer.parseInt(portListen));
			} catch (UnknownHostException e) {
				logs.logError("Don't know host: " + server);
				System.exit(0);
			} catch (IOException e) {
				;
			}

			try {
				if (!supress) {
					logs.logInfo("Connect to write port: " + server + ":"
							+ portListen);
					supress = true;
				}
				writeSocket = new Socket(server, Integer.parseInt(portWrite));
			} catch (UnknownHostException e) {
				logs.logError("Don't know host: " + server);
				System.exit(0);
			} catch (IOException e) {
				;
			}

			try {
				if ((readSocket != null) && (writeSocket != null)) {
					logs.logInfo("Connected to radar !");
					is = new DataInputStream(readSocket.getInputStream());
					os = new DataOutputStream(writeSocket.getOutputStream());
					error = false;
				} else {
					error = true;
					// Just delay and retry
					is = null;
					os = null;
					try {
						Thread.sleep(2000);
					} catch (InterruptedException ie) {
						;
					}
				}

			} catch (IOException e) {
				error = true;
			}
			if (!error) {
				supress = false;
			}
			busy = false;
		}
	}

	public DataOutputStream getDataOutputStream() {
		return os;
	}

	public DataInputStream getDataInputStream() {
		return is;
	}
}
