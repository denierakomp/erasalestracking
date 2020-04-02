package com.biggestworks.erasales;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;

import static android.text.TextUtils.isEmpty;

public class AdapterMainListVisit extends RecyclerView.Adapter<AdapterMainListVisit.ViewHolder> {
    Context context;
    ArrayList<HashMap<String, String>> list_data;

    public AdapterMainListVisit(MainListSales galeriPhotosales, ArrayList<HashMap<String, String>> list_data) {
        this.context = galeriPhotosales;
        this.list_data = list_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_foto_sales, parent, false);
        //ViewHolder holder = new ViewHolder(view);
        //return holder;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // ini versi tanpa menggunakan stringSignature jadi menggunakan objectKey
        Glide.with(context)
                .load("http://www.biggestworks.com/Android/era/salesphoto/" + list_data.get(position).get("salesphoto"))
                //.apply(RequestOptions.circleCropTransform() //hasilnya circle crop
                .apply(RequestOptions.centerCropTransform()
                        .signature(new ObjectKey(System.currentTimeMillis()))

                        .placeholder(R.mipmap.ic_launcher)
                        .override(250, 250))
                //.centerCrop()
                .into(holder.imggaleri);

        holder.txtsalesphoto.setText(list_data.get(position).get("salesphoto"));
        //holder.txtharga.setTextColor(Color.RED);
        holder.txtsalesname.setText(list_data.get(position).get("salesname"));
        holder.txtsalesid.setText(list_data.get(position).get("salesid"));

        holder.txttglawal.setText(list_data.get(position).get("statustglawal"));
        holder.txttglakhir.setText(list_data.get(position).get("statustglakhir"));

    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    //public class ViewHolder extends RecyclerView.ViewHolder {
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imggaleri;
        TextView txtsalesphoto, txtsalesname, txtsalesid, txttglawal,txttglakhir ;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            imggaleri = (ImageView) itemView.findViewById(R.id.IVGaleri);
            txtsalesname = (TextView) itemView.findViewById(R.id.TxtSalesName);
            txtsalesphoto = (TextView) itemView.findViewById(R.id.TxtSalesPhoto);
            txtsalesid = (TextView) itemView.findViewById(R.id.TxtSalesID);
            txttglawal = (TextView) itemView.findViewById(R.id.TxtTglAwal);
            txttglakhir = (TextView) itemView.findViewById(R.id.TxtTglAkhir);
        }

        @Override
        public void onClick(View view) {
            //if (isEmpty(txttglawal.getText())) {

            Intent i = new Intent(context, ListSalesVisit.class);
            i.putExtra("intentsalesid", txtsalesid.getText());
            i.putExtra("intentsalesname", txtsalesname.getText());
            i.putExtra("intenttglawal", txttglawal.getText());
            i.putExtra("intenttglakhir", txttglakhir.getText());

            //i.putExtra("intenttglawal",  );
            context.startActivity(i);

        }

    }

}