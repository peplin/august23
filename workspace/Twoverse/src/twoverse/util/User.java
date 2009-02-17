package twoverse.util;

import java.io.Serializable;

import jbcrypt.BCrypt;

public class User implements Serializable {
    private String mUsername;
    private String mHashedPassword;
    private int mId;
    private String mEmail;
    private String mPhone;
    private int mPoints;
    
    public class UnsetPasswordException extends Exception {
        UnsetPasswordException(String e) {
            super(e);
        }
    }

    public User(int id, String username, String email, String phone, int points) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPhone(phone);
        setPoints(points);
    }

    public User(String username, String email, String phone, int points) {
        setUsername(username);
        setEmail(email);
        setPhone(phone);
        setPoints(points);
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setPlaintextPassword(String plaintextPassword) {
        mHashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public void setHashedPassword(String hashedPassword) {
        mHashedPassword = hashedPassword;
    }

    public boolean validatePassword(String plaintextCandidate)
            throws UnsetPasswordException {
        if (getHashedPassword() == null) {
            throw new UnsetPasswordException("Password is not set");
        }

        if (BCrypt.checkpw(plaintextCandidate, getHashedPassword()))
            return true;
        return false;
    }

    public boolean validateHashedPassword(String hashedCandidate)
            throws UnsetPasswordException {
        if (getHashedPassword() == null) {
            throw new UnsetPasswordException("Password is not set");
        }

        if (hashedCandidate.equals(getHashedPassword()))
            return true;
        return false;
    }
    
    public boolean validate(User user) throws UnsetPasswordException {
        return validateHashedPassword(user.getHashedPassword());
    }

    public String getHashedPassword() {
        return mHashedPassword;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public void spendPoints(int withdrawl) {
        mPoints -= withdrawl;
    }

    public void earnPoints(int deposit) {
        mPoints += deposit;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

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
