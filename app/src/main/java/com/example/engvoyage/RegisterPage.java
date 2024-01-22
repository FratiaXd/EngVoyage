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

    public void register(String name, String surname, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("RegisterPage", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(RegisterPage.this, "Authentication success.", Toast.LENGTH_SHORT).show();

                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("name", name);
                    userInfo.put("surname", surname);
                    userInfo.put("email", email);
                    db.collection("users")
                            .add(userInfo)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("RegisterPage", "DocumentSnapshot successfully written!");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("RegisterPage", "Error writing document", e);
                                }
                            });
                } else {
                    Log.w("RegisterPage", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterPage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void registerButtonClicked(View view) {
        TextInputEditText nameValue = findViewById(R.id.nameInput);
        TextInputEditText surnameValue = findViewById(R.id.surnameInput);
        TextInputEditText emailValue = findViewById(R.id.emailInput);
        TextInputEditText passwordValue = findViewById(R.id.passwordInput);

        String name = nameValue.getText().toString();
        String surname = surnameValue.getText().toString();
        String email = emailValue.getText().toString();
        String password = passwordValue.getText().toString();

        register(name, surname, email, password);
    }

    public void goBackToLogIn(View view) {
        finish();
    }
}