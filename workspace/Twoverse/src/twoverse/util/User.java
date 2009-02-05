package twoverse.util;

import jbcrypt.BCrypt;

public class User {
    public User(int id, String username, String hashedPassword, String email,
            String phone, int points) {

    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setPassword(String plaintextPassword) {
        mHashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public boolean validatePassword(String plaintextPassword) {
        String candidate = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());

        if (BCrypt.checkpw(candidate, mHashedPassword))
            return true;
        return false;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public void spendPoints(int withdrawl) {
        mPoints -= withdrawl;
    }

    public void addPoints(int deposit) {
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

    private String mUsername;
    private String mHashedPassword;
    private int mId;
    private String mEmail;
    private String mPhone;
    private int mPoints;
}
