package com.biggestworks.erasales;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.text.TextUtils.isEmpty;
//import static com.biggestworks.erasales.SessionManagement;
import static com.biggestworks.erasales.SessionManagement.KEY_SALESID;
import static com.biggestworks.erasales.SessionManagement.KEY_STATUSID;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

public class InputVisit extends AppCompatActivity {
    private String statusID;

    private TextInputLayout txtalamatL, txtnoojolL;
    private TextView txtuserid, txtvisitno,txtreview;
    private Button btnsimpanuser, btnupload;
    private ProgressDialog progress;
    //private String URL = "http://www.biggestworks.com/";

    public static final String URL = "http://www.biggestworks.com/";
    private static final String TAG = InputVisit.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    //script tambahan utk pilih gambar
    Button buttonChoose;
    ImageView imageView;
    Bitmap bitmap, decoded;
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100
    String tag_json_obj = "json_obj_req";


    private String UPLOAD_URL = "http://www.biggestworks.com/Android/era/uploadphotovisit.php";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_KODE = "kode";
    private String KEY_CAPT = "caption";

    //public Boolean fieldkosong = false;

    SessionManagement session;
    ArrayList<HashMap<String, String>> list_datauser;

    //public NetworkImageView imgprofile;
    //private CustomAdapter customAdapter;

    public String statusid, userid, NoVisit;

    Unbinder unbinder;

