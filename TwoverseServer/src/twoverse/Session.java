package twoverse;

import java.sql.Time;

public class Session {
	public Session(String username, Time loginTime) {
		
	}
	public void finalize() {
		
	}
	public void refresh() throws TimedOutException {
		
	}
	
	private String username;
	private int session;
	private Time lastRefresh;
}