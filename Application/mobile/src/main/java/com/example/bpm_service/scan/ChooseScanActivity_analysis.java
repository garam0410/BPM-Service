package com.example.bpm_service.scan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bpm_service.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ChooseScanActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private final int GET_GALLERY_IMAGE = 200;

    private String imageFilePath;
    private Uri photoUri;

    private ImageButton newButton,loadButton;

    private ProgressDialog progressDialog;
    private IntentIntegrator qrScan;

    Bitmap image;

    String ocrApiGwUrl;
    String ocrSecretKey;

    private String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_scan);
        SharedPreferences sharedPreferences;

        sharedPreferences = getSharedPreferences("PREF", Context.MODE_PRIVATE);
        ocrApiGwUrl = sharedPreferences.getString("ocr_api_gw_url", "https://81530adfba1349cf83ce96ee4b6549a8.apigw.ntruss.com/custom/v1/10017/76900c26a46eb427dd3d2c8e453aabb0b43b93e80ceb037106524173d73ef18e/general");
        ocrSecretKey = sharedPreferences.getString("ocr_secret_key", "QkJXcndkeW1UcHVaTlFTa3dOS3JYTmhUd3JtUmhLcVA=");
        progressDialog = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR 영화 등록");

        Intent intent = getIntent();
        if(intent!=null){
            userId = intent.getStringExtra("userId");
        }

        // 촬영 시작
        newButton = findViewById(R.id.newPicture);

        qrScan = new IntentIntegrator(this);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionCheck();
                SendTakePhotoIntent();
            }
        });

        loadButton = (ImageButton) findViewById(R.id.loadPicture);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });
    }

    public class PapagoNmTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            return OcrProc.main(strings[0], strings[1], strings[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            ReturnThreadResult(result);
        }
    }

    public void ReturnThreadResult(String result){
        System.out.println("### Return Thread Result");
        String translateText = "";

        String rlt = result;

        try{
            JSONObject jsonObject = new JSONObject(rlt);

            JSONArray jsonArray = jsonObject.getJSONArray("images");

            System.out.println(jsonArray);

            for(int i = 0; i<jsonArray.length(); i++){
                JSONArray jsonArray_fields = jsonArray.getJSONObject(i).getJSONArray("fields");

                for(int j = 0; j<jsonArray_fields.length(); j++){
                    String inferText = jsonArray_fields.getJSONObject(j).getString("inferText");
                    translateText += inferText;
                    translateText+= " ";
                }
            }
            // 여기서 텍스트를 넘겨준다?
            //textView.setText(translateText);
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra("translateText", translateText);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void PermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // 권한 없음
                ActivityCompat.requestPermissions(ChooseScanActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
            } else {
                // 권한 있음
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            image = BitmapFactory.decodeFile(imageFilePath);
            RotateReady();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String temp = Base64.getEncoder().encodeToString(bytes);

            ChooseScanActivity.PapagoNmTask papagoNmTask = new PapagoNmTask();
            papagoNmTask.execute(ocrApiGwUrl,ocrSecretKey, temp);
        }

        else if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageFilePath = selectedImageUri.getPath();
                RotateReady();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                String temp = Base64.getEncoder().encodeToString(bytes);

                ChooseScanActivity.PapagoNmTask papagoNmTask = new PapagoNmTask();
                papagoNmTask.execute(ocrApiGwUrl,ocrSecretKey, temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void RotateReady(){
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(imageFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int exifOrientation;
        int exifDegree;

        if (exif != null) {
            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifDegree = ExifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        image = Rotate(image, exifDegree);
    }

    private int ExifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap Rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void SendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = CreateImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File CreateImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }
}