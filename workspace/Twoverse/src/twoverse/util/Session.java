package twoverse.util;

import java.sql.Time;


class TimedOutException extends Exception {
	TimedOutException(String message) {}
}

class BadUsernameException extends Exception {
	BadUsernameException(String message) {}
}

class BadPasswordException extends Exception {	
	BadPasswordException(String message) {}
}

public class Session {
	public Session(String username, Time loginTime) {
		
	}
	public void finalize() {
		
	}
	public void refresh() throws TimedOutException {
		
	}
	
	private User mUser;
	private int mSessionId;
	private Time mLastRefresh;
}