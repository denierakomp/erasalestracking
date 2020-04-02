package com.biggestworks.erasales;



import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
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
import static android.widget.Toast.LENGTH_SHORT;
import static com.biggestworks.erasales.SessionManagement.KEY_STATUSID;
import static com.biggestworks.erasales.SessionManagement.KEY_SALESID;
import static java.lang.String.valueOf;

public class ViewUser extends AppCompatActivity {
    private String statusID;
    private EditText txtnamauser, txtuserlogin, txtnoojol, txtemail, txtalamat, txtnohp, txtpass;
    private TextInputLayout txtalamatL, txtnoojolL;
    private TextView txtuserid, txtstatusid, txttitle;
    private Button btnsimpanuser, btnupload, btnedituser;
    private ProgressDialog progress;
    //private String URL = "http://www.biggestworks.com/";

    public static final String URL = "http://www.biggestworks.com/";
    private static final String TAG = ViewUser.class.getSimpleName();
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


    private String UPLOADUPDATE_URL = "http://www.biggestworks.com/Android/era/updatephotoprofile.php";
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_KODE = "kode";
    private String KEY_CAPT = "caption";

    public Boolean statuscarianak = false;
    public Boolean fieldkosong = false;
    //public Boolean statuscarianakkosong = false;

    public String namaanakcari;

    SessionManagement session;
    ArrayList<HashMap<String, String>> list_datauser;

    private RecyclerView lvhape;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private StringRequest stringRequest;
    public static String userstatus;
    public NetworkImageView imgprofile;
    //private CustomAdapter customAdapter;

    public String statusid, userid;

    Unbinder unbinder;

