package com.sinan.shopandgain;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.shopandgain.R;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.send_button).setOnClickListener(this);

        activityResultLauncher = registerForActivityResult(                                                  // Gets data from child activity
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        showResult(result.getData().getExtras().getBoolean("didGetPoints"));
                    } else {                                                                                // Result was a failure  or process cancelled
                        Toast.makeText(this, "Fotoğraf seçilemedi veya çekilemedi", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this,ReceiptActivity.class);
        activityResultLauncher.launch(intent);

    }

    public void showResult(Boolean didGetPoint){

        String title;
        String body;

        if(didGetPoint){
            title = "Tebrikler!!!";
            body = "10 Sadakat Puanı Kazandınız";
        }else{
            title = "Uyarı";
            body = "Fişinizde P&G ürünleri tespit edilemedi";
        }

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(body);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "TAMAM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


}