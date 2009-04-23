/**
 * Twoverse Session
 *
 * by Christopher Peplin (chris.peplin@rhubarbtech.com)
 * for August 23, 1966 (GROCS Project Group)
 * University of Michigan, 2009
 *
 * http://august231966.com
 * http://www.dc.umich.edu/grocs
 *
 * Copyright 2009 Christopher Peplin 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package twoverse.util;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Session for a logged in user.
 * 
 * @author Christopher Peplin (chris.peplin@rhubarbtech.com)
 * @version 1.0, Copyright 2009 under Apache License
 */
public class Session implements Serializable {
    private static final long serialVersionUID = 3290251644990110932L;
    private User mUser;
    private int mId;
    private Timestamp mLastRefresh;
    private static int sNextId = 0;

    /**
     * Constructs a new session for the given user.
     * 
     * @param user
     *            account for this session
     */
    public Session(User user) {
        refresh();
        setUser(user);
        setId(sNextId++);
    }

    /**
     * Sets the user of this session.
     * 
     * @param user
     */
    public void setUser(User user) {
        mUser = user;
    }

    /**
     * Returns the user of this session.
     * 
     * @return user
     */
    public User getUser() {
        return mUser;
    }

    /**
     * Sets the session ID. This should be a valid session from the server.
     * 
     * @param sessionId
     */
    public void setId(int sessionId) {
        mId = sessionId;
    }

    /**
     * Returns the current session ID.
     * 
     * @return id
     */
    public int getId() {
        return mId;
    }

    /**
     * Updates the time of this session to the current time. Used to time out
     * old, inactive sessions.
     */
    public void refresh() {
        java.util.Date now = new java.util.Date();
        mLastRefresh = new Timestamp(now.getTime());
    }

    /**
     * Get the time of the last refresh.
     * 
     * @return time of last refresh
     */
    public Timestamp getLastRefresh() {
        return mLastRefresh;
    }

    @SuppressWarnings("serial")
    public class TimedOutException extends Exception {
        TimedOutException(String message) {
        }
    }

    @SuppressWarnings("serial")
    public class BadUsernameException extends Exception {
        /**
         * @param message
         */
        BadUsernameException(String message) {
        }
    }

    @SuppressWarnings("serial")
    public class BadPasswordException extends Exception {
        BadPasswordException(String message) {
        }
    }

    /**
     * 
     * @param other
     *            session for comparison
     * @return true iff both session have the same ID and user
     */
    public boolean equals(Session other) {
        return mId == other.mId && mUser.equals(other.mUser);
    }

    @Override
    public String toString() {
        return "[id: " + getId() + ", " + "user: " + getUser() + ", "
                + "last refresh: " + getLastRefresh() + "]";
    }
}
