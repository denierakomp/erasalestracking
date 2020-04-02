package com.biggestworks.erasales;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.biggestworks.erasales.SessionManagement.KEY_STATUSID;
import static com.biggestworks.erasales.SessionManagement.KEY_LATITUDESESSION;
import static com.biggestworks.erasales.SessionManagement.KEY_LONGITUDESESSION;
import static com.biggestworks.erasales.SessionManagement.KEY_SALESID;
import static com.biggestworks.erasales.SessionManagement.KEY_VISITNOSESSION;

public class MainActivity extends AppCompatActivity {
    public String m_novisit, statusid;
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManagement session;

    //declare GPS
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView, txtnovisit;
    Boolean Bolfakegps;

    @Override
    protected void onResume() {
        super.onResume();
        //if (m_novisit != null ) {

        //if (!isFakeGPSOn()) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            if ((statusid != null) && (m_novisit != null) ) {
                //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                getLastLocation();
            } else {
                getLastLocation2();
            }
        //}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManagement(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        //final String statusid = user.get(KEY_STATUSID);
        final String userid = user.get(KEY_SALESID);
        statusid = user.get(KEY_STATUSID);
        String visitnosesi = user.get(KEY_VISITNOSESSION);
        String visitlastlatsesi = user.get(KEY_LATITUDESESSION);
        String visitlastlongsesi = user.get(KEY_LONGITUDESESSION);


        //get data GPS user
        latTextView = findViewById(R.id.Txtlat);
        lonTextView = findViewById(R.id.Txtlong);

        //get intent no visit from input visit form
        Intent intent = getIntent();
        m_novisit = intent.getStringExtra("intent_novisit");
        txtnovisit = findViewById(R.id.TxtNoVisit);

            if (visitnosesi != null) {
                txtnovisit.setText(visitnosesi);
                latTextView.setText(visitlastlatsesi);
                lonTextView.setText(visitlastlongsesi);
            }

        Button btnkamera = findViewById(R.id.buttonKamera);
        Button btnviewkunjungan = findViewById(R.id.buttonViewHasilKunjungan);
        Button btnview = findViewById(R.id.buttonViewVisit);
        Button btnviewchart = findViewById(R.id.buttonViewChart);
        Button btnviewuser = findViewById(R.id.buttonViewUser);
        Button btnedituser = findViewById(R.id.buttonEditUser);

        /*
        if (Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            Bolfakegps = false;
        } else {
            Bolfakegps = true;
        }
        */

        //requestNewLocationData();

        /*
        boolean isMock = false;
        Location location = null;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
            //if (Settings.Secure.getString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            //isMock = Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            Toast.makeText(MainActivity.this, "FAKE GPS AKTIF!", Toast.LENGTH_LONG).show();
        } else {
            isMock = !Settings.Secure.getString(MainActivity.this.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
            Toast.makeText(MainActivity.this, "FAKE GPS TIDAK AKTIF!", Toast.LENGTH_LONG).show();
        }
        */


        //if (isMockLocationOn(Location location ,MainActivity.this)) = "true" {}

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //getLastLocation2();

        if ((statusid != null) && (m_novisit != null)) {
            //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            getLastLocation();
        } else {
            getLastLocation2();
        }

        if (statusid != null) {

            if (statusid.equals("0")) { //as user
                //btnkamera.setEnabled(true);
                btnkamera.setVisibility(View.VISIBLE);
                btnviewkunjungan.setVisibility(View.VISIBLE);
                //btnview.setEnabled(false);
                //btnviewuser.setEnabled(false);
                btnview.setVisibility(View.GONE);
                btnviewuser.setVisibility(View.GONE);
                btnviewchart.setVisibility(View.GONE);

            } else if (statusid.equals("1")) { //as manager
                //btnkamera.setEnabled(false);
                btnkamera.setVisibility(View.GONE);
                btnviewkunjungan.setVisibility(View.GONE);
                //btnview.setEnabled(true);
                btnview.setVisibility(View.VISIBLE);
                //btnviewuser.setEnabled(true);
                btnviewuser.setVisibility(View.GONE);
                btnviewchart.setVisibility(View.VISIBLE);
            } else if (statusid.equals("10")) { //as admin
                btnkamera.setVisibility(View.VISIBLE);
                btnviewkunjungan.setVisibility(View.VISIBLE);
                btnview.setVisibility(View.VISIBLE);
                btnviewuser.setVisibility(View.VISIBLE);
                btnviewchart.setVisibility(View.VISIBLE);
            }
            btnedituser.setVisibility(View.VISIBLE); //semua user bisa edit accountnya
        }

        btnkamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,InputVisit.class);
                startActivity(intent);
            }
        });

        btnviewkunjungan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ListViewVisitUser.class);
                startActivity(intent);
            }
        });

        btnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainListSales.class);
                startActivity(intent);
            }
        });

        btnviewchart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewChart.class);
                startActivity(intent);
            }
        });

        btnviewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewListUser.class);
                startActivity(intent);
            }
        });

        btnedituser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewUser.class);
                startActivity(intent);
            }
        });
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
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");

                                    session.createSessionVisit(m_novisit, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                                    updatedataGPSUser(location.getLatitude(),location.getLongitude());
                                    //tambahan untuk memberikan peringatan karena menggunakan FAKE GPS
                                    String dataLat = Double.toString(location.getLatitude());
                                    String dataLong = Double.toString(location.getLongitude());

                                    if ((dataLat.length() > 12) || (dataLat.length() > 12))  {
                                        Toast.makeText(MainActivity.this, "PERINGATAN!!! DATA GPS ANDA PALSU!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Terima kasih!", Toast.LENGTH_SHORT).show();
                                    }

                                    //
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




    //tambahan scipt GPS
    @SuppressLint("MissingPermission")
    private void getLastLocation2(){
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
                                    latTextView.setText(location.getLatitude()+"");
                                    lonTextView.setText(location.getLongitude()+"");

                                    //session.createSessionVisit(m_novisit, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
                                    //updatedataGPSUser(location.getLatitude(),location.getLongitude());
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


    private void updatedataGPSUser(Double Lat, Double Long) {

        String URL = "http://www.biggestworks.com/";
        Double datalatuser, datalonguser;

        datalatuser = Lat;
        datalonguser = Long;

        Date mtgl = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        String tgl = ftgl.format(mtgl);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ValueUser> call = api.tambahGPSsalesvisit(m_novisit, tgl, datalatuser, datalonguser);
        call.enqueue(new Callback<ValueUser>() {
            @Override
            public void onResponse(Call<ValueUser> call, Response<ValueUser> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                //progress.dismiss();
                if (value.equals("1")) {
                    //uploadImage();
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    //finish();
                    //tologinform();
                } else {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValueUser> call, Throwable t) {
                //progress.dismiss();
                Toast.makeText(MainActivity.this, "Jaringan Error!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    //To avoid these rare cases when the location == null, we called a new method requestNewLocationData() which will record the location information in runtime.
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){
        //In exmple, if you need the location update in each 5-10 seconds you can update these line as,
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(50000);
        mLocationRequest.setFastestInterval(50000);
        mLocationRequest.setSmallestDisplacement(100f);
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
            latTextView.setText(mLastLocation.getLatitude()+"");
            lonTextView.setText(mLastLocation.getLongitude()+"");
            updatedataGPSUser(mLastLocation.getLatitude(),mLastLocation.getLongitude());

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

    private boolean isFakeGPSOn() { //tdk pakai
        // returns true if mock location enabled, false if not enabled.
        if (Settings.Secure.getString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            Toast.makeText(MainActivity.this, "FAKE GPS FALSE", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(MainActivity.this, "FAKE GPS TRUE", Toast.LENGTH_SHORT).show();
            return true;

        }
    }

    public static boolean isMockLocationOn(Location location, Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return location.isFromMockProvider();
        } else {
            String mockLocation = "0";
            try {
                mockLocation = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return !mockLocation.equals("0");
        }
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Apakah anda ingin keluar dari aplikasi ini?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user pressed "yes", then he is allowed to exit from application
                //session.logoutUser();
                session.editor.clear();
                session.editor.commit();
                //finish();
                finishAffinity();
                //ActivityCompat.finishAffinity(this);
                //System.exit(0);
                // moveTaskToBack(true);
                //onDestroy();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if user select "No", just cancel this dialog and continue with app
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}