    private static final int REQUEST_CHOOSE_IMAGE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        session = new SessionManagement(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        //final String statusid = user.get(KEY_STATUSID);
        //final String userid = user.get(KEY_USERID);
        statusid = user.get(KEY_STATUSID);
        userid = user.get(KEY_SALESID);
        ButterKnife.bind(this);
        //IMAGE OBJECT
        imgprofile = findViewById((R.id.ImageNetProfile));
        imageView = findViewById(R.id.IVProfile);

        txttitle = findViewById(R.id.titledataregistrasi);
        txtstatusid = findViewById(R.id.TxtStatusID);
        txtnamauser = findViewById(R.id.TxtNamaUser);
        txtuserlogin = findViewById(R.id.TxtLoginUserName);


        txtpass = findViewById(R.id.TxtPasswordUser);

        btnsimpanuser = findViewById(R.id.ButtonSaveUser);
        btnupload = findViewById(R.id.BtnUploadPhotoProfile);
        btnedituser = findViewById(R.id.BtnEditUser);

        //Intent intent = getIntent();
        //statusID = intent.getStringExtra("statusid");
        //txtstatusid.setText(statusID);
        //adjustinputtext(statusID);
        statusID = statusid;

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showFileChooser();
                EasyImage.openChooserWithGallery(ViewUser.this, "Pilih Gambar Profile",
                        REQUEST_CHOOSE_IMAGE);
            }
        });





        btnedituser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bukaedittext ();
            }
        });


        btnsimpanuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean fieldkosong = false;
                String nama2 = txtnamauser.getText().toString();
                String namauser2 = txtuserlogin.getText().toString();

                String pass2 = txtpass.getText().toString();


                Date mtgl = new Date();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat ftgl = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                String tgl = ftgl.format(mtgl);
                String status = txtstatusid.getText().toString();

                if (isEmpty(nama2)) {
                    Toast.makeText(ViewUser.this, "Nama lengkap harus diisi!", Toast.LENGTH_SHORT).show();
                    fieldkosong = true;
                    txtnamauser.requestFocus();

                } else if ((imageView.getDrawable()== null) && (imgprofile.getDrawable() == null)) {
                    //} else if (imageView.getDrawable() == "ic_person_black_24dp") {

                    Toast.makeText(ViewUser.this, "Harap lampirkan foto diri!", Toast.LENGTH_SHORT).show();
                    fieldkosong = true;
                    btnupload.requestFocus();

                } else if (isEmpty(namauser2)) {
                    Toast.makeText(ViewUser.this, "Nama alias / login harus diisi!", Toast.LENGTH_SHORT).show();
                    fieldkosong = true;
                    txtuserlogin.requestFocus();



                } else if (isEmpty(pass2)) {
                    Toast.makeText(ViewUser.this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                    fieldkosong = true;
                    txtpass.requestFocus();


                }

                //balikin ke false jangan LUPA!
                if (fieldkosong == false) {
                    //membuat progress dialog
                    progress = new ProgressDialog(ViewUser.this);
                    progress.setCancelable(false);
                    progress.setMessage("Loading ...");
                    progress.show();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RegisterAPI api = retrofit.create(RegisterAPI.class);
                    Call<ValueUser> call = api.updatedatauser(userid, nama2, namauser2, pass2);
                    call.enqueue(new Callback<ValueUser>() {
                        @Override
                        public void onResponse(Call<ValueUser> call, Response<ValueUser> response) {
                            String value = response.body().getValue();
                            String message = response.body().getMessage();
                            progress.dismiss();
                            if (value.equals("1")) {
                                uploadupdateImage();
                                Toast.makeText(ViewUser.this, message, Toast.LENGTH_SHORT).show();
                                //finish();
                                kosong();
                            } else {
                                Toast.makeText(ViewUser.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ValueUser> call, Throwable t) {
                            progress.dismiss();
                            Toast.makeText(ViewUser.this, "Jaringan Error!", Toast.LENGTH_SHORT).show();
                        }

                    });
                    progress.dismiss();
                }


            }
        });

        loaddatauser();
        kunciedittext();
        //adjustinputtext(txtstatusid.getText().toString());
    }

    private void loaddatauser () {
        //session = new SessionManagement(getApplicationContext());
        //HashMap<String, String> user = session.getUserDetails();
        //final String userid = user.get(KEY_USERID);
        String urlcariuser= "http://www.biggestworks.com/Android/era/getdatacari.php";

        requestQueue = Volley.newRequestQueue(ViewUser.this);
        //list_dataimage = new ArrayList<HashMap<String, String>>();

        stringRequest = new StringRequest(Request.Method.POST, urlcariuser, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response ", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("user");

                    for (int a = 0; a < jsonArray.length(); a++) {
                        //JSONObject json = jsonArray.getJSONObject(a);
                        JSONObject temp;
                        //HashMap<String, String> map = new HashMap<String, String>();
                        temp = jsonArray.getJSONObject(a);
                        String image = temp.optString("sales_photo");
                        //String userid = temp.optString("user_name");
                        String username = temp.optString("sales_name");
                        String userpass = temp.optString("sales_pass");
                        String useraccountname = temp.optString("sales_login_name");
                        String userstatus = temp.optString("sales_status");

                        if (image.isEmpty()) {  //jika foto profile tdk ada
                            imageView.setVisibility(View.VISIBLE); //image utk tampung pilih foto harus visible true
                            imgprofile.setVisibility(View.GONE);
                            //statusbayar = false;
                        } else { //jika foto profile ada
                            imageView.setVisibility(View.GONE);
                            imgprofile.setVisibility(View.VISIBLE);
                            //statusbayar = true;
                            String Urlx = "http://www.biggestworks.com/Android/era/salesphoto/" + image;
                            ImageLoader imageLoader = AppController.getInstance().getImageLoader();

                            //load gambar menggunakan networkimageview
                            imgprofile.setImageUrl(Urlx, imageLoader);

                        }

                        txtnamauser.setText(username);
                        txtstatusid.setText(userstatus);
                        //adjustinputtext(userstatus);

                        txtuserlogin.setText(useraccountname);
                        txtpass.setText(userpass);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewUser.this, error.getMessage(), LENGTH_SHORT).show();
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request
                params.put("search", userid);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadupdateImage() { //update and upload profile photo
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOADUPDATE_URL,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Add", jObj.toString());
                                //Toast.makeText(User.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                kosong();
                            } else {
                                //Toast.makeText(User.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.dismiss();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();
                        //menampilkan toast
                        //Toast.makeText(User.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                //params.put(KEY_IMAGE, getStringImage(decoded));
                params.put(KEY_IMAGE, getStringImage(decoded));
                params.put("userid", userid);

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
                        .setCropShape(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ? CropImageView.CropShape.RECTANGLE : CropImageView.CropShape.OVAL)
                        //.setCropShape(CropImageView.CropShape.RECTANGLE)
                        //.setFixAspectRatio(false)
                        .setFixAspectRatio(true)
                        .start(ViewUser.this);
            }

            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                super.onImagePickerError(e, source, type);
                Toast.makeText(ViewUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCanceled(EasyImage.ImageSource source, int type) {
                super.onCanceled(source, type);
            }
        });
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
                        .apply(new RequestOptions().circleCrop())
                        //.apply(new RequestOptions().fitCenter())
                        .into(imageView);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void kosong() {
        //imageView.setImageResource(0);
        //loaddatauser();
        finish();
        //selesai proses kembali ke menu utama
        Intent i=new Intent(ViewUser.this, ViewUser.class);
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
        //tampilkan imageview
        imageView.setVisibility(View.VISIBLE);
        imgprofile.setVisibility(View.GONE);
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






    private void bukaedittext () {
        //imageView = findViewById(R.id.IVProfile);

        //txttitle = findViewById(R.id.titledataregistrasi);
        //txtuserid.setEnabled(true);
        txtnamauser.setEnabled(true);
        txtuserlogin.setEnabled(true);
        txtpass.setEnabled(true);

        //imageView.setVisibility(View.VISIBLE);
        //imgprofile.setVisibility(View.GONE);
        btnupload.setEnabled(true);
        btnsimpanuser.setEnabled(true);

    }

    private void kunciedittext () {
        //imageView = findViewById(R.id.IVProfile);

        //txttitle = findViewById(R.id.titledataregistrasi);
        //txtuserid.setEnabled(true);
        txtnamauser.setEnabled(false);
        txtuserlogin.setEnabled(false);
        txtpass.setEnabled(false);

        imageView.setVisibility(View.VISIBLE);
        imgprofile.setVisibility(View.GONE);
        btnupload.setEnabled(false);
        btnsimpanuser.setEnabled(false);

    }

}
