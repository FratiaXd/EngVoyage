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
        //Initializes firebase elements
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    //Registers user
    public void register(User user1, String password) {
        //Creates user in firebase with the provided credentials
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
                    //If user created
                    //Saves user details in firestore document
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
                    //Once user created goes back to the main activity
                    finish();
                } else {
                    Log.w("RegisterPage", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(RegisterPage.this, "Failed to create an account.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Gets user input and then calls register()
    public void registerButtonClicked(View view) {
        TextInputEditText nameValue = findViewById(R.id.nameInput);
        TextInputEditText surnameValue = findViewById(R.id.surnameInput);
        TextInputEditText emailValue = findViewById(R.id.emailInput);
        TextInputEditText passwordValue = findViewById(R.id.passwordInput);
        TextInputEditText passwordValueRepeat = findViewById(R.id.repeatPasswordInput);

        String name = nameValue.getText().toString().trim();
        String surname = surnameValue.getText().toString().trim();
        String email = emailValue.getText().toString().trim();
        String password = passwordValue.getText().toString().trim();
        String passwordRepeat = passwordValueRepeat.getText().toString().trim();

        //Validates user input
        if (name.isEmpty()) {
            nameValue.setError("The name is required");
            nameValue.requestFocus();
            return;
        }
        if (surname.isEmpty()) {
            surnameValue.setError("The surname is required");
            surnameValue.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            emailValue.setError("The email is required");
            emailValue.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordValue.setError("The password is required");
            passwordValue.requestFocus();
            return;
        }
        if (passwordRepeat.isEmpty()) {
            passwordValueRepeat.setError("The password is required");
            passwordValueRepeat.requestFocus();
            return;
        }
        if (!password.equals(passwordRepeat)) {
            passwordValue.setError("Passwords do not match!");
            passwordValue.requestFocus();
            passwordValueRepeat.setError("Passwords do not match!");
            passwordValueRepeat.requestFocus();
            return;
        }
        //Creates user object instance
        User user1 = new User(name, surname, email);
        //Tries to register user with the provided credentials
        register(user1, password);
    }

    //Takes back to the main activity
    public void goBackToLogIn(View view) {
        finish();
    }
}