package com.biggestworks.erasales;


import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import static com.biggestworks.erasales.SessionManagement.KEY_STATUSID;

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    //private static final String PREF_NAME = "AndroidHivePref";
    private static final String PREF_NAME = "BiggestWorks";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_ADMIN = "IsAdminIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // User ID (make variable public to access from outside)
    public static final String KEY_SALESID = "salesid";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    // Statusid (make variable public to access from outside)
    public static final String KEY_STATUSID = "statusid";

    // Statusid (make variable public to access from outside)
    public static final String KEY_VISITNOSESSION = "visitnosession";
    public static final String KEY_LATITUDESESSION = "latitudesession";
    public static final String KEY_LONGITUDESESSION = "longitudesession";

    // Constructor
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session Admin
     * */
    public void createLoginSessionAdm(String name, String salesid, String status){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing admin value as TRUE
        editor.putBoolean(IS_ADMIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing userid in pref
        editor.putString(KEY_SALESID, salesid);

        // Storing email in pref
        //editor.putString(KEY_EMAIL, email);

        // Storing statusid in pref
        editor.putString(KEY_STATUSID, status);

        // commit changes
        editor.commit();
    }

    //public void createLoginSession(String name, String userid, String email, String status){
    public void createLoginSession(String name, String salesid, String status){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing admin value as TRUE
        editor.putBoolean(IS_ADMIN, false);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing name in pref
        editor.putString(KEY_SALESID, salesid);

        // Storing email in pref
        //editor.putString(KEY_EMAIL, email);

        // Storing statusid in pref
        editor.putString(KEY_STATUSID, status);

        // commit changes
        editor.commit();
    }


    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user id
        user.put(KEY_SALESID, pref.getString(KEY_SALESID, null));

        // user email id
        //user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user status id
        user.put(KEY_STATUSID, pref.getString(KEY_STATUSID, null));

        // sales last visit no
        user.put(KEY_VISITNOSESSION, pref.getString(KEY_VISITNOSESSION, null));

        // sales last visit latitude
        user.put(KEY_LATITUDESESSION, pref.getString(KEY_LATITUDESESSION, null));

        // sales last visit longitude
        user.put(KEY_LONGITUDESESSION, pref.getString(KEY_LONGITUDESESSION, null));

        // return user
        return user;
    }


    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in, redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isAdminIn() {
        return pref.getBoolean(IS_ADMIN, false);
    }



    /**
     * Create session visit
     * */
    public void createSessionVisit(String visitnosesi, String latsesi, String longsesi){

        // Storing visit no in pref
        editor.putString(KEY_VISITNOSESSION, visitnosesi);

        // Storing visit no in pref
        editor.putString(KEY_LATITUDESESSION, latsesi);

        // Storing visit no in pref
        editor.putString(KEY_LONGITUDESESSION, longsesi);

        // commit changes
        editor.commit();
    }

}