/**
 * @author Theo den Exter, ARS
 * Date: May 21th 2016
 * Version 1.0
 *
 * Queue between JMS and sending to radar. Should only contain 1 item.
 * Singleton
 *
 * History
 *
 */

package eu.srk.org;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Queue {

	private static Queue instance = null;

	// List of vessels
	static List<Object> list = new ArrayList<Object>();

	protected Queue() {
	}

	public static Queue getInstance() {
		if (instance == null) {
			instance = new Queue();
		}
		return instance;
	}

	public synchronized boolean queueEmpty() {
		return (list.size() == 0);
	}

	// public synchronized void PutQueue(PositionReport p) {
	public synchronized void putQueue(Object p) {
		list.add(p);
		notify();
	}

	// public synchronized PositionReport GetQueue() {
	public synchronized Object getQueue() {
		if (list.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				;
			}
		}

		// PositionReport p = (PositionReport) list.get(0);
		Object p = (Object) list.get(0);

		if (p.getClass().getName() == "java.lang.String") {
			if (list.size() > 0) {
				Iterator<Object> i = list.iterator();
				Object o = (Object) i.next();
				i.remove();
			}
		}

		return p;
	}

}