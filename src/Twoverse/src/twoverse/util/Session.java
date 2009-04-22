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

public class Session implements Serializable {
    private static final long serialVersionUID = 3290251644990110932L;
    private User mUser;
    private int mId;
    private Timestamp mLastRefresh;
    private static int sNextId = 0;

    /**
     * @param user
     */
    public Session(User user) {
        refresh();
        setUser(user);
        setId(sNextId++);
    }

    /**
     * @param user
     */
    public void setUser(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

    /**
     * @param sessionId
     */
    public void setId(int sessionId) {
        mId = sessionId;
    }

    /**
     * @return
     */
    public int getId() {
        return mId;
    }

    /**
     * 
     */
    public void refresh() {
        java.util.Date now = new java.util.Date();
        mLastRefresh = new Timestamp(now.getTime());
    }

    /**
     * @return
     */
    public Timestamp getLastRefresh() {
        return mLastRefresh;
    }

    /**
     * @author peplin
     *
     */
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

    /**
     * @author peplin
     *
     */
    @SuppressWarnings("serial")
    public class BadPasswordException extends Exception {
        BadPasswordException(String message) {
        }
    }

    /**
     * @param other
     * @return
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
