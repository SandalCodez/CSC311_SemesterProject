//package service;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.prefs.Preferences;
//
//public class UserSession {
//
//    private static UserSession instance;
//
//    private String userName;
//
//    private String password;
//    private String privileges;
//
//    private UserSession(String userName, String password, String privileges) {
//        this.userName = userName;
//        this.password = password;
//        this.privileges = privileges;
//        Preferences userPreferences = Preferences.userRoot();
//        userPreferences.put("USERNAME",userName);
//        userPreferences.put("PASSWORD",password);
//        userPreferences.put("PRIVILEGES",privileges);
//    }
//
//
//
//    public static UserSession getInstace(String userName,String password, String privileges) {
//        if(instance == null) {
//            instance = new UserSession(userName, password, privileges);
//        }
//        return instance;
//    }
//
//    public static UserSession getInstace(String userName,String password) {
//        if(instance == null) {
//            instance = new UserSession(userName, password, "NONE");
//        }
//        return instance;
//    }
//    public String getUserName() {
//        return this.userName;
//    }
//
//    public String getPassword() {
//        return this.password;
//    }
//
//    public String getPrivileges() {
//        return this.privileges;
//    }
//
//    public void cleanUserSession() {
//        this.userName = "";// or null
//        this.password = "";
//        this.privileges = "";// or null
//    }
//
//    @Override
//    public String toString() {
//        return "UserSession{" +
//                "userName='" + this.userName + '\'' +
//                ", privileges=" + this.privileges +
//                '}';
//    }
//}
package service;

import java.util.prefs.Preferences;

/**
 * Thread-safe implementation of the UserSession class using double-checked locking.
 * This class manages user authentication state and preferences storage.
 */
public class UserSession {

    // Volatile ensures visibility across threads
    private static volatile UserSession instance;

    // User information
    private String userName;
    private String password;
    private String privileges;

    // Keys for preferences storage
    private static final String PREF_USERNAME = "USERNAME";
    private static final String PREF_PASSWORD = "PASSWORD";  // In a production app, never store raw passwords
    private static final String PREF_PRIVILEGES = "PRIVILEGES";

    /**
     * Private constructor to prevent direct instantiation
     */
    private UserSession(String userName, String password, String privileges) {
        this.userName = userName;
        this.password = password;
        this.privileges = privileges;

        // Store in preferences
        storeUserPreferences();
    }

    /**
     * Thread-safe singleton implementation with double-checked locking
     */
    public static UserSession getInstance(String userName, String password, String privileges) {
        // First check (not synchronized)
        if (instance == null) {
            // Synchronize on the class object
            synchronized (UserSession.class) {
                // Second check (synchronized)
                if (instance == null) {
                    instance = new UserSession(userName, password, privileges);
                }
            }
        }
        return instance;
    }

    /**
     * Overloaded getInstance method with default privileges
     */
    public static UserSession getInstance(String userName, String password) {
        return getInstance(userName, password, "NONE");
    }

    /**
     * Get existing instance without creating a new one
     * @return Current UserSession instance or null if not initialized
     */
    public static UserSession getCurrentInstance() {
        return instance;
    }

    /**
     * Stores user information in system preferences
     */
    private void storeUserPreferences() {
        Preferences userPreferences = Preferences.userRoot().node("app/usersession");
        userPreferences.put(PREF_USERNAME, userName);
        userPreferences.put(PREF_PASSWORD, password);  // Should use secure storage in production
        userPreferences.put(PREF_PRIVILEGES, privileges);
    }

    /**
     * Loads a user session from stored preferences
     * @return UserSession object or null if no stored session found
     */
    public static UserSession loadFromPreferences() {
        Preferences userPreferences = Preferences.userRoot().node("app/usersession");
        String storedUsername = userPreferences.get(PREF_USERNAME, null);
        String storedPassword = userPreferences.get(PREF_PASSWORD, null);
        String storedPrivileges = userPreferences.get(PREF_PRIVILEGES, "NONE");

        if (storedUsername != null && storedPassword != null) {
            return getInstance(storedUsername, storedPassword, storedPrivileges);
        }
        return null;
    }

    /**
     * Clears the current user session
     */
    public void cleanUserSession() {
        synchronized (UserSession.class) {
            this.userName = "";
            this.password = "";
            this.privileges = "";

            // Clear preferences
            Preferences userPreferences = Preferences.userRoot().node("app/usersession");
            userPreferences.remove(PREF_USERNAME);
            userPreferences.remove(PREF_PASSWORD);
            userPreferences.remove(PREF_PRIVILEGES);

            // Reset the singleton instance
            instance = null;
        }
    }

    // Getters
    public String getUserName() {
        return this.userName;
    }

    public String getPassword() {
        return this.password;
    }

    public String getPrivileges() {
        return this.privileges;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "userName='" + this.userName + '\'' +
                ", privileges=" + this.privileges +
                '}';
    }
}
