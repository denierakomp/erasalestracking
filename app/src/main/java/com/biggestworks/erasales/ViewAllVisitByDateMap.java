package com.biggestworks.erasales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;

public class ViewAllVisitByDateMap extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    double latitude = 0;
    double longitude = 0;
    double latitude2 = 0;
    double longitude2 = 0;
    LatLng position;

    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    ArrayList<HashMap<String, String>> list_data;
    //private LatLngBounds AUSTRALIA = new LatLngBounds(new LatLng(-44, 113), new LatLng(-10, 154));
    public String getintentsalesid, getintenttglawal, getintenttglakhir;
    private static final String TAG = ViewAllVisitByDateMap.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cek_gpssales);
        //latitude = getIntent().getDoubleExtra("lat", 0);
        //longitude = getIntent().getDoubleExtra("long", 0);

        Intent intent = getIntent();
        //getintentsalesid = intent.getStringExtra("salesid");
        getintenttglawal = intent.getStringExtra("tglawal");
        getintenttglakhir = intent.getStringExtra("tglakhir");
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        fm.getMapAsync(ViewAllVisitByDateMap.this);

    }

    @Override
    public void onConnected(Bundle bundle) {
        /* getDirection();*/
        /*getCurrentLocation();*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        //MarkerOptions options = new MarkerOptions();
        try {
            //getdatasalestrackingbyvisit();
            String urltrack = "http://www.biggestworks.com/Android/era/getalllisttrackingbydatemap.php";
            requestQueue = Volley.newRequestQueue(ViewAllVisitByDateMap.this);
            list_data = new ArrayList<HashMap<String, String>>();

            stringRequest = new StringRequest(Request.Method.POST, urltrack, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response ", response);
                    if (response.isEmpty()) {
                        Toast.makeText(ViewAllVisitByDateMap.this, "Data tracking kunjungan semua user tidak ditemukan!", LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("listalltrackingbydate");

                            for (int a = 0; a < jsonArray.length(); a++) {

                                JSONObject json = jsonArray.getJSONObject(a);
                                //String jamdetik = json.getString("tracking_datetime").substring(10,19);
                                String visitnotes = json.getString("visit_notes");
                                String jamdetik = json.getString("tracking_datetime");
                                String latitudedata = json.getString("tracking_latitude");
                                String longitudedata = json.getString("tracking_longitude");
                                final Double doublelatdata = Double.parseDouble(latitudedata);
                                final Double doublelondata = Double.parseDouble(longitudedata);

                                position = new LatLng(doublelatdata, doublelondata);
                                MarkerOptions options = new MarkerOptions();
                                options.position(position);
                                //options.title(getintentsalesname +"time: " +jamdetik); //kalau mau ditambah nama salesnya
                                options.title(visitnotes + " - time: " +jamdetik);

                                googleMap.addMarker(options);
                                CameraUpdate updatePosition = CameraUpdateFactory.newLatLngZoom(position,12);
                                googleMap.moveCamera(updatePosition);
                                googleMap.animateCamera(updatePosition);


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ViewAllVisitByDateMap.this, error.getMessage(), LENGTH_SHORT).show();
                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //params.put("salesid", getintentsalesid);
                    params.put("tglawal", getintenttglawal);
                    params.put("tglakhir", getintenttglakhir);
                    //Log.d(TAG, "CARI: " + getintentvisitno );
                    return params;
                }
            };
            requestQueue.add(stringRequest);

        } catch (Exception ex) {
            //ex.printStackTrace();
            ex.getMessage();
        }
        //googleMap.addMarker(options);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    private void getdatasalestrackingbyvisit (){
        String urltrack = "http://www.biggestworks.com/Android/era/getdatasalestracking.php";
        requestQueue = Volley.newRequestQueue(ViewAllVisitByDateMap.this);
        list_data = new ArrayList<HashMap<String, String>>();

        stringRequest = new StringRequest(Request.Method.POST, urltrack, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                if (response.isEmpty()) {
                    Toast.makeText(ViewAllVisitByDateMap.this, "Data tracking kunjungan sales tidak ditemukan!", LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("galeri_sales");

                        for (int a = 0; a < jsonArray.length(); a++) {

                            JSONObject json = jsonArray.getJSONObject(a);

                            String latitudedata = json.getString("tracking_latitude");
                            String longitudedata = json.getString("tracking_longitude");
                            final Double doublelatdata = Double.parseDouble(latitudedata);
                            final Double doublelondata = Double.parseDouble(longitudedata);

                            position = new LatLng(doublelatdata, doublelondata);
                            MarkerOptions options = new MarkerOptions();
                            //googleMap.addMarker(options);

                            /*
                            mMap.addMarker(new MarkerOptions()
                                    //.title(json.getString("tracking_datetime"))
                                    //.snippet(Integer.toString(json.getInt("tracking_description")))
                                    .position(new LatLng(
                                            doublelatdata,
                                            doublelondata
                                    ))
                            );
                            */

                            /*
                            HashMap<String, String> map = new HashMap<String, String>();
                            map.put("visitsalesid", json.getString("visit_sales_id"));
                            map.put("visitsalesdate", json.getString("visit_date"));
                            map.put("visitnotes", json.getString("visit_notes"));
                            map.put("visitvisitno", json.getString("visit_visitno"));
                            map.put("visitphotourl", json.getString("visit_photo_url"));
                            */

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewAllVisitByDateMap.this, error.getMessage(), LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search", "");
                //Log.d(TAG, "CARI: " + getintentvisitno );
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }





}



