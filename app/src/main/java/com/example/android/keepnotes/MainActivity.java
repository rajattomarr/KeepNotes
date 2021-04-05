package com.example.android.keepnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.keepnotes.DataAdapter.DataAdapterListener;
import com.example.android.keepnotes.DataAdapter.OnItemClickListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, OnItemClickListener, DataAdapterListener {
    DrawerLayout drawerLayout;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;


    //camera crop
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String[] cameraPermission;
    String[] storagePermission;


    private static final String ALLOW_KEY = "ALLOWED";
    private static final String CAMERA_PREF = "camera_pref";



    NavigationView navigationView;
    Toolbar toolbar1;
    FloatingActionButton addbutton;
    ImageView imageselected;
    BottomAppBar bottomAppBar;
    ImageView imageView;
    DataAdapter adapter;
    AlertDialog.Builder builder;
    RecyclerView rv;
    private Object Bitmap;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageselected = findViewById(R.id.img);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar1 = findViewById(R.id.toolbar);
        rv = findViewById(R.id.rv);


        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        // layout setting grid linear or staggered
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(staggeredGridLayoutManager);
        adapter = new DataAdapter(MainActivity.this, MainActivity.this, MainActivity.this);
        rv.setAdapter(adapter);


        //for navigation drawer
        setSupportActionBar(toolbar1);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar1, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }


        //next activity on add float button
        addbutton = findViewById(R.id.addfloat);
        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, edit.class);
                MainActivity.this.startActivityForResult(myIntent, 10);
                setVisible(true);
            }
        });


        //action performing on bottomactionbar

        bottomAppBar = findViewById(R.id.bottomAppBar);
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gallery:
                        Toast.makeText(MainActivity.this, "Gallery clicked", Toast.LENGTH_SHORT).show();
                        if (ContextCompat.checkSelfPermission(
                                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_CODE_STORAGE_PERMISSION
                            );
                        } else {
                            Toast.makeText(MainActivity.this, "your toast message is on screen", Toast.LENGTH_SHORT).show();
                            selectimage();

                        }
                        break;

                    case R.id.camera:
                        Toast.makeText(MainActivity.this, "Camera clicked", Toast.LENGTH_SHORT).show();
                        //we have to choose any one whether a camera or gallery
                        showImagePicDialog();


                        break;
                }
                return true;
            }
        });


    }

    // show image pic dialog camera or gallery
    private void showImagePicDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromGallery();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }


    // checking storage permissions
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }


    // Requesting  gallery permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }


    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }



    // Requesting camera permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }



    private void selectimage() {
        Toast.makeText(MainActivity.this,"your image is shown",Toast.LENGTH_SHORT);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectimage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity().start(MainActivity.this);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            DataModel dataModel = new DataModel("Titlename", "Notecontact", null, null);


            Uri uri = data.getParcelableExtra("img");
            dataModel.setImg(uri);
            dataModel.setTitlename(data.getStringExtra("name"));
            dataModel.setNotecontact(data.getStringExtra("number"));

            if (adapter != null) {
                adapter.add(dataModel);
            }
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) ;
        if (data != null) {
            Uri SelectImageuri = data.getData();
            if (SelectImageuri != null) {
                try {
//                    InputStream inputStream = getContentResolver().openInputStream(SelectImageuri);
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    Toast.makeText(this, "selected image", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, edit.class);

                    intent.putExtra("img", SelectImageuri);
                    startActivityForResult(intent, 10);
                    setVisible(true);
                    //      imageselected.setImageBitmap(bitmap);
                } catch (Exception exception) {
           //         Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("eceptionnn", exception.getMessage() + "");
                }
            }


        }


        // camera with crop code

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {


                DataModel dataModel = new DataModel("Titlename", "Notecontact", null, null);


                Uri uri = data.getParcelableExtra("img");
                dataModel.setImg(uri);
                dataModel.setTitlename(data.getStringExtra("name"));
                dataModel.setNotecontact(data.getStringExtra("number"));

                if (adapter != null) {
                    adapter.add(dataModel);
                }
            } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) ;
            if (data != null) {
                Uri SelectImageuri = data.getData();
                if (SelectImageuri != null) {
                    try {
//                    InputStream inputStream = getContentResolver().openInputStream(SelectImageuri);
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    Toast.makeText(this, "selected image", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, edit.class);

                        intent.putExtra("img", SelectImageuri);
                        startActivityForResult(intent, 10);
                        setVisible(true);
                        //      imageselected.setImageBitmap(bitmap);
                    } catch (Exception exception) {
               //         Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("eceptionnn", exception.getMessage() + "");
                    }
                }


            }


            //             Uri resultUri = result.getUri();
            //             Picasso.get().load(resultUri).into(imageselected);
        }
    }
  //  }





    //navigation menu items actions









    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_notes:

            case R.id.nav_reminder:
                Toast.makeText(this, "reminder clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.createnewlabel:




            case R.id.Archive:




            case R.id.Trash:






            case R.id.settings:






            case R.id.help:
        }
        return true;
    }

    @Override
    public void onClick(int pos, String titlename, String notenumber) {
        adapter.removeAt(pos);
    }

    public static Bitmap getImage(Uri uri, Context context) {
        InputStream inputStream = null;
        if (uri == null) {
            return null;
        }
        try {

            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        return bitmap;
    }



}