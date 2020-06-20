package com.example.projectaidm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailEditText;
    EditText passwordEditText;
    Button signInButton;
    TextView tvSignUp;
    FirebaseAuth myFirebaseAuth;
    private FirebaseAuth.AuthStateListener myAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myFirebaseAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signInButton = findViewById(R.id.signInButton);
        tvSignUp = findViewById(R.id.signUpTextView);

        myAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser myFirebaseUser = myFirebaseAuth.getCurrentUser();
                if (myFirebaseUser != null) {
                    Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent intentHome = new Intent(LoginActivity.this, HomeActivity.class);
                    intentHome.putExtra("userEmail", myFirebaseUser.getEmail());
                    startActivity(intentHome);
                } else {
                    Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Filds Are Empty!", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    emailEditText.setError("Please enter a Email");
                    emailEditText.requestFocus();
                } else if (password.isEmpty()) {
                    passwordEditText.setError("Please enter your password");
                    passwordEditText.requestFocus();
                } else if (!(email.isEmpty() && password.isEmpty())) {
                    myFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intentToHome = new Intent(LoginActivity.this, HomeActivity.class);
                                String username = getUsername(email);
                                System.out.println(username);
                                intentToHome.putExtra("username", username);
                                startActivity(intentToHome);
                            }
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "Error Ocurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignUp = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intentSignUp);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        myFirebaseAuth.addAuthStateListener(myAuthStateListener);
    }

    public String getUsername(String email) {
        String username = "";
        char[] usernameArray = email.toCharArray();
        for (int i = 0; i<usernameArray.length; i++){
            if (usernameArray[i] == '@'){
                break;
            }
            username = username+ usernameArray[i];
        }
        return username;
    }
}
