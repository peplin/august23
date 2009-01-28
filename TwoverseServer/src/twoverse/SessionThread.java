package twoverse;

import java.util.HashMap;



public class SessionThread extends Thread {
	public boolean login(String username, String password) throws Exception {
		return false;
	}
	
	public boolean logout(String username, int session) throws Exception {
		return false;
	}
	public void refresh(String username, int session) throws Exception {
		
	}
	public void cleanup() {
		
	}
	
	private HashMap<Integer, Session> sessions;
}
