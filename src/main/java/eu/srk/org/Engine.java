/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Start communication engine
 *
 * Note: contains a teststub for providing messages from IVS to RW
 *       This will also be the future interface between JMS and Engine
 *
 * History
 *
 */

package eu.srk.org;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class Engine extends Thread {

	String server; 
	String portListen;
	String portWrite;
	DataInputStream is;
	DataOutputStream os;

	ServerSocketManagement serverSocket;

	Engine(String server, String portListen, String portWrite) {
		this.server = server;
		this.portListen = portListen;
		this.portWrite = portWrite;
	}

	public void run() {

		serverSocket = ServerSocketManagement.getInstance();
		serverSocket.createServerSocket(server, portListen);
		
		is = null;
		os = null;


			// Submit test data
			SubmitTestRequests p3 = new SubmitTestRequests();
			p3.start();
			
			while (true) {
				try {
					serverSocket.waitForConnection();
					is = serverSocket.getDataInputStream();
					os = serverSocket.getDataOutputStream();

					SendThread p1 = new SendThread();
					p1.start();

					ReceiveThread p2 = new ReceiveThread();
					p2.start();

					// Wait for ending threads
					p1.join();
					p2.join();

				} catch (InterruptedException e) {
					//e.printStackTrace();
				}
			}


}
}