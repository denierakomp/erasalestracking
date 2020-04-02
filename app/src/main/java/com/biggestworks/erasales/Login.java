package com.biggestworks.erasales;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {
    //Defining views
    private EditText editTextEmail;
    private EditText editTextPassword;

    private Context context;
    private AppCompatButton buttonLogin, buttonRegister, buttonTamu;
    private ProgressDialog pDialog;

    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    //ArrayList<HashMap<String, String>> list_datauser;

   /* public final static String TAG_ADMIN = "admin";
    public final static String TAG_USER = "user";
    public final static String TAG_GUEST = "guest";

    public boolean statuslogin = false;*/

    //tambahan kode dari app sharedpreff
    // Alert Dialog Manager
    com.biggestworks.erasales.AlertDialogManager alert = new com.biggestworks.erasales.AlertDialogManager();
    //tambahan kode dari app sharedpreff
    // Session Manager Class
    com.biggestworks.erasales.SessionManagement session;


    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = Login.this;

        // Session Manager
        session = new SessionManagement(getApplicationContext());

        //Initializing views
        pDialog = new ProgressDialog(context);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (AppCompatButton) findViewById(R.id.buttonLogin);
        buttonRegister = (AppCompatButton) findViewById(R.id.buttonRegister);

        //Adding click listener
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });


    }

    private void register (){
        Intent intent = new Intent(Login.this, TambahUser.class);
        startActivity(intent);

    }


    private void login() {
        //Getting values from edit texts
        //final String email = editTextEmail.getText().toString().trim();
        final String salesname = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();


        pDialog.setMessage("Login Process...");
        showDialog();
        //Creating a string request

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppVar.LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String salesnameString = null;
                String salesnamefullString = null;
                String salesidString = null;
                String salesstatusString = null;
                //script baru untuk mendapatkan info user saat login
                Log.d("response ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("infosales");
                    for (int a = 0; a < jsonArray.length(); a++) {
                        JSONObject json = jsonArray.getJSONObject(a);
                        //HashMap<String, String> map = new HashMap<String, String>();
                        //map.put("useridinfo", json.getString("user_id"));
                        salesidString = json.getString("sales_id"); //ambil data user dari database saat login
                        salesnameString = json.getString("sales_login_name");
                        salesnamefullString = json.getString("sales_name");
                        salesstatusString = json.getString("sales_status"); //status user apakah sebagai user biasa atau bukan

                        //list_datauser.add(map);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //txtstatususer.setText = (list_datauser.get(position));
                //If we are getting success from server
                if (response.contains(AppVar.LOGIN_SUCCESS1)) {
                    Toast.makeText(getApplicationContext(), "Hallo : " + salesnamefullString + ", Semangat ya!" , Toast.LENGTH_SHORT).show();
                    //session.createLoginSessionAdm(email, useridinfo, password);
                    session.createLoginSessionAdm(salesnameString, salesidString, salesstatusString);
                    //session.editor.putBoolean(IS_ADMIN, true);
                    hideDialog();
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    //intent.putExtra("leveluser", "admin");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    // Add new Flag to start new Activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //finish();
                    startActivity(intent);

                } else {
                    if (response.contains(AppVar.LOGIN_SUCCESS0)) {  //login as user
                        Toast.makeText(getApplicationContext(), "Hallo : " + salesnamefullString + ", Semangat ya!" , Toast.LENGTH_SHORT).show();
                        session.createLoginSession(salesnameString, salesidString, salesstatusString);
                        hideDialog();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        //intent.putExtra("leveluser", "user");
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        // Add new Flag to start new Activity
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //finish();
                        startActivity(intent);
                    } else {
                        hideDialog();
                        //Displaying an error message on toast
                        Toast.makeText(context, "Nama / Password Salah", Toast.LENGTH_LONG).show();
                    }

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //You can handle error here if you want
                        hideDialog();
                        Toast.makeText(context, "Kesalahan Jaringan Server", Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                //params.put(AppVar.KEY_EMAIL, email);
                params.put(AppVar.KEY_SALESNAME, salesname);
                params.put(AppVar.KEY_PASSWORD, password);

                //returning parameter
                return params;
            }
        };

        //Adding the string request to the queue
        Volley.newRequestQueue(this).add(stringRequest);

    }


    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    //tambahan scipt GPS
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    //latTextView.setText(location.getLatitude()+"");
                                    //lonTextView.setText(location.getLongitude()+"");
                                    //updatedataGPSUser(location.getLatitude(),location.getLongitude());
                                    //session.createSessionVisit(m_novisit, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Mohon AKTIF-kan GPS anda!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }


    //To avoid these rare cases when the location == null, we called a new method requestNewLocationData() which will record the location information in runtime.
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        //In exmple, if you need the location update in each 5-10 seconds you can update these line as,
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(5f);
        //Like if you want to make an app that'll get users location update realtime you can comment this line mLocationRequest.setNumUpdates(1);
        mLocationRequest.setNumUpdates(1000);

        //When an update receives it'll call a callBack method named mLocationCallback
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //latTextView.setText(mLastLocation.getLatitude()+"");
            //lonTextView.setText(mLastLocation.getLongitude()+"");
            //updatedataGPSUser(mLastLocation.getLatitude(),mLastLocation.getLongitude());

        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    //sd disini script GPS

    @Override
    public void onBackPressed() {
                finishAffinity();
            }

}

