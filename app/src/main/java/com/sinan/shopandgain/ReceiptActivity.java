package com.sinan.shopandgain;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.shopandgain.R;

import java.util.ArrayList;
import java.util.List;

public class ReceiptActivity extends AppCompatActivity {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static final int CAMERA = 0;
    private static final int GALLERY = 1;

    private int selection = -1;


    private ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);


        if(checkAndRequestPermissions(ReceiptActivity.this)){
            chooseImage(ReceiptActivity.this);
        }

        activityResultLauncher = registerForActivityResult(  //gets data from camera or library intent ,then sends it to process
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        getImage(result.getData());
                    } else { // Result was a failure
                        Toast.makeText(this, "Fotoğraf seçilemedi veya çekilemedi", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });

    }

    private void chooseImage(Context context){  //Dialog for choosing method for getting image and then directing to either camera or gallery

        final CharSequence[] optionsMenu = {"Fotoğraf Çek", "Fotoğraf Galerisinden Seç", "İptal" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setItems(optionsMenu, (dialogInterface, i) -> {

            if(optionsMenu[i].equals("Fotoğraf Çek")){

                selection = CAMERA;
                // Open the camera and get the photo

                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(takePicture);
            }
            else if(optionsMenu[i].equals("Fotoğraf Galerisinden Seç")){

                // choose from  external storage

                selection = GALLERY;

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(pickPhoto);

            }
            else if (optionsMenu[i].equals("İptal")) {
                dialogInterface.dismiss();
                onBackPressed();
            }

        });
        builder.show();
    }

    public void getImage(Intent data){ // Processes returned data  from gallery or camera as bitmap then sends it to text detection

        Bitmap bitmap = null;

        if (selection == CAMERA){

            bitmap = (Bitmap) data.getExtras().get("data");

        }else{

            Uri selectedImageUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (selectedImageUri != null) {
                Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    bitmap = BitmapFactory.decodeFile(picturePath);
                    cursor.close();
                }
            }
        }

        Intent parentIntent = new Intent();
        parentIntent.putExtra("didGetPoints", DetectText.getText(getApplicationContext(),bitmap));   //Text detection then pass data to parent activity
        setResult(RESULT_OK, parentIntent);
        finish();

    }


    public static boolean checkAndRequestPermissions(final Activity context) {   //Check for camera and gallery permission
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[0]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {  //Process result of permission
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(ReceiptActivity.this,
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Kameraya erişmek için izniniz lazım.", Toast.LENGTH_SHORT)
                        .show();

            } else if (ContextCompat.checkSelfPermission(ReceiptActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Depolama alanına erişim için izniniz lazım.",
                        Toast.LENGTH_SHORT).show();

            } else {
                chooseImage(ReceiptActivity.this);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}