    private static final int REQUEST_CHOOSE_IMAGE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_visit);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        //final String statusid = user.get(KEY_STATUSID);
        //final String userid = user.get(KEY_USERID);
        statusid = user.get(KEY_STATUSID);
        userid = user.get(KEY_SALESID);
        ButterKnife.bind(this);
        //IMAGE OBJECT

        imageView = findViewById(R.id.IVProfile);

        //txttitle = findViewById(R.id.titledataregistrasi);
        txtvisitno = findViewById(R.id.TxtVisitNo);
        txtreview = findViewById(R.id.TxtReview);

        btnsimpanuser = findViewById(R.id.ButtonSaveVisit);
        btnupload = findViewById(R.id.BtnUploadPhotoProfile);

        //dapatkan no visit
        Date mtgl = new Date();
        @SuppressLint("SimpleDateFormat")
        //SimpleDateFormat ftglcomplete = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        SimpleDateFormat ftglcompleteorder = new SimpleDateFormat("ddMMyyyykkmmss");

        String tglcompleteorder = ftglcompleteorder.format(mtgl);
        String result = new StringBuffer(tglcompleteorder).reverse().toString();
        NoVisit = result + "." + userid;
        txtvisitno.setText(NoVisit);

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showFileChooser();
                //EasyImage.openChooserWithGallery(InputVisit.this, "Pilih Foto Profilenya",REQUEST_CHOOSE_IMAGE);
                EasyImage.openCamera(InputVisit.this,REQUEST_CHOOSE_IMAGE);
            }
        });


        btnsimpanuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean fieldkosong = false;

                String review = txtreview.getText().toString();

                Date mtgl = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                String tgl = ftgl.format(mtgl);

                //String status = txtvisitno.getText().toString();


                //if ((imageView.getDrawable()== null) && (imgprofile.getDrawable() == null)) {
                if ((imageView.getDrawable()== null)) {
                    //} else if (imageView.getDrawable() == "ic_person_black_24dp") {

                    Toast.makeText(InputVisit.this, "Harap lampirkan fotonya!", Toast.LENGTH_SHORT).show();
                    fieldkosong = true;
                    btnupload.requestFocus();



                } else if (isEmpty(review)) {
                    Toast.makeText(InputVisit.this, "Tujuan tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    fieldkosong = true;
                    txtreview.requestFocus();

                }

                //balikin ke false jangan LUPA!
                if (fieldkosong == false) {
                    //membuat progress dialog
                    progress = new ProgressDialog(InputVisit.this);
                    progress.setCancelable(false);
                    progress.setMessage("Loading ...");
                    progress.show();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RegisterAPI api = retrofit.create(RegisterAPI.class);
                    Call<ValueUser> call = api.tambahdatavisit(userid, review, tgl, NoVisit);
                    call.enqueue(new Callback<ValueUser>() {
                        @Override
                        public void onResponse(Call<ValueUser> call, Response<ValueUser> response) {
                            String value = response.body().getValue();
                            String message = response.body().getMessage();
                            progress.dismiss();
                            if (value.equals("1")) {

                                uploadImage();
                                Toast.makeText(InputVisit.this, message, Toast.LENGTH_SHORT).show();
                                //finish();
                                tomainactivity();
                            } else {
                                Toast.makeText(InputVisit.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ValueUser> call, Throwable t) {
                            progress.dismiss();
                            Toast.makeText(InputVisit.this, "Kesalahan dalam input kunjungan", Toast.LENGTH_SHORT).show();
                        }

                    });
                    progress.dismiss();
                    //txtreview.setText("");
                    //Toast.makeText(InputVisit.this, "Terima kasih telah memberikan review yg jujur!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //getAddress(InputVisit.this ,-6.2313023000,106.681191600);
    }



    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        //final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Add", jObj.toString());
                                //Toast.makeText(InputVisit.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                //kosong();
                            } else {
                                //Toast.makeText(InputVisit.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        //loading.dismiss();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        //loading.dismiss();

                        //menampilkan toast
                        //Toast.makeText(InputVisit.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();
                //menambah parameter yang di kirim ke web servis
                params.put(KEY_IMAGE, getStringImage(decoded));
                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        //tambahan 1 kode dibawah ini utk multiple image
        //program aslinya tidak ada multi choice image
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                CropImage.activity(Uri.fromFile(imageFile))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        //.setCropShape(CropImageView.CropShape.OVAL)
                        //.setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        //.setFixAspectRatio(false)
                        .setFixAspectRatio(true)
                        .start(InputVisit.this);
            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                super.onImagePickerError(e, source, type);
                Toast.makeText(InputVisit.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                super.onCanceled(source, type);
            }
        });
        // ----

        // Method ini berfungsi ketika sudah selesai dari activity android-image-picker
        // Jika result_ok maka gambar yang sudah di crop akan dimasukan kedalam imageview
        // yang kita olah menggunakan library glide.
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //tambahan
                //Uri filePath = data.getData();
                try {
                    //mengambil gambar dari Gallery
                    //bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                    setToImageView(getResizedBitmap(bitmap, 512));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //tambahan sd diatas
                Glide.with(this)
                        .load(new File(resultUri.getPath()))
                        //.apply(new RequestOptions().circleCrop())
                        .apply(new RequestOptions().fitCenter())
                        .into(imageView);
                /*
                https://stackoverflow.com/questions/7719617/imageview-adjustviewbounds-not-working
                This way drawable will stretch to fit in the ImageView center by preserving the aspect ratio.
                We just have to calculate the right height to make it proportional so we don't have any blank space:

                private void setImageBitmap(Bitmap bitmap, ImageView imageView){
                    float i = ((float)imageWidth)/((float)bitmap.getWidth());
                    float imageHeight = i * (bitmap.getHeight());
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(imageWidth, (int) imageHeight));
                    imageView.setImageBitmap(bitmap);
                }
                */

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        /*
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil gambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
    }

    private void kosong() {
        //imageView.setImageResource(0);
        finish();
        //selesai proses kembali ke menu utama
        //Intent i=new Intent(ViewTransBasketArchive.this, DataTransBasket.class);
        //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(i);
    }

    private void tomainactivity() {
        //imageView.setImageResource(0);
        //finish();
        //selesai proses kembali ke menu utama
        Intent i=new Intent(InputVisit.this, MainActivity.class);
        i.putExtra("intent_novisit", NoVisit);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageView.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public static void getAddress(Context context, double LATITUDE, double LONGITUDE) {

//Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {



                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                Log.d(TAG, "getAddress:  address" + address);
                Log.d(TAG, "getAddress:  city" + city);
                Log.d(TAG, "getAddress:  state" + state);
                Log.d(TAG, "getAddress:  country" + country);
                Log.d(TAG, "getAddress:  postalCode" + postalCode);
                Log.d(TAG, "getAddress:  knownName" + knownName);


            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

}

