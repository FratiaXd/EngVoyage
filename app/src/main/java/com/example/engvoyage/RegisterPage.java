package com.example.engvoyage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterPage extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
    }

    public void register(String name, String surname, String email, String password) {

    }

    public void registerButtonClicked(View view) {

    }

    public void goBackToLogIn(View view) {
        finish();
    }
}