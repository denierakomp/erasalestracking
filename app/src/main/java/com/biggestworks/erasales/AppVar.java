package com.biggestworks.erasales;




public class AppVar {
    //URL to our login.php file, url bisa diganti sesuai dengan alamat server kita
    public static final String LOGIN_URL = "http://www.biggestworks.com/Android/era/loginadm.php";
    //public static final String LOGIN_URL = "http://www.biggestworks.com/Android/AvelShop/loginadm.php";

    //Keys for email and password as defined in our $_POST['key'] in login.php
    public static final String KEY_SALESNAME = "salesname";
    public static final String KEY_PHONENO = "salesphone";
    public static final String KEY_PASSWORD = "password";
    //public static final String KEY_STATUSID = "status";


    //If server response is equal to this that means login is successful
    public static final String LOGIN_SUCCESS1 = "success1"; //admin
    public static final String LOGIN_SUCCESS0 = "success0"; //user biasa
    //public static final String LOGIN_SUCCESS = "success user_status:2";
}
