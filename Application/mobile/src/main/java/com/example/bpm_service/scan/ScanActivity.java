package com.example.bpm_service.scan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bpm_service.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScanActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 672;
    private final int GET_GALLERY_IMAGE = 200;

    private String imageFilePath;
    private Uri photoUri;

    private Button applyButton, cancelButton, loadButton;
    private ImageButton qrButton;
    private TextView textView;

    private ProgressDialog progressDialog;

    Bitmap image;
    private TessBaseAPI mTess;
    String dataPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        progressDialog = new ProgressDialog(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR 영화 등록");

        // 촬영 시작
        qrButton = (ImageButton) findViewById(R.id.qrStart);

        //결과 텍스트
        textView = (TextView) findViewById(R.id.textView);

        cancelButton = (Button) findViewById(R.id.cancel_button);
        applyButton = (Button) findViewById(R.id.apply_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionCheck();
                sendTakePhotoIntent();
            }
        });

        loadButton = (Button) findViewById(R.id.loadPicture);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });
    }

    public void PermissionCheck() {
        /**
         * 6.0 마시멜로우 이상일 경우에는 권한 체크후 권한을 요청한다.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // 권한 없음
                ActivityCompat.requestPermissions(ScanActivity.this,
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
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            image = BitmapFactory.decodeFile(imageFilePath);

            OCRReady();
        }

        else if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                qrButton.setImageBitmap(image);
                imageFilePath = selectedImageUri.getPath();

                OCRReady();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void OCRReady(){
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
            exifDegree = exifOrientationToDegrees(exifOrientation);
        } else {
            exifDegree = 0;
        }
        progressDialog.setMessage("분석중입니다....");
        progressDialog.show();

        //언어파일 경로
        dataPath = getFilesDir() + "/tesseract/";
        System.out.println(getFilesDir());

        //트레이닝 데이터가 카피되어 있는지 체크
        checkFile(new File(dataPath + "tessdata/"),"eng");
        checkFile(new File(dataPath + "tessdata/"),"kor");

        // 변환 언어 선택
        String lang = "eng+kor";

        //OCR 세팅
        mTess = new TessBaseAPI();
        mTess.init(dataPath,lang);
        image = rotate(image, exifDegree);
        qrButton.setImageBitmap(image);
        processImage();
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
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

    private File createImageFile() throws IOException {
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

    // 이미지에서 텍스트 읽기
    public void processImage(){
        String OCRresult = null;
        mTess.setImage(image);
        OCRresult = mTess.getUTF8Text();

        textView.setText(OCRresult);
        progressDialog.cancel();
    }

    //언어 데이터 파일, 디바이스에 복사
    private void copyFiles(String lang){
        try{

            String filePath = dataPath + "/tessdata/"+lang+".traineddata";
            AssetManager assetManager = this.getAssets();
            InputStream inputStream = assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outputStream = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024];

            int read;
            while((read = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, read);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //언어 데이터 파일 존재 유무 체크
    private void checkFile(File dir, String lang){
        if(!dir.exists() && dir.mkdirs()){
            System.out.println("파일도 없고 폴더도없어용");
            copyFiles(lang);
        }

        if(dir.exists()){
            System.out.println("폴더는 있넹");
            String dataFilePath = dataPath + "/tessdata/"+lang+".traineddata";
            System.out.println(dataFilePath);
            File dataFile = new File(dataFilePath);
            if(!dataFile.exists()){
                copyFiles(lang);
            }
        }
    }
}