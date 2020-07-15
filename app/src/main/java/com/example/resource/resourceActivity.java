package com.example.resource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.os.Parcelable;
import android.os.PersistableBundle;
import android.text.InputType;

import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class resourceActivity extends AppCompatActivity {

    ImageView loggout;
    String mytext;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor meditor;
    RecyclerView recyclerView;
    recycleradapter adapter;
    uploader Uploader;

    ImageView uploadbtn;
    List<uploader> filenames;

    Uri pdfuri;


    DatabaseReference firebaseDatabase;
    StorageReference firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Calendar calendar = Calendar.getInstance();
        String Month = DateFormat.getDateInstance(DateFormat.MONTH_FIELD).format(calendar.getTime());

        TextView textMonth = findViewById(R.id.day);
        textMonth.setText(Month);


        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //loaddata();
        filenames= PrefConfig.loaddata(this);
        if (filenames == null) filenames = new ArrayList<>();

        adapter = new recycleradapter(filenames);
        adapter.setOnItemClickedListner(new recycleradapter.OnItemClickListner() {
            @Override
            public void OnDeleteClicked(int position) {
                removeItem(position);
            }

            @Override
            public void OnDownloadClicked(int position) {
                downloaditem(position);
            }

        });

        sharedPreferences = getSharedPreferences("resources", Context.MODE_PRIVATE);
        meditor = sharedPreferences.edit();


        loggout = (ImageView) findViewById(R.id.loggout);
        uploadbtn = (ImageView) findViewById(R.id.uploadbtn);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("uploads");
        firebaseStorage = FirebaseStorage.getInstance().getReference("uploads");


        loggout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(resourceActivity.this);
                alert.setTitle("Confirm Logout.");
                alert.setIcon(R.mipmap.logout);
                alert.setMessage("Are you sure about logging out....??");
                alert.setCancelable(false);
                // alert.setCanceledOnTouchOutside(false);

                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        meditor.putBoolean("isloggedin", false).commit();
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(resourceActivity.this, "you cancelled logging out", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }
        });


    }


    public void uploadaction(View view) {
        if (ContextCompat.checkSelfPermission(resourceActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            selectpdf();
        } else {
            ActivityCompat.requestPermissions(resourceActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectpdf();
        }
        if(requestCode==6 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "permissions granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "please grant permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectpdf() {

        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select pdf"), 86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfuri = data.getData();
            uploadpdf(pdfuri);
        }
        else{
            Toast.makeText(this, "please select a file", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadpdf(Uri pdfuri) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading.....");
        progressDialog.show();


        StorageReference reference = firebaseStorage.child("uploads/" + System.currentTimeMillis() + ".pdf");
        reference.putFile(pdfuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Name(uri);
                progressDialog.dismiss();
                Toast.makeText(resourceActivity.this, "file selected", Toast.LENGTH_SHORT).show();

            }


            private String Name(final Task<Uri> uri) {
                AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(resourceActivity.this);
                alertDialogbuilder.setTitle("Enter file name");
                final EditText pdfname = new EditText(resourceActivity.this);
                pdfname.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialogbuilder.setView(pdfname);
                alertDialogbuilder.setCancelable(false);
                alertDialogbuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mytext = pdfname.getText().toString();
                        //  adapter.UpdateList(filenames);
                        Uri url = uri.getResult();
                        Uploader = new uploader(mytext, url.toString());
                        filenames.add(Uploader);
                        PrefConfig.savedata(getApplicationContext(),filenames);
                        firebaseDatabase.child(firebaseDatabase.push().getKey()).setValue(Uploader);
                        recyclerView.setAdapter(adapter);
                       // savedata();
                        Toast.makeText(resourceActivity.this, "file uploaded successfully", Toast.LENGTH_SHORT).show();

                    }
                });
                alertDialogbuilder.show();
                return mytext;

            }

        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setMessage("Uploaded:" + (int) progress + "%");
            }
        });

    }

    /**
    @Override
    protected void onStop() {
        super.onStop();
      //  savedata();
    }**/

    public void removeItem(int position) {
        filenames.remove(position);
        adapter.notifyItemRemoved(position);
        PrefConfig.savedata(getApplicationContext(),filenames);

    }

    public void downloaditem(int position) {
        File file = new File(getExternalFilesDir(null), "Dummy");
        String URL = filenames.get(position).getFileurl().trim();
        String name = filenames.get(position).getFilename();
        DownloadManager.Request request=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            request = new DownloadManager.Request(Uri.parse(URL));
            request.setTitle(name);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);
            request.setDescription("Downloading");
            request.allowScanningByMediaScanner();
            request.setDestinationUri(Uri.fromFile(file));
            request.setRequiresCharging(false);
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
            Toast.makeText(this,getMimeType(Uri.parse(URL)), Toast.LENGTH_SHORT).show();
            request.setMimeType(getMimeType(Uri.parse(URL)));

        }
        else
            {
            request = new DownloadManager.Request(Uri.parse(URL));
            request.setTitle(name);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                    DownloadManager.Request.NETWORK_MOBILE);
            request.setDescription("Downloading");
            request.allowScanningByMediaScanner();
            request.setDestinationUri(Uri.fromFile(file));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
            Toast.makeText(this,getMimeType(Uri.parse(URL)), Toast.LENGTH_SHORT).show();
            request.setMimeType(getMimeType(Uri.parse(URL)));

        }
        DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadmanager.enqueue(request);
    }


    @Override
    public void onResume(){
        super.onResume();
        PrefConfig.loaddata(this);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PrefConfig.loaddata(this);
        adapter.notifyDataSetChanged();
    }

    private String getMimeType(Uri uri)
    {
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}

