/**
 * Twoverse User
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

import jbcrypt.BCrypt;

public class User implements Serializable {
    private static final long serialVersionUID = 1849465514178345554L;
    private String mUsername;
    private String mHashedPassword;
    private int mId;
    private String mEmail;
    private String mPhone;
    private int mPoints;

    public static class UnsetPasswordException extends Exception {
        private static final long serialVersionUID = -6748088724958043808L;

        public UnsetPasswordException(String e) {
            super(e);
        }
    }

    /**
     * @param id
     * @param username
     * @param email
     * @param phone
     * @param points
     */
    public User(int id, String username, String email, String phone, int points) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPhone(phone);
        setPoints(points);
    }

    /**
     * @param username
     * @param email
     * @param phone
     * @param points
     */
    public User(String username, String email, String phone, int points) {
        setUsername(username);
        setEmail(email);
        setPhone(phone);
        setPoints(points);
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        mUsername = username;
    }

    /**
     * @return
     */
    public String getUsername() {
        return mUsername;
    }

    /**
     * @param plaintextPassword
     */
    public void setPlaintextPassword(String plaintextPassword) {
        mHashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    /**
     * @param hashedPassword
     */
    public void setHashedPassword(String hashedPassword) {
        mHashedPassword = hashedPassword;
    }

    /**
     * @param plaintextCandidate
     * @return
     * @throws UnsetPasswordException
     */
    public boolean validatePassword(String plaintextCandidate)
            throws UnsetPasswordException {
        if(getHashedPassword() == null) {
            throw new UnsetPasswordException("Password is not set");
        }

        if(BCrypt.checkpw(plaintextCandidate, getHashedPassword())) {
            return true;
        }
        return false;
    }

    /**
     * @param hashedCandidate
     * @return
     * @throws UnsetPasswordException
     */
    public boolean validateHashedPassword(String hashedCandidate)
            throws UnsetPasswordException {
        if(getHashedPassword() == null) {
            throw new UnsetPasswordException("Password is not set");
        }

        if(hashedCandidate.equals(getHashedPassword())) {
            return true;
        }
        return false;
    }

    /**
     * @param user
     * @return
     * @throws UnsetPasswordException
     */
    public boolean validate(User user) throws UnsetPasswordException {
        return validateHashedPassword(user.getHashedPassword());
    }

    /**
     * @return
     */
    public String getHashedPassword() {
        return mHashedPassword;
    }

    /**
     * @param points
     */
    public void setPoints(int points) {
        mPoints = points;
    }

    /**
     * @param withdrawl
     */
    public void spendPoints(int withdrawl) {
        mPoints -= withdrawl;
    }

    /**
     * @param deposit
     */
    public void earnPoints(int deposit) {
        mPoints += deposit;
    }

    /**
     * @return
     */
    public int getPoints() {
        return mPoints;
    }

    /**
     * @param phone
     */
    public void setPhone(String phone) {
        mPhone = phone;
    }

    /**
     * @return
     */
    public String getPhone() {
        return mPhone;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        mEmail = email;
    }

    /**
     * @return
     */
    public String getEmail() {
        return mEmail;
    }

    /**
     * @param id
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * @return
     */
    public int getId() {
        return mId;
    }

    /**
     * @param other
     * @return
     */
    public boolean equals(User other) {
        return mUsername.equals(other.mUsername)
                && mHashedPassword.equals(other.mHashedPassword)
                && mId == other.mId && mEmail.equals(other.mEmail)
                && mPhone.equals(other.mPhone) && mPoints == other.mPoints;
    }

    @Override
    public String toString() {
        String result =
                "[" + getId() + ", " + getUsername() + ", "
                        + getHashedPassword() + ", " + getEmail() + ", "
                        + getPhone() + ", " + getPoints() + "]";
        return result;
    }
}
