package com.biggestworks.erasales;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Response;

import static android.widget.Toast.LENGTH_SHORT;
import static com.biggestworks.erasales.SessionManagement.KEY_NAME;
import static com.biggestworks.erasales.SessionManagement.KEY_SALESID;
import static java.time.OffsetTime.parse;

public class ListViewVisitUser extends AppCompatActivity {
    // Deklarasi
    ImageView ivojekpict;
    TextView txttglawalsalesvisit, txttglakhirsalesvisit, txtsalesnamevisit, txtsalesidvisit ;
    private Button btDatePicker, btDatePicker2, btnreview, btnseeall;
    public String keyuserID, keyusername;
    public Integer sum_visit;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private RecyclerView recyclerView;
    private ImageView imageutama;
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManagement session;
    ArrayList<HashMap<String, String>> list_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sales_visit);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        keyuserID = user.get(KEY_SALESID);
        keyusername = user.get(KEY_NAME);

        Date mtgl = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd");
        String tgl = ftgl.format(mtgl);

        // Inisialisasi Widget
        //Intent intent = getIntent();
        String getsalesid = keyuserID;
        String getsalesname = keyusername;
        //String getsalesname = intent.getStringExtra("intentsalesname");
        //String gettglawal = intent.getStringExtra("intenttglawal");
        //String gettglakhir = intent.getStringExtra("intenttglakhir");

        txttglawalsalesvisit =  findViewById(R.id.TxtTglAwalSalesVisit);
        txttglakhirsalesvisit = findViewById(R.id.TxtTglAkhirSalesVisit);
        txtsalesnamevisit = findViewById(R.id.TxtSalesNameVisit);
        txtsalesidvisit = findViewById(R.id.TxtSalesIDVisit);

        txttglawalsalesvisit.setText(tgl);
        txttglakhirsalesvisit.setText(tgl);
        txtsalesidvisit.setText(getsalesid);
        txtsalesnamevisit.setText(getsalesname);
        //txttglawalsalesvisit.setText(gettglawal);
        //txttglakhirsalesvisit.setText(gettglakhir);
        //txtsalesnamevisit.setText(getsalesname);
        //txtsalesidvisit.setText(getsalesid);

        recyclerView = (RecyclerView) findViewById(R.id.RV_ListVisitSales);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        btDatePicker = findViewById(R.id.bt_datepicker);
        btDatePicker2 = findViewById(R.id.bt_datepicker2);
        btnreview = findViewById(R.id.BtnReView);

        btnseeall = findViewById(R.id.Btn_SeeAll);

        btnseeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showDateDialog();
                //loadlistvisitall ();

                Intent i = new Intent(ListViewVisitUser.this, ViewAllVisitByDate.class);
                i.putExtra("salesid", txtsalesidvisit.getText().toString());
                i.putExtra("tglawal", txttglawalsalesvisit.getText().toString());
                i.putExtra("tglakhir", txttglakhirsalesvisit.getText().toString());

                ListViewVisitUser.this.startActivity(i);


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

        //ShowDetailOjek();
        //listvisitsales();
        loadlistvisitsales();

        btnreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadlistvisitsales ();

            }
        });
    }



    private void loadlistvisitsales (){
        //Intent intent = getIntent();
        //final String carinamafoto = intent.getStringExtra("datacariojek");

        String url = "http://www.biggestworks.com/Android/era/getlistvisitsales.php";
        recyclerView = (RecyclerView) findViewById(R.id.RV_ListVisitSales);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //rvfotogal = (RecyclerView) findViewById(R.id.RV_ListFotoSales);
        //RecyclerView.LayoutManager llm = new GridLayoutManager(getApplicationContext(), 2);
        //rvfotogal.setLayoutManager(llm);

        requestQueue = Volley.newRequestQueue(ListViewVisitUser.this);
        list_data = new ArrayList<HashMap<String, String>>();

        stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                if (response.isEmpty()) {
                    Toast.makeText(ListViewVisitUser.this, "Data kunjungan anda tidak ada!", LENGTH_SHORT).show();
                    //AdapterListViewVisitUser adapter = new AdapterListViewVisitUser(ListViewVisitUser.this, list_data);
                    //adapter.setClickListener(this);
                    //recyclerView.setAdapter(null);
                    //recyclerView.cle
                    //adapter.notifyDataSetChanged();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("listvisitsales");
                        sum_visit = jsonArray.length();
                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject json = jsonArray.getJSONObject(a);
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("visitid", json.getString("visit_id"));
                            map.put("visitsalesid", json.getString("visit_sales_id"));
                            map.put("visitsalesdate", json.getString("visit_date"));
                            map.put("visitnotes", json.getString("visit_notes"));
                            map.put("visitvisitno", json.getString("visit_visitno"));
                            map.put("visitphotourl", json.getString("visit_photo_url"));
                            map.put("visitsalesname", txtsalesnamevisit.getText().toString());
                            list_data.add(map);
                            AdapterListViewVisitUser adapter = new AdapterListViewVisitUser(ListViewVisitUser.this, list_data);
                            //adapter.setClickListener(this);
                            recyclerView.setAdapter(adapter);
                        }
                        //tambahkan hitungan cek in kiriman aplikasi
                        btnseeall.setText("Lihat semua (" + sum_visit + ")");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ListViewVisitUser.this, error.getMessage(), LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("salesid", txtsalesidvisit.getText().toString());
                params.put("tglawal", txttglawalsalesvisit.getText().toString());
                params.put("tglakhir", txttglakhirsalesvisit.getText().toString());
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
                txttglawalsalesvisit.setText(dateFormatter.format(newDate.getTime()));
                //statustglawal = tvDateResult.getText().toString();
                //loadlistvisitsales();
                //Toast.makeText(MainListSales.this, "loading data tgl1 sales", Toast.LENGTH_LONG).show();

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
                txttglakhirsalesvisit.setText(dateFormatter.format(newDate.getTime()));
                //statustglakhir = tvDateResult2.getText().toString();
                //loadlistvisitsales();
                //Toast.makeText(MainListSales.this, "loading data tgl1 sales", Toast.LENGTH_LONG).show();

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

}

