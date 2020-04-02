package com.biggestworks.erasales;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterAPI {
    @FormUrlEncoded
    @POST("/Android/era/tambahdatavisit.php")
    Call<ValueUser> tambahdatavisit(@Field("userid") String userid,
                                    @Field("review") String review,
                                    @Field("tgl") String tgl,
                                    @Field("visitno") String visitno);

    @FormUrlEncoded
    @POST("/Android/era/tambahGPSsalesvisit.php")
    Call<ValueUser> tambahGPSsalesvisit(@Field("visitno") String m_novisit,
                                  @Field("tgl") String tgl,
                                  @Field("datalat") Double datalatuser,
                                  @Field("datalong") Double datalonguser);

    @FormUrlEncoded
    @POST("/Android/era/daftartambahuser.php")
    Call<ValueUser> daftaruser(@Field("nama2") String nama2,
                               @Field("namauser2") String namauser2,
                               @Field("pass2") String pass2,
                               @Field("tgl") String tgl,
                               @Field("status") String status);

    @FormUrlEncoded
    @POST("/Android/era/updateuser.php")
    Call<ValueUser> updateuser(@Field("userid") String useridupdate,
                               @Field("status") String statusupdate);


    @FormUrlEncoded
    @POST("/Android/era/updategroup.php")
    Call<ValueUser> updategroup(@Field("userid") String useridupdate,
                               @Field("status") String statusgroup);


    @FormUrlEncoded
    @POST("/Android/era/updatedatauser.php")
    Call<ValueUser> updatedatauser(@Field("userid") String userid,
                                   @Field("nama2") String nama2,
                                   @Field("namauser2") String namauser2,
                                   @Field("pass2") String pass2
                                   );

    @FormUrlEncoded
    @POST("/Android/era/deleteitemvisit.php")
    Call<ValueUser> deleteitemvisit(@Field("idvisit") String idvisit,
                                    @Field("Svisitphotourl") String Svisitphotourl
                                    );



}
