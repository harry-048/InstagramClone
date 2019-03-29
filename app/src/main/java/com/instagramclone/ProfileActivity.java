package com.instagramclone;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private FirebaseAuth mAuth;
    private String user;
    private String username;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            user=currentUser.getEmail();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        final String name = intent.getStringExtra("username");

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Log.d("profile:","sucessfull");
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        final ListView listView = findViewById(R.id.listView);
        final ArrayList<ListFeed> feeds = new ArrayList<>();
        final CustomAdapterRecyclerView customAdapter = new CustomAdapterRecyclerView(feeds, this);
        recyclerView.setAdapter(customAdapter);




        database.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                if (username.equals(document.getData().get("user"))){
                                   // Toast.makeText(ProfileActivity.this, "nm:"+name+",db:"+document.getData().get("user"), Toast.LENGTH_SHORT).show();
                                    Log.d("user", name+ " , username: " + document.getData().get("user"));
                                    feeds.add(new ListFeed(document.getData().get("user")+"",document.getData().get("imageurl")+"",document.getData().get("timestamp")+""));
                                    customAdapter.notifyDataSetChanged();
                                }
                                else
                                    Toast.makeText(ProfileActivity.this, "not works", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("Failed", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            Uri seletedImage = data.getData();
            if(requestCode==1 && resultCode==RESULT_OK){
                try {

                    //Toast.makeText(this, "username: "+username, Toast.LENGTH_SHORT).show();
                    ContentResolver contentResolver=getContentResolver();
                    MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();


                    String fileext= mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(seletedImage));
                    ByteArrayOutputStream stream=null;
                    if (fileext=="png"){
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),seletedImage);
                        stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream);
                    }
                    if (fileext=="jpg"){
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),seletedImage);
                        stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                    }


                    byte[] byteArray = stream.toByteArray();

                    final StorageReference postRef = storageRef.child(System.currentTimeMillis()+new Random().nextInt(61) + "");

                    UploadTask uploadTask = postRef.putBytes(byteArray);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            savePost(username, postRef.getName());
                        }
                    });

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }*/

   /* private void savePost(String user, String path) {
        Map<String, Object> post = new HashMap<>();
        post.put("user", user);
        post.put("imageurl", path);
        post.put("timestamp", System.currentTimeMillis());

        database.collection("posts").document()
                .set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Succes", "DocumentSnapshot successfully written!");
                        Toast.makeText(ProfileActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed", "Error writing document", e);
                        Toast.makeText(ProfileActivity.this, "Uploaded Failed", Toast.LENGTH_SHORT).show();
                    }
                });


    }*/

    public void getPhoto(){
       /* Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }

    public void addContent(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else {
                getPhoto();
            }
        }else {
            getPhoto();
        }
    }

    public void mainpage(View view) {
        onBackPressed();
    }
}
