package eu.srk.org;


import java.io.*;
import java.net.Socket;
import java.net.*;

public class ServerSocketManagement {
	LoggerObject logs;

	private static ServerSocketManagement instance = null;

	protected ServerSocketManagement() {
	}

	public static ServerSocketManagement getInstance() {
		if (instance == null) {
			instance = new ServerSocketManagement();
		}
		return instance;
	}

	DataOutputStream os;
	DataInputStream is;

	ServerSocket serverSocket;
	Socket readSocket;
	Socket writeSocket;

	String server;
	String port;

	boolean error = false;
	boolean busy = false;
	boolean supress = false;

	public void createServerSocket(String server1, String port1) {
		this.server = server1;
		this.port = port1;
		logs = LoggerObject.getInstance();

		logs.logInfo("Create server socket: " + server1 + ":" + port1);

		// Check if server exist
		try {
			serverSocket = new ServerSocket(Integer.parseInt(port));
		} catch (IOException e) {
			logs.logError("RWSim: Server port not valid: " + e);
		}
	}

	public void disconnect() {
		logs.logInfo("Disconnection requested");
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

	public void waitForConnection() {
		if (!supress) {
			logs.logInfo("Single socket connection to client required");
			supress = true;
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

		logs.logInfo("Accept");
		try {
			readSocket = serverSocket.accept();
		} catch (UnknownHostException e) {
			logs.logError("RWSim: Don't know host: " + server);
			System.exit(0);
		} catch (IOException e) {
			logs.logError("RWSim: connect error " + e);
		}

		try {
			if (readSocket != null) {
				logs.logInfo("Connected");
				supress = false;
				is = new DataInputStream(readSocket.getInputStream());
				os = new DataOutputStream(readSocket.getOutputStream());
				error = false;
			} else {
				error = true;
			}
		} catch (IOException e) {
			error = true;
		}
	}

	public DataOutputStream getDataOutputStream() {
		return os;
	}

	public DataInputStream getDataInputStream() {
		return is;
	}
}
