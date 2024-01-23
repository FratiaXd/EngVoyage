package com.example.engvoyage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void register(User user1, String password) {
        mAuth.createUserWithEmailAndPassword(user1.getEmail(), password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("RegisterPage", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid(); //get user id?
                    //get database ref with user id (customer reg 5:10)
                    Toast.makeText(RegisterPage.this, "Account created successfully.", Toast.LENGTH_SHORT).show();

                    db.collection("users")
                            .document(uid)
                            .set(user1)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("RegisterPage", "DocumentSnapshot successfully written!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("RegisterPage", "Error writing document", e);
                                }
                            });
                    finish();
                } else {
                    Log.w("RegisterPage", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterPage.this, "Failed to create an account.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerButtonClicked(View view) {
        TextInputEditText nameValue = findViewById(R.id.nameInput);
        TextInputEditText surnameValue = findViewById(R.id.surnameInput);
        TextInputEditText emailValue = findViewById(R.id.emailInput);
        TextInputEditText passwordValue = findViewById(R.id.passwordInput);

        String name = nameValue.getText().toString().trim();
        String surname = surnameValue.getText().toString().trim();
        String email = emailValue.getText().toString().trim();
        String password = passwordValue.getText().toString().trim();

        User user1 = new User(name, surname, email);
        register(user1, password);
    }

    public void goBackToLogIn(View view) {
        finish();
    }
}