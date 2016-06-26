package eu.srk.org;

import eu.srk.org.StatusRequest;

public class StatusRequest {


	private static StatusRequest instance = null;
	boolean button;

	protected StatusRequest() {
	}

	public static StatusRequest getInstance() {
		if (instance == null) {
			instance = new StatusRequest();
		}
		return instance;
	}

	public boolean getRequest() {
		return button;
	}

	public void setRequest(boolean b) {
		button = b;
	}

}