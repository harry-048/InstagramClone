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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnKeyListener{

    Boolean signupMode = true;
    TextView loginTextView;
    EditText userNameEdittext;
    EditText passwordEdittext;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginTextView = (TextView) findViewById(R.id.loginTextview);
        userNameEdittext = (EditText) findViewById(R.id.userNameEditText);
        passwordEdittext = (EditText) findViewById(R.id.passwordEditText);
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
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null)
        showUserList();
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
        if (userName.isEmpty() || password.isEmpty()){
            Toast.makeText(getBaseContext(),"UserName or Password is missing",Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MainActivity.this, "Sign up Authentication failed. "+task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
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
