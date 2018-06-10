package com.trinity.phoenix.fifa_18russiaadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CountryActivity extends AppCompatActivity {

    EditText editText_name;
    Spinner spinner_name;
    ImageView imageView_image;
    Button button_upload,add;

    FirebaseStorage storage;
    StorageReference storageReference;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    String s,name;

    FirebaseDatabase firebaseDatabaseadd,firebaseDatabaseadd1;
    DatabaseReference databaseReferenceadd,databaseReferenceadd1;

    private Uri file_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        editText_name = (EditText)findViewById(R.id.addNameEditText);
        spinner_name = (Spinner)findViewById(R.id.getNameSpinner);
        imageView_image = (ImageView)findViewById(R.id.getImageView);
        button_upload = (Button)findViewById(R.id.uploadButton);
        add = (Button)findViewById(R.id.add);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabaseadd = FirebaseDatabase.getInstance();
                databaseReferenceadd = firebaseDatabaseadd.getReference().child("countryname");
                databaseReferenceadd.push().setValue(editText_name.getText().toString().toUpperCase().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CountryActivity.this,"Success",Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        imageView_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST);

                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //getFileUri();
                i.putExtra(MediaStore.EXTRA_OUTPUT, file_uri);
                startActivityForResult(i, PICK_IMAGE_REQUEST);
            }
        });

        button_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image();
            }
        });

        fetchname();

    }

    private void fetchname() {
        firebaseDatabaseadd = FirebaseDatabase.getInstance();
        databaseReferenceadd = firebaseDatabaseadd.getReference().child("countryname");
        databaseReferenceadd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetch(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void image(){

        if(filePath != null)
        {
            final String name = spinner_name.getSelectedItem().toString().trim();
            Toast.makeText(CountryActivity.this,"name "+name,Toast.LENGTH_LONG).show();

            final ProgressDialog progressDialog = new ProgressDialog(CountryActivity.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            firebaseDatabaseadd1 = FirebaseDatabase.getInstance();
            databaseReferenceadd1 = firebaseDatabaseadd.getReference();
            String key = databaseReferenceadd1.push().getKey();
            final CountryData teamappData = new CountryData();
            teamappData.setUrl(key);

            final StorageReference ref = storageReference.child(name).child(teamappData.getUrl());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(CountryActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            teamappData.setUrl(String.valueOf(taskSnapshot.getDownloadUrl()));
                            teamappData.setCountryname(name);
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = firebaseDatabase.getReference().child(teamappData.getCountryname()).child("metaData");
                            databaseReference.setValue(teamappData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(CountryActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CountryActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });

        }
    }



    private void fetch(DataSnapshot dataSnapshot){
        java.util.ArrayList<String> strings = new java.util.ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren())
        {
            strings.add(String.valueOf(ds.getValue()));
        }
        ArrayAdapter<String> adp1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strings);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_name.setAdapter(adp1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView_image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST) {
            filePath = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView_image.setImageBitmap(bitmap);
            Uri tempUri = getImageUri(getApplicationContext(), bitmap);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            filePath = tempUri;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}