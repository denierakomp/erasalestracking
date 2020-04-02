package com.biggestworks.erasales;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
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
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.widget.Toast.LENGTH_SHORT;

public class AdapterListSalesVisit extends RecyclerView.Adapter<AdapterListSalesVisit.MyViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> list_data;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    private RecyclerView recyclerView;
    private ImageView imageutama;
    public static final String URL = "http://www.biggestworks.com/";

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Layout inflater
        View view = LayoutInflater.from(context).inflate(R.layout.activity_layout_visit_item, parent, false);

        // Hubungkan dengan MyViewHolder
        //AdapterListSalesVisit.MyViewHolder holder = new AdapterListSalesVisit.MyViewHolder(view);
        //return holder;
        return new MyViewHolder(view);
    }

    public AdapterListSalesVisit(ListSalesVisit listSalesVisit, ArrayList<HashMap<String, String>> list_data) {
        this.context = listSalesVisit;
        this.list_data = list_data;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ImageView imagefull;
        // Set widget



        // ini versi tanpa menggunakan stringSignature jadi menggunakan objectKey
        Glide.with(context)
                .load("http://www.biggestworks.com/Android/era/imagevisit/" + list_data.get(position).get("visitphotourl"))
                //.apply(RequestOptions.circleCropTransform() //hasilnya circle crop
                .apply(RequestOptions.centerCropTransform()
                        .signature(new ObjectKey(System.currentTimeMillis()))

                        .placeholder(R.mipmap.ic_launcher)
                        .override(250, 250))
                //.centerCrop()
                .into(holder.ivfotokunjungan);



        holder.tvJudul.setText(list_data.get(position).get("visitnotes"));
        holder.txtvisitnogps.setText(list_data.get(position).get("visitvisitno"));
        holder.txtnamasales.setText(list_data.get(position).get("visitsalesname"));
        Locale localeID = new Locale("in", "ID");
        String sDate1 = list_data.get(position).get("visitsalesdate");

        try {

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date1 = ftgl.parse(sDate1);
            SimpleDateFormat fstgl = new SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm:ss",localeID);
            String tgl = fstgl.format(date1);
            holder.tvTglTerbit.setText(tgl);


        } catch (ParseException e) {
            e.printStackTrace();

        }


        // Event klik ketika item list nya di klik
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, CekGPSSales.class);
                i.putExtra("intentvisitno", holder.txtvisitnogps.getText());
                i.putExtra("intentsalesname", holder.txtnamasales.getText());
                //i.putExtra("intenttglawal", txttglawal.getText());
                //i.putExtra("intenttglakhir", txttglakhir.getText());

                //i.putExtra("intenttglawal",  );
                context.startActivity(i);

            }

        });


        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(false);
                builder.setMessage("Yakin data item ini akan dihapus?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RegisterAPI api = retrofit.create(RegisterAPI.class);
                        String idvisit = list_data.get(position).get("visitid");
                        String Svisitphotourl = list_data.get(position).get("visitphotourl");

                        Call<ValueUser> call = api.deleteitemvisit(idvisit, Svisitphotourl);
                        call.enqueue(new Callback<ValueUser>() {
                            @Override
                            public void onResponse(Call<ValueUser> call, Response<ValueUser> response) {
                                String value = response.body().getValue();
                                String message = response.body().getMessage();

                                if (value.equals("1")) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ValueUser> call, Throwable t) {
                                t.printStackTrace();
                            }
                        });

                        // Remove the item on remove/button click
                        list_data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,list_data.size()); // This will call onBindViewAdapter again and change all your strings for you


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

        });



    }



    // Menentukan Jumlah item yang tampil
    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener { //tambahan implements view.onclicklistener
        // Deklarasi widget
        ImageView ivfotokunjungan;
        TextView tvJudul, tvTglTerbit, txtvisitnogps, txtnamasales, txtvisitid, txtvisitphotourl;
        protected ImageButton btn_delete;
        public MyViewHolder(View itemView) {
            super(itemView);
            // inisialisasi widget
            ivfotokunjungan = (ImageView) itemView.findViewById(R.id.ivFotoKunjungan);
            tvJudul = (TextView) itemView.findViewById(R.id.tvJudulBerita);
            tvTglTerbit = (TextView) itemView.findViewById(R.id.tvTglTerbit);
            txtvisitnogps = (TextView) itemView.findViewById(R.id.TxtVisitNoGPS);
            txtnamasales = (TextView) itemView.findViewById(R.id.Txt_NamaSales);
            //tvberitaid = (TextView) itemView.findViewById(R.id.tvBeritaID);
            txtvisitid = (TextView) itemView.findViewById(R.id.TxtVisitID);
            txtvisitphotourl = (TextView) itemView.findViewById(R.id.TxtVisitPhotoURL);
            btn_delete = (ImageButton) itemView.findViewById(R.id.ButtonDeleteItem);

            btn_delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
