package twoverse.util;

import jbcrypt.BCrypt;

public class User {
    public User(int id, String username, String hashedPassword, String email,
            String phone, int points) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPhone(phone);
        setPoints(points);
        setHashedPassword(hashedPassword);
    }

    public void setUsername(String username) {
        mUsername = username;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setPlaintestPassword(String plaintextPassword) {
        mHashedPassword = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public void setHashedPassword(String hashedPassword) {
        mHashedPassword = hashedPassword;
    }

    public boolean validatePassword(String plaintextPassword) {
        String candidate = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());

        if (BCrypt.checkpw(candidate, mHashedPassword))
            return true;
        return false;
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

    private String mUsername;
    private String mHashedPassword;
    private int mId;
    private String mEmail;
    private String mPhone;
    private int mPoints;
}
