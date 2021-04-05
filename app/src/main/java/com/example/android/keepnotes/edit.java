package com.example.android.keepnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.android.keepnotes.MainActivity.getImage;

public class edit extends AppCompatActivity {
    ImageView back;
    EditText edt1, edt2;
    ImageView iv2;
    ImageView save;
    DataAdapter adapter;
    ImageView imageselected;
    static final int REQUEST_CODE_SELECT_IMAGE = 1;
    static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        edt1 = findViewById(R.id.name1);
        edt2 = findViewById(R.id.number);
        imageselected = findViewById(R.id.img);
        Uri uri =  getIntent().getParcelableExtra("img");

        Bitmap bitmap= getImage(uri,getApplicationContext());

        Toast.makeText(this, "selected image", Toast.LENGTH_SHORT).show();

        imageselected.setImageBitmap(bitmap);



        //back to previous activity action
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"wow",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(edit.this,MainActivity.class);
                edit.this.startActivityForResult(intent,10);
                    }
                });


        save= findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri =  getIntent().getParcelableExtra("img");

                Toast.makeText(getApplicationContext(),"Successful",Toast.LENGTH_SHORT).show();
                String name = edt1.getText().toString();
                String number = edt2.getText().toString();
                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                intent.putExtra("img",uri);
                setResult(10, intent);
                finish();


            }
        });



    }



    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);



    }
}