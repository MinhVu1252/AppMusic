package com.example.demoprojectmusic.Model;

public class UserDataManager {
    private static UserDataManager instance;
    private int userID;

    private UserDataManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
