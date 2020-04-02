package com.biggestworks.erasales;

public class ResultUser {
    String sales_id;
    String sales_name;
    String sales_phone;
    String sales_login_name;
    String sales_pass;

    String sales_last_login_date;
    String sales_date_created;
    String sales_url_avatar;
    
    String fotolike_galeriid;
    String fotolike_salesid;

    public String getSalesId() {   return sales_id;    }
    public String getSalesName() {  return sales_name;    }
    public String getSalesPass() {  return sales_pass;}
    public String getSalesLogin() {  return sales_login_name;}

    public String getPhoneSales() {return sales_phone;}

    public String getSalesDateLastLogin() {   return sales_last_login_date;    }
    public String getSalesDateCreated() {   return sales_date_created;    }
    public String getUrlAvatar() {   return sales_url_avatar;    }

    public String getFotolike_galeriid() {   return fotolike_galeriid;    }
    public String getFotolike_salesid() {   return fotolike_salesid;    }

}

