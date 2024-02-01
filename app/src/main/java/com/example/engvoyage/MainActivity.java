package com.example.engvoyage;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private Button openRegister;
    private DocumentReference docRefUser;
    private User userInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //need loading animation
        mAuth = FirebaseAuth.getInstance();
        openRegister = findViewById(R.id.registerBtn);
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        openRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterPage.class);
                startActivity(intent);
            }
        });
    }

    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            docRefUser = db.collection("users").document(uid);
            getUserDetails();
        }
    }

    public void logInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            docRefUser = db.collection("users").document(uid);
                            getUserDetails();
                            Toast.makeText(MainActivity.this, "Authentication complete", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w("MainActivity", "logInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void openApp() {
        Intent intent = new Intent(MainActivity.this, Navigation.class);
        intent.putExtra("user", userInformation);
        startActivity(intent);
    }

    private void getUserDetails() {
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userInformation = document.toObject(User.class);
                        openApp();
                    }
                }
            }
        });
    }

    public void logInClicked(View view) {
        TextInputEditText emailValue = findViewById(R.id.emailInput);
        TextInputEditText passwordValue = findViewById(R.id.passwordInput);

        String email = emailValue.getText().toString().trim();
        String password = passwordValue.getText().toString().trim();

        logInUser(email, password);
    }
}