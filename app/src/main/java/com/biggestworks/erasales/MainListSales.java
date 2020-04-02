package com.biggestworks.erasales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
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

import static android.widget.Toast.LENGTH_SHORT;
import static com.biggestworks.erasales.SessionManagement.KEY_SALESID;
import static com.biggestworks.erasales.SessionManagement.KEY_STATUSID;

public class MainListSales extends AppCompatActivity {
    public String statustglawal, statustglakhir;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private TextView tvDateResult, tvDateResult2;
    private Button btDatePicker, btDatePicker2, btnviewallinmap;

    private RecyclerView rvfotogal;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private Button btnupload;

    ArrayList<HashMap<String, String>> list_data;
    private static final String LOG_TAG =
            MainListSales.class.getSimpleName();

    private static final String TAG = MainListSales.class.getSimpleName();
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
        setContentView(R.layout.activity_main_list_sales);


        Date mtgl = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd");
        String tgl = ftgl.format(mtgl);

        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        userid = user.get(KEY_SALESID);
        statusid = user.get(KEY_STATUSID);
        /**
         * Kita menggunakan format tanggal dd-MM-yyyy
         * jadi nanti tanggal nya akan diformat menjadi
         * misalnya 01-12-2017
         */
        //dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        tvDateResult = (TextView) findViewById(R.id.tv_dateresult);
        tvDateResult.setText(tgl);
        statustglawal = tvDateResult.getText().toString();
        btDatePicker = findViewById(R.id.bt_datepicker);

        tvDateResult2 = (TextView) findViewById(R.id.tv_dateresult2);
        tvDateResult2.setText(tgl);
        statustglakhir = tvDateResult2.getText().toString();
        btDatePicker2 = findViewById(R.id.bt_datepicker2);

        btnviewallinmap = findViewById(R.id.Btn_SeeAllInMap);

        btnviewallinmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDateDialog();
                //loadlistvisitall ();

                Intent i = new Intent(MainListSales.this, ViewAllVisitByDateMap.class);
                //i.putExtra("salesid", txtsalesidvisit.getText().toString());
                i.putExtra("tglawal", tvDateResult.getText().toString());
                i.putExtra("tglakhir", tvDateResult2.getText().toString());

                MainListSales.this.startActivity(i);


            }
        });

        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
                //loadinglistfotosales();
                //Toast.makeText(MainListSales.this, "loading data tgl1 sales", Toast.LENGTH_LONG).show();

            }
        });

        btDatePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog2();
                //loadinglistfotosales();
                //Toast.makeText(MainListSales.this, "loading data tgl2 sales", Toast.LENGTH_LONG).show();

            }
        });

        loadinglistfotosales();
        //Toast.makeText(MainListSales.this, "loading data sales", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onResume() {
        super.onResume();

            //loadinglistfotosales();
            //updatecountbadgeUSER();



    }

    private void loadinglistfotosales (){
        String url = "http://www.biggestworks.com/Android/era/getlistfotosales.php";
        rvfotogal = (RecyclerView) findViewById(R.id.RV_ListFotoSales);
        RecyclerView.LayoutManager llm = new GridLayoutManager(getApplicationContext(), 2);
        rvfotogal.setLayoutManager(llm);

        requestQueue = Volley.newRequestQueue(MainListSales.this);
        list_data = new ArrayList<HashMap<String, String>>();

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                if (response.isEmpty()) {
                    Toast.makeText(MainListSales.this, "Data user tidak ditemukan!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("galeri_sales");

                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject json = jsonArray.getJSONObject(a);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("salesid", json.getString("sales_id"));
                            map.put("salesname", json.getString("sales_name"));
                            map.put("salesphone", json.getString("sales_phone"));
                            map.put("salesphoto", json.getString("sales_photo"));

                            map.put("statustglawal", statustglawal);
                            map.put("statustglakhir", statustglakhir);

                            list_data.add(map);
                            AdapterMainListVisit adapter = new AdapterMainListVisit(MainListSales.this, list_data);
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
                Toast.makeText(MainListSales.this, error.getMessage(), LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search", userid);
                params.put("statusid", statusid);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                //tvDateResult.setText("Tanggal dipilih : "+dateFormatter.format(newDate.getTime()));
                tvDateResult.setText(dateFormatter.format(newDate.getTime()));
                statustglawal = tvDateResult.getText().toString();
                loadinglistfotosales();
                Toast.makeText(MainListSales.this, "loading data tgl awal user", Toast.LENGTH_LONG).show();

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }



    private void showDateDialog2(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                //tvDateResult2.setText("Tanggal dipilih : "+dateFormatter.format(newDate.getTime()));
                tvDateResult2.setText(dateFormatter.format(newDate.getTime()));
                statustglakhir = tvDateResult2.getText().toString();
                loadinglistfotosales();
                Toast.makeText(MainListSales.this, "loading data tgl akhir user", Toast.LENGTH_LONG).show();

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

}
