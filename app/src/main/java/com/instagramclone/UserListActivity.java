package com.instagramclone;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class UserListActivity extends AppCompatActivity {

    FirebaseFirestore database = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    private FirebaseAuth mAuth;
    private String user;
    private String username;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ImageView homeImageView;
    ImageView settingsImageView;





    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            final String uid = currentUser.getUid();
            Log.d("Auth uid",uid);
            user=currentUser.getEmail();
            database.collection("users")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot result = task.getResult();
                                if (result != null)
                                for (QueryDocumentSnapshot document : result) {
                                    Log.d("Success", document.getId() + " => " + document.getData());
                                    if (uid.equals(document.getData().get("UID"))){
                                        //Toast.makeText(UserListActivity.this, "uid: "+document.getData().get("UID"), Toast.LENGTH_SHORT).show();
                                        username=document.getData().get("username").toString();
                                        Log.d("Auth username",username);
                                    }
                                }
                                else{
                                    Log.d("uid","is null");
                                }
                            } else {
                                Log.w("Failed", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
      //  user=currentUser.getEmail();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        homeImageView= (ImageView) findViewById(R.id.homeImageView);
        settingsImageView = (ImageView) findViewById(R.id.settingsImageView);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.add_content:
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
                        break;
                    case R.id.settings:
                        Toast.makeText(UserListActivity.this, "settings", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        final ListView listView = findViewById(R.id.listView);
      /*  final ArrayList<String> usernames = new ArrayList<String>();
        final ArrayList<String> imageUrls = new ArrayList<String>();*/
        final ArrayList<ListFeed> feeds = new ArrayList<>();
       // final ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,usernames);

       /* final CustomAdapter arrayAdapter  = new CustomAdapter(this,feeds);
        listView.setAdapter(arrayAdapter);*/

        final CustomAdapterRecyclerView customAdapter = new CustomAdapterRecyclerView(feeds, this);
        recyclerView.setAdapter(customAdapter);

      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),UserFeedActivity.class);
                intent.putExtra("username",usernames.get(position));
                intent.putExtra("imageUrl",imageUrls.get(position));
                startActivity(intent);
            }
        });

        listView.setAdapter(arrayAdapter);
*/

        database.collection("posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                               /* Log.d("Succes", document.getId() + " => " + document.getData());
                                imageUrls.add(document.getData().get("imageurl").toString());
                                Object time = document.getData().get("timestamp");
                                if (time == null)
                                    time = System.currentTimeMillis() + "";
                                Date date = new Date();
                                date.setTime(Long.parseLong(time.toString()));
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss", Locale.getDefault());
                                usernames.add(
                                        document.getData().get("user") + " posted at " +
                                                sdf.format(date)
                                );
                                arrayAdapter.notifyDataSetChanged();*/

                              /* if (username.equals(document.getData().get("user"))){
                                   profileIntent.putExtra("image",document.getData().get("imageurl").toString());
                               }*/


                               feeds.add(new ListFeed(document.getData().get("user")+"",document.getData().get("imageurl")+"",document.getData().get("timestamp")+""));
                               customAdapter.notifyDataSetChanged();



                            }
                        } else {
                            Log.w("Failed", "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    @Override
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

    }

    private void savePost(String user, String path) {
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
                        Toast.makeText(UserListActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Failed", "Error writing document", e);
                        Toast.makeText(UserListActivity.this, "Uploaded Failed", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    public void getPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.share){
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
        }else if (item.getItemId()==R.id.logout){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            mAuth.signOut();
            finish();
        }

        /*if (Build.VERSION.SDK_INT < 23) {
            if (item.getItemId()==R.id.share){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else {
                    getPhoto();
                }
            }else if (item.getItemId()==R.id.logout){
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }
        else
        {
            if (item.getItemId()==R.id.share){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else {
                    getPhoto();
                }
            }else if (item.getItemId()==R.id.logout){
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        }*/

        return super.onOptionsItemSelected(item);
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

    public void logout(View view) {

        /*Intent profileIntent = new Intent(getApplicationContext(),ProfileActivity.class);
        profileIntent.putExtra("username",username);
        startActivity(profileIntent);*/

        homeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_button_inactive));
        settingsImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_active));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to Logout?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                mAuth.signOut();
                                finish();
                            }
                        });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                homeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_button_active));
                settingsImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_inactive));
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                homeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_home_button_active));
                settingsImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_user_inactive));
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
