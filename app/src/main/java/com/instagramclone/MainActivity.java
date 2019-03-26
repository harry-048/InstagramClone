package com.instagramclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener{

    Boolean signupMode = true;
    TextView loginTextView;
    EditText userNameEdittext;
    EditText passwordEdittext;
    EditText userIdEdittext;
    EditText confirmPasswordEditText;
    Button addUsername;
    Button signUpButton;
    private FirebaseAuth mAuth;
   // CollectionReference users = .collection("users");
   FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginTextView = (TextView) findViewById(R.id.loginTextview);
        userNameEdittext = (EditText) findViewById(R.id.userNameEditText);
        passwordEdittext = (EditText) findViewById(R.id.passwordEditText);
        userIdEdittext = (EditText) findViewById(R.id.userIdEdittext);
        confirmPasswordEditText = (EditText) findViewById(R.id.confirmPasswordEditText);
        addUsername = (Button) findViewById(R.id.addusernameButton);
        signUpButton = findViewById(R.id.signUpButton);
        mAuth = FirebaseAuth.getInstance();

        ImageView logoImageView = findViewById(R.id.logoImageView);
        RelativeLayout backgroundLayout = findViewById(R.id.backgroundLayout);


       // logoImageView.setOnClickListener(this);
        //backgroundLayout.setOnClickListener(this);


        passwordEdittext.setOnKeyListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null)
            showUserList();
       // updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        userNameEdittext.setVisibility(View.INVISIBLE);
        passwordEdittext.setVisibility(View.INVISIBLE);
        confirmPasswordEditText.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.INVISIBLE);
        loginTextView.setVisibility(View.INVISIBLE);

        userIdEdittext.setVisibility(View.VISIBLE);
        addUsername.setVisibility(View.VISIBLE);




        if(currentUser!=null){

            final String Uid = currentUser.getUid();
            final Map<String, Object> user = new HashMap<>();

            addUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String userId = userIdEdittext.getText().toString();
                    if (userId.isEmpty()){
                        Toast.makeText(MainActivity.this, "Please Enter Username!", Toast.LENGTH_SHORT).show();
                      //  userIdEdittext.setText("hha");
                        userIdEdittext.requestFocus();
                    }
                    else {
                        user.put("UID", Uid);
                        user.put("username", userId);
                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            boolean f = true;
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("Success", document.getId() + " => " + document.getData()+"=>"+document.getData().get("username"));
                                                if(userId.equals(document.getData().get("username"))){
                                                    f =false;
                                                    Toast.makeText(MainActivity.this, "Username Already Exists!", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {

                                                }
                                            }
                                            if (f){
                                                // Add a new document with a generated ID
                                                db.collection("users")
                                                        .add(user)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Toast.makeText(MainActivity.this, "Username successfully added!", Toast.LENGTH_SHORT).show();
                                                                Log.d("Success", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                                showUserList();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(MainActivity.this, "Username adding failed!", Toast.LENGTH_SHORT).show();
                                                                Log.w("Failed", "Error adding document", e);
                                                            }
                                                        });
                                            }
                                        } else {
                                            Toast.makeText(MainActivity.this, "Username fetching failed!", Toast.LENGTH_SHORT).show();
                                            Log.w("Failed", "Error getting documents.", task.getException());
                                        }
                                    }
                                });
                    }
                }
            });




        }

    }


    public void showUserList(){
        Intent intent = new Intent(getApplicationContext(),UserListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
            signUpClick(v);
        }
        return false;
    }

    public void signUpClick(View view) {

        Log.d("signup","logged in ");
        String userName = userNameEdittext.getText().toString();
        String password = passwordEdittext.getText().toString();

        String confirmPassword = confirmPasswordEditText.getText().toString();
        try{
            if (userName.isEmpty()){
                Toast.makeText(getBaseContext(),"Email Id is required.",Toast.LENGTH_SHORT).show();
                userNameEdittext.requestFocus();
            }
            else if(password.isEmpty()){
                Toast.makeText(getBaseContext(),"Please enter password.",Toast.LENGTH_SHORT).show();
                passwordEdittext.requestFocus();
            }
            else if(confirmPassword.isEmpty() || !confirmPassword.equals(password)){
                Toast.makeText(getBaseContext(),"Please confirm password.",Toast.LENGTH_SHORT).show();
                passwordEdittext.requestFocus();
            }
            else {
                if (signupMode){
                    mAuth.createUserWithEmailAndPassword(userName, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Success", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.d("Failed", "createUserWithEmail:failure", task.getException());
                                        try{
                                            throw task.getException();
                                        }catch(FirebaseAuthWeakPasswordException e) {
                                            Toast.makeText(MainActivity.this, "Weak Password.",
                                                    Toast.LENGTH_SHORT).show();
                                           //  passwordEdittext.setSelection(passwordEdittext.length());
                                            passwordEdittext.requestFocus();
                                        } catch(FirebaseAuthInvalidCredentialsException e) {
                                            Toast.makeText(MainActivity.this, "Invalid Email.",
                                                    Toast.LENGTH_SHORT).show();
                                            userNameEdittext.requestFocus();
                                        } catch(FirebaseAuthUserCollisionException e) {
                                            Toast.makeText(MainActivity.this, "User already Exists.",
                                                    Toast.LENGTH_SHORT).show();
                                            userNameEdittext.requestFocus();
                                        } catch(Exception e) {
                                            Toast.makeText(MainActivity.this, "Sign up Authentication failed. "+task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                        }


                                        //updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }else {
                    mAuth.signInWithEmailAndPassword(userName, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Sucess", "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Failed", "signInWithEmail:failure", task.getException());
                                        Toast.makeText(MainActivity.this, "Login Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    public void login(View view) {
        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        if (signupMode){
            signupMode=false;
            signUpButton.setText("Login");
            loginTextView.setText("or, Sign Up");
        }
        else {
            signupMode=true;
            signUpButton.setText("Sign Up");
            loginTextView.setText("or, Login");
        }
        Log.d("Login","logged in ");
    }


}
