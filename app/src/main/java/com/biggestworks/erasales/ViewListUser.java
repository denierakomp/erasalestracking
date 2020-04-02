package com.biggestworks.erasales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.Toast.LENGTH_SHORT;
import static com.biggestworks.erasales.SessionManagement.KEY_SALESID;

public class ViewListUser extends AppCompatActivity {


    private SimpleDateFormat dateFormatter;
    private TextView tvDateResult, tvDateResult2;
    private Button btnsaveupdateuser;

    private RecyclerView rvfotogal;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;


    ArrayList<HashMap<String, String>> list_data;
    private static final String LOG_TAG =
            ViewListUser.class.getSimpleName();

    private static final String TAG = ViewListUser.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    ImageView imageView;
    Bitmap bitmap, decoded;
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100
    String tag_json_obj = "json_obj_req";



    private String KEY_IMAGE = "image";

    public String statusid, userid ;
    public String txtcaptionfoto;
    SessionManagement session;
    ArrayList<HashMap<String, String>> list_datauser;

    Unbinder unbinder;

    private static final int REQUEST_CHOOSE_IMAGE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_user);

        Date mtgl = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd");
        String tgl = ftgl.format(mtgl);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        userid = user.get(KEY_SALESID);

        //btnsaveupdateuser = findViewById(R.id.Btn_SaveUpdateUser);
        /*
        btnsaveupdateuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        */


        loadinglistfotouser();
        //Toast.makeText(ViewListUser.this, "loading data sales", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //loadinglistfotouser();
        //updatecountbadgeUSER();
    }

    private void loadinglistfotouser (){
        String url = "http://www.biggestworks.com/Android/era/getlistfotouser.php";
        rvfotogal = (RecyclerView) findViewById(R.id.RV_ListViewUser);
        RecyclerView.LayoutManager llm = new GridLayoutManager(getApplicationContext(), 2);
        rvfotogal.setLayoutManager(llm);

        requestQueue = Volley.newRequestQueue(ViewListUser.this);
        list_data = new ArrayList<HashMap<String, String>>();

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                if (response.isEmpty()) {
                    Toast.makeText(ViewListUser.this, "Data user tidak ditemukan!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("galeri_user");

                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject json = jsonArray.getJSONObject(a);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("userid", json.getString("sales_id"));
                            map.put("username", json.getString("sales_name"));
                            map.put("userphone", json.getString("sales_phone"));
                            map.put("userphoto", json.getString("sales_photo"));
                            map.put("userlastlogin", json.getString("sales_last_login_date"));
                            map.put("userstatus", json.getString("sales_status"));
                            map.put("userleadbyid", json.getString("sales_leadbyID"));
                            list_data.add(map);
                            AdapterViewListUser adapter = new AdapterViewListUser(ViewListUser.this, list_data);
                            //adapter.setClickListener(this);
                            rvfotogal.setAdapter(adapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewListUser.this, error.getMessage(), LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search", "");
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




}
