package com.biggestworks.erasales;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.text.TextUtils.substring;
import static android.widget.Toast.LENGTH_SHORT;
import static com.biggestworks.erasales.SessionManagement.KEY_STATUSID;

public class AdapterViewListUser extends RecyclerView.Adapter<AdapterViewListUser.MyViewHolder> {
    public static final String URL = "http://www.biggestworks.com/";
    public String Stringuserid, Stringuserstatus;
    Context context;
    ArrayList<HashMap<String, String>> list_data;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private RecyclerView recyclerView;
    private ImageView imageutama;
    SessionManagement session;
    public String statusid, userid;
    private ArrayList<String> arrayList;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        session = new SessionManagement(context.getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        //final String statusid = user.get(KEY_STATUSID);
        //final String userid = user.get(KEY_USERID);
        statusid = user.get(KEY_STATUSID);

        // Layout inflater
        View view = LayoutInflater.from(context).inflate(R.layout.activity_layout_view_user_detail, parent, false);

        // Hubungkan dengan MyViewHolder
        //AdapterViewListUser.MyViewHolder holder = new AdapterViewListUser.MyViewHolder(view);
        //return holder;
        return new MyViewHolder(view);

    }

    public AdapterViewListUser(ViewListUser viewListUser, ArrayList<HashMap<String, String>> list_data) {
        this.context = viewListUser;
        this.list_data = list_data;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageView imagefull;
        // Set widget
        // ini versi tanpa menggunakan stringSignature jadi menggunakan objectKey
        Glide.with(context)
                .load("http://www.biggestworks.com/Android/era/salesphoto/" + list_data.get(position).get("userphoto"))
                //.apply(RequestOptions.circleCropTransform() //hasilnya circle crop
                .apply(RequestOptions.centerCropTransform()
                        .signature(new ObjectKey(System.currentTimeMillis()))
                        .placeholder(R.mipmap.ic_launcher)
                        .override(250, 250))
                //.centerCrop()
                .into(holder.ivfotouser);

        holder.txtusername.setText(list_data.get(position).get("username"));
        holder.txtuserid.setText(list_data.get(position).get("userid"));
        holder.txtuserleadbyid.setText(list_data.get(position).get("userleadbyid"));
        String cekboxmanager = list_data.get(position).get("userstatus");
        //boolean resultOfComparison = stringA.equals(stringB);

        if (cekboxmanager.equals("0")) { //1 = sebagai user biasa
            holder.cekboxasmanager.setChecked(false); //jangan jadikan manager

        } else {
            holder.cekboxasmanager.setChecked(true); //status sudah menjadi manager
        }

        //holder.cekboxasmanager.setText(list_data.get(position).get("userstatus"));
        if (statusid.equals("10")) {
            holder.cekboxasmanager.setEnabled(true);
        } else {
            holder.cekboxasmanager.setEnabled(false);
        }

        Locale localeID = new Locale("in", "ID");
        String sDate1 = list_data.get(position).get("userlastlogin");

        try {

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date1 = ftgl.parse(sDate1);
            SimpleDateFormat fstgl = new SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm:ss",localeID);
            String tgl = fstgl.format(date1);
            holder.txttgllogin.setText(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Event klik ketika item list nya di klik
        holder.cekboxasmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusid.equals("10")) {
                    boolean checked = ((CheckBox) view).isChecked();
                    //checked = true;
                    if (checked) {
                        checked = true;
                        //Stringuserstatus.equals("1");
                        updatestatus(holder.txtuserid.getText().toString(), "1");
                    } else {
                        checked = false;
                        //Stringuserstatus.equals("0");
                        updatestatus(holder.txtuserid.getText().toString(), "0");
                    }
                } else {
                    Toast.makeText(context, "Hanya Admin yg dapat merubah akses!", Toast.LENGTH_LONG).show();

                }
            }

        });

        //holder.spinnerbox.setText(list_data.get(position).get("username"));

        /*
        //populate data list_manager from database
        String url = "http://www.biggestworks.com/Android/era/getlistmanager.php";
        //rvfotogal = (RecyclerView) findViewById(R.id.RV_ListViewUser);
        //RecyclerView.LayoutManager llm = new GridLayoutManager(getApplicationContext(), 2);
        //rvfotogal.setLayoutManager(llm);

        //requestQueue = Volley.newRequestQueue(context);
        //list_data = new ArrayList<HashMap<String, String>>();
        List<String> lables = new ArrayList<String>();

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                if (response.isEmpty()) {
                    Toast.makeText(context, "Data user tidak ditemukan!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("list_manager");

                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject json = jsonArray.getJSONObject(a);
                            HashMap<String, String> map = new HashMap<String, String>();
                            //map.put("userid", json.getString("sales_id"));
                            map.put("username", json.getString("sales_name"));

                            //lables.add(categoriesList.get(i).getName());
                            lables.add(String.valueOf(map));
                            //list_data.add(map);

                            //AdapterViewListUser adapter = new AdapterViewListUser(ViewListUser.this, list_data);
                            //adapter.setClickListener(this);
                           // rvfotogal.setAdapter(adapter);


                            // Creating adapter for spinner
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context,
                                    android.R.layout.simple_spinner_item, lables);

                            // Drop down layout style - list view with radio button
                            spinnerAdapter
                                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            holder.spinnerbox.setAdapter(spinnerAdapter);

          /*
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("search", "1"); //as user & sales
                return params;
            }
        };
        requestQueue.add(stringRequest);
        */
        /*
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.groupby, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        holder.spinnerbox.setAdapter(adapter);
        */
        //go

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
        //        R.array.groupby, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //holder.spinnerbox.setAdapter(adapter);

        //mulai dari sini

        /*
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.groupby, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        holder.spinnerbox.setAdapter(adapter);





        holder.spinnerbox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                // Showing selected spinner item
                if (position != 0) {
                    Toast.makeText(parent.getContext(), "Selected: " + item + position, Toast.LENGTH_LONG).show();
                    //updatestatus(holder.txtuserid.getText().toString(), "1");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });



        /*
        //ini masih error
        holder.spinnerbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(this, "Selected " + holder.spinnerbox.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Hallo : " + salesnamefullString + ", Semangat ya!" , Toast.LENGTH_SHORT).show();
                //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                String a = holder.spinnerbox.getSelectedItem().toString();
                //Toast.makeText(context, "Selected " + a, Toast.LENGTH_SHORT).show();


            }
        });
        */


    }



    // Menentukan Jumlah item yang tampil
    @Override
    public int getItemCount() {
        return list_data.size();
    }

    //public class MyViewHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {
    public class MyViewHolder extends RecyclerView.ViewHolder  {
        // Deklarasi widget
        ImageView ivfotouser;
        TextView txtuserid, txttgllogin, txtvisitnogps, txtusername, txtuserleadbyid;
        CheckBox cekboxasmanager;
        Spinner spinnerbox;
        public MyViewHolder(View itemView) {
            super(itemView);
            // inisialisasi widget
            ivfotouser = (ImageView) itemView.findViewById(R.id.IVUserGaleri);
            //tvJudul = (TextView) itemView.findViewById(R.id.tvJudulBerita);
            txttgllogin = (TextView) itemView.findViewById(R.id.TxtTglLogin);
            //txtvisitnogps = (TextView) itemView.findViewById(R.id.TxtVisitNoGPS);
            txtusername = (TextView) itemView.findViewById(R.id.TxtUserName);
            txtuserid = (TextView) itemView.findViewById(R.id.TxtUserID);
            txtuserleadbyid = (TextView) itemView.findViewById(R.id.TxtLeadByID);
            cekboxasmanager = (CheckBox) itemView.findViewById(R.id.CekBox_AsManager);
            spinnerbox = (Spinner) itemView.findViewById(R.id.spinner1);



            //arrayList = new ArrayList<String>();
            String url = "http://www.biggestworks.com/Android/era/getlistmanager.php";
            List<String> lables = new ArrayList<String>();
            requestQueue = Volley.newRequestQueue(context);
            stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("response ", response);
                    if (response.isEmpty()) {
                        Toast.makeText(context, "Data user tidak ditemukan!", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("list_manager");

                            for (int a = 0; a < jsonArray.length(); a++) {
                                JSONObject json = jsonArray.getJSONObject(a);
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("userid", json.getString("sales_id"));
                                map.put("username", json.getString("sales_name"));

                                //lables.add(categoriesList.get(i).getName());
                                //lables.add(String.valueOf(map));
                                //lables.add(json.getString("sales_id"));
                                lables.add(json.getString("sales_id") + " | " +json.getString("sales_name"));
                                // Creating adapter for spinner
                                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(context,
                                        android.R.layout.simple_spinner_item, lables);

                                // Drop down layout style - list view with radio button
                                spinnerAdapter
                                        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                // attaching data adapter to spinner
                                spinnerbox.setAdapter(spinnerAdapter);
                                spinnerbox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String item = parent.getItemAtPosition(position).toString();
                                        // Showing selected spinner item
                                        if (position != 0) {
                                            //Toast.makeText(parent.getContext(), "Selected: " + item + position, Toast.LENGTH_LONG).show();
                                            String idmanagernya = substring(item,0,2);
                                            txtuserleadbyid.setText(idmanagernya);
                                            updategroup(txtuserid.getText().toString(), idmanagernya);
                                            Toast.makeText(parent.getContext(), "Berhasil memasukkan ke group : " + item, LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }

                                });

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, error.getMessage(), LENGTH_SHORT).show();
                }

            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("search", "1"); //as user & sales
                    return params;
                }
            };
            requestQueue.add(stringRequest);




            /*
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                    R.array.groupby, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinnerbox.setAdapter(adapter);
            spinnerbox.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();
                    // Showing selected spinner item
                    if (position != 0) {
                        Toast.makeText(parent.getContext(), "Selected: " + item + position, Toast.LENGTH_LONG).show();
                        //updatestatus(holder.txtuserid.getText().toString(), "1");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });
            */

            //spinnerbox.setOnItemSelectedListener(this);

            /*
            spinnerbox.setOnClickListener(new View.OnClickListener() {
            //spinnerbox.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onClick(View v) {
                    //Toast.makeText(this, "Selected " + holder.spinnerbox.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(), "Hallo : " + salesnamefullString + ", Semangat ya!" , Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    String a = spinnerbox.getSelectedItem().toString();
                    //Toast.makeText(context, "Selected " + a, Toast.LENGTH_SHORT).show();


                }
            });
            */

        }
        /*
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position != 0) {
                Toast.makeText(context, "tes1", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "tes2", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }*/





    }

    public void updatestatus (String useridupdate, String statusupdate){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ValueUser> call = api.updateuser(useridupdate, statusupdate);
        call.enqueue(new Callback<ValueUser>() {
            @Override
            public void onResponse(Call<ValueUser> call, retrofit2.Response<ValueUser> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                //progress.dismiss();
                if (value.equals("1")) {
                    //uploadImage();
                    Toast.makeText(context, message, LENGTH_SHORT).show();
                    //finish();
                    //tologinform();
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValueUser> call, Throwable t) {
                //progress.dismiss();
                //Toast.makeText(TambahUser.this, "Tambah data user error!", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void updategroup (String useridupdate, String statusgroup){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<ValueUser> call = api.updategroup(useridupdate, statusgroup);
        call.enqueue(new Callback<ValueUser>() {
            @Override
            public void onResponse(Call<ValueUser> call, retrofit2.Response<ValueUser> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                //progress.dismiss();
                if (value.equals("1")) {
                    //uploadImage();
                    Toast.makeText(context, message, LENGTH_SHORT).show();
                    //finish();
                    //tologinform();
                } else {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ValueUser> call, Throwable t) {
                //progress.dismiss();
                //Toast.makeText(TambahUser.this, "Tambah data user error!", Toast.LENGTH_SHORT).show();
            }

        });

    }
    /*
    private void getdata() {
        String url = "http://www.biggestworks.com/Android/era/getlistmanager.php";
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //JSONObject j = null;
                        //JSONObject jsonObject = null;
                        try {
                            //j = new JSONObject(response);
                            //result = j.getJSONArray(JSON_ARRAY);
                            //empdetails(result);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("list_manager");


                            for (int a = 0; a < jsonArray.length(); a++) {
                                JSONObject json = jsonArray.getJSONObject(a);


                                    JSONObject json = j.getJSONObject(i);
                                    arrayList.add(json.getString("sales_name"));
                                    //HashMap<String, String> map = new HashMap<String, String>();
                                    //map.put("userid", json.getString("sales_id"));
                                    //map.put("username", json.getString("sales_name"));


                            }
                            // arrayList.add(0,"Select Employee");
                            //spinnerbox.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, arrayList));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
    private void empdetails(JSONArray j) {
        for (int i = 0; i < j.length(); i++) {
            try {
                JSONObject json = j.getJSONObject(i);
                arrayList.add(json.getString("sales_name"));
                //HashMap<String, String> map = new HashMap<String, String>();
                //map.put("userid", json.getString("sales_id"));
                //map.put("username", json.getString("sales_name"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // arrayList.add(0,"Select Employee");
        holder.spinnerbox.setAdapter(new ArrayAdapter<String>(PopulateSpinnerMysql.this, android.R.layout.simple_spinner_dropdown_item, arrayList));
    }
    */

}
