package com.biggestworks.erasales;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.widget.Toast.LENGTH_SHORT;
import static com.biggestworks.erasales.SessionManagement.KEY_SALESID;
import static com.biggestworks.erasales.SessionManagement.KEY_STATUSID;


public class ViewChart extends AppCompatActivity {
    public String statustglawal, statustglakhir;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private TextView tvDateResult, tvDateResult2;
    private Button btDatePicker, btDatePicker2, btnshare;
    public String statusid, userid ;
    public String txtcaptionfoto;
    SessionManagement session;

    ArrayList<HashMap<String, String>> list_data;

    private RequestQueue requestQueue;
    private StringRequest stringRequest;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    ImageView ivGambarBerita;

    //public JSONArray array;
    //public String Mstrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_chart);

        //FacebookSdk.sdkInitialize(this.getApplicationContext()); //FB
        //callbackManager = CallbackManager.Factory.create();
        //shareDialog = new ShareDialog(this);
        //*/

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

        btnshare = findViewById(R.id.bt_sharedata);
        ivGambarBerita = (ImageView) findViewById(R.id.ivGambarBerita);

        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareIt0(ivGambarBerita);
                onClickWhatsApp(ivGambarBerita);
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
        LihatGrafik();

        /*
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                float x=e.getX();
                float y=e.getY();
            }

            @Override
            public void onNothingSelected()
            {

            }
        });
        */

    }

    private void LihatGrafik (){
        String url = "http://www.biggestworks.com/Android/era/viewuservisitchartbydate.php";
        BarChart chart = findViewById(R.id.barchart);
        requestQueue = Volley.newRequestQueue(ViewChart.this);
        list_data = new ArrayList<HashMap<String, String>>();

        /*
        MarkerView mv = new MarkerView(this, R.layout.chart_marker);
        mv.setChartView(graph); // For bounds control i.e graph
        graph.setMarker(mv); // Set marker
        graph.setHighlightPerDragEnabled(false);
        graph.setHighlightPerTapEnabled(false);
        */

        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                if (response.isEmpty()) {
                    Toast.makeText(ViewChart.this, "Data user tidak ditemukan!", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("userchart");
                        ArrayList NoOfEmp = new ArrayList();
                        ArrayList year = new ArrayList();
                        //ArrayList marray = new ArrayList();
                        //array = jsonObject.getJSONArray("userchart");
                        //Legend legendA = chart.getLegend();
                        ArrayList<String> xAxisLabel = new ArrayList<>();
                        //tambahan ke WA
                        List<String> mStrings = new ArrayList<String>();

                        for (int a = 0; a < jsonArray.length(); a++) {
                            JSONObject json = jsonArray.getJSONObject(a);
                            HashMap<String, String> map = new HashMap<String, String>();
                            int totalvisit = json.getInt("COUNT(tb_visit.visit_id)");

                            //Integer totalvisit = Integer.valueOf(json.getString("visit_id "));

                            NoOfEmp.add(new BarEntry(totalvisit, a));
                            //year.add(json.getString("visit_sales_id"));
                            year.add(json.getString("sales_name").substring(0,2));
                            xAxisLabel.add(json.getString("sales_name"));

                            mStrings.add(json.getString("sales_name"));

                            //System.out.println(mStrings[i]);
                            //list_data.add(map);
                            //AdapterMainListVisit adapter = new AdapterMainListVisit(MainListSales.this, list_data);
                            //adapter.setClickListener(this);
                            //rvfotogal.setAdapter(adapter);

                        }
                        BarDataSet bardataset = new BarDataSet(NoOfEmp, "user's list");
                        bardataset.setHighlightEnabled(true);
                        //bardataset.setDrawHighlightIndicators(true);
                        //bardataset.setHighlightColor(Color.RED);

                        chart.animateY(2000);
                        BarData data = new BarData(year, bardataset);

                        //tambahan WA
                        String[] Mstrings = new String[mStrings.size()];
                        Mstrings = mStrings.toArray(Mstrings);//now strings is t
                        //ArrayList<HashMap<String, String>> taskItems = new ArrayList<>();
                        //Mstrings = mStrings.toArray(Mstrings);//now strings is
                        Intent intent = new Intent();
                        intent.putExtra("taskItems", Mstrings);



                        /*
                        XAxis xAxis = chart.getXAxis();
                        //xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        //xAxis.setDrawGridLines(false);
                        xAxis.setValueFormatter(new XAxisValueFormatter() {
                            @Override
                            public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {
                                //return xAxisLabel.get((int)value);
                                return xAxisLabel.get((int)a);
                            }
                        //xAxis.setValueFormatter(new IAxisValueFormatter() {
                            //@Override
                            //public String getFormattedValue(float value, AxisBase axis) {
                            //    return xAxisLabel.get((int)value);
                            //}
                        });
                        */


                        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                        chart.setData(data);
                        chart.setDescription("User's Clicked Chart");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewChart.this, error.getMessage(), LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tglawal", statustglawal);
                params.put("tglakhir", statustglakhir);
                params.put("search", userid);
                params.put("statusid", statusid);
                return params;
            }
        };

        requestQueue.add(stringRequest);



    }

    public void onClickWhatsApp(View view) {

        PackageManager pm=getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "YOUR TEXT HERE";

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            Intent intent = getIntent();
            ArrayList<String> fetchList= new ArrayList<String>();
            fetchList=  getIntent().getStringArrayListExtra("taskItems");

            waIntent.putExtra(Intent.EXTRA_TEXT, fetchList); 
            //waIntent.putExtra(Intent.EXTRA_TEXT, Mstrings);

            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public void shareIt0(View view){
        String mIsiBerita = getIntent().getStringExtra("ISI_BERITA");
        String mIsiBeritaReplace2 = mIsiBerita.replaceAll("</br></br>", "\n\n");
        String mIsiBeritaReplace3 = mIsiBeritaReplace2.replaceAll("</br>", " ");
        String shareMessage= "\nInformasi lebih lanjut, silahkan diinstal aplikasi ini di:\n\n";
        shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";

        //sharing implementation
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        //String shareBody = "string of text " + txt_var + " more text! Get the app at http://someapp.com";
        String shareBody = "string of text  more text! Get the app at http://someapp.com";

        PackageManager pm = view.getContext().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(sharingIntent, 0);
        for(final ResolveInfo app : activityList) {

            String packageName = app.activityInfo.packageName;
            Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
            targetedShareIntent.setType("text/plain");
            targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "share");
            /*
            if(TextUtils.equals(packageName, "com.facebook.katana")){
                //targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://kompas.com");
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()

                            .setQuote(getIntent().getStringExtra("JDL_BERITA"))
                            .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID))
                            .build();
                    shareDialog.show(linkContent);
                }

            } else {
                //targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                targetedShareIntent.putExtra(Intent.EXTRA_SUBJECT, getIntent().getStringExtra("JDL_BERITA"));
                targetedShareIntent.putExtra(Intent.EXTRA_TEXT, mIsiBeritaReplace3 + "\n" + shareMessage);

            }
            */

            targetedShareIntent.setPackage(packageName);
            targetedShareIntents.add(targetedShareIntent);

        }

        Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "Share Idea");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
        startActivity(chooserIntent);

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
                LihatGrafik();
                Toast.makeText(ViewChart.this, "Loading data...", LENGTH_SHORT).show();

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

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
                //tvDateResult2.setText("Tanggal dipilih : "+dateFormatter.format(newDate.getTime()));
                tvDateResult2.setText(dateFormatter.format(newDate.getTime()));
                statustglakhir = tvDateResult2.getText().toString();
                LihatGrafik();
                Toast.makeText(ViewChart.this, "Loading data...", LENGTH_SHORT).show();

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }



}
