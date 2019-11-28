package com.example.safproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.clickListener,
        BottomSheetDialog.bottomSheetListner, DialogListener {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private int videoPosition;
    private Uri fileUri;

    //
    private boolean permission;
    private File storage;
    private String[] storagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //In marshmallow and above we need to ask for permission first
        checkStorageAccessPermission();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //if you face lack in scrolling then add following lines
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerViewAdapter = new RecyclerViewAdapter(this, this);

        recyclerView.setAdapter(recyclerViewAdapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //load data here
                //for first time data will be loaded here
                //then it will be loaded in splash screen
                //because if we could not have permission then we could not load data in splash screen window
                storagePaths = StorageUtil.getStorageDirectories(this);

                for (String path : storagePaths) {
                    storage = new File(path);
                    Method.load_Directory_Files(storage);
                }

                recyclerViewAdapter.notifyDataSetChanged();
            }
        }
    }

    private void checkStorageAccessPermission() {
        //ContextCompat use to retrieve resources. It provide uniform interface to access resources.
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("This permission is needed to access media file in your phone")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                        1);
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            // Do nothing. Because if permission is already granted then files will be accessed/loaded in splash_screen_activity
        }
    }

    @Override
    public void onIconMoreClick(int position) {
        //From arraylist this position file is selected
        videoPosition = position;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
        bottomSheetDialog.setVideoPosition(position);
        bottomSheetDialog.show(getSupportFragmentManager(),bottomSheetDialog.getTag());
    }

    @Override
    public void deleteVideoFromList() {
        //Show delete alert
        DeleteDialog deleteDialog = new DeleteDialog();
        deleteDialog.show(getSupportFragmentManager(),deleteDialog.getTag());
    }

    @Override
    public void deleteFile() {
        //Here goes the logic of deletion
        Boolean result;
        File file = Constant.allMediaList.get(videoPosition);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            List<UriPermission> permissions = getContentResolver().getPersistedUriPermissions();

            if (permissions != null && permissions.size()>0){
                fileUri = permissions.get(0).getUri();
                deleteFileWithStorageAccessFramework(file);
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Please select external storage directorye (e.g SDCard)")
                        .setMessage("Due to change in android security policy it is not possible to delete file from" +
                                "sdcard without permission.")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                startActivityForResult(intent, 1);
                            }
                        }).show();
            }

        }else {
            result = file.delete();
            if (result){
                Constant.allMediaList.remove(videoPosition);
            }else {
                Toast.makeText(this,"Deletion Failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if (resultCode == Activity.RESULT_OK){
                    fileUri  = data.getData();
                    getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                                                                | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
        }
    }

    private void deleteFileWithStorageAccessFramework(File selectedFile) {

        DocumentFile documentFile = DocumentFile.fromTreeUri(this,fileUri);

        String[] parts = (selectedFile.getPath()).split("\\/");

        for (int i=3; i<parts.length; i++){
            if (documentFile != null){
                documentFile = documentFile.findFile(parts[i]);
            }
        }

        if (documentFile == null){
            //file not found in tree search
            //user select wrong directory

            //show permission dialog again
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Please select external storage directorye (e.g SDCard)")
                    .setMessage("Due to change in android security policy it is not possible to delete file from" +
                            "sdcard without permission.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            startActivityForResult(intent, 1);
                        }
                    }).show();
        }
        else {
            //file found
            //now we can delete it
            if (documentFile.delete()){
                //delete successfully
                Constant.allMediaList.remove(videoPosition);

                recyclerViewAdapter.notifyItemRemoved(videoPosition);
            }
            else {
                Toast.makeText(this,"Deletion Failed",Toast.LENGTH_SHORT).show();
            }
        }

    }
}
