package com.example.engvoyage;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private Button openRegister;
    private DocumentReference docRefUser;
    private User userInformation;
    private List<Course> courseList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializes buttons and firebase elements
        mAuth = FirebaseAuth.getInstance();
        openRegister = findViewById(R.id.registerBtn);
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        courseList = new ArrayList<>();

        //Takes user to the registration activity
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
        //If user was logged in before
        if (currentUser != null) {
            String uid = currentUser.getUid();
            docRefUser = db.collection("users").document(uid);
            //Find user in the database
            getUserDetails();
        }
    }

    //Log in user
    public void logInUser(String email, String password) {
        //Firebase authentication
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //If user credentials are correct and exist in firebase
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            docRefUser = db.collection("users").document(uid);
                            //Reads user details into the user object
                            getUserDetails();
                            Toast.makeText(MainActivity.this, "Authentication complete", Toast.LENGTH_SHORT).show();
                        } else {
                            //Credentials are incorrect or such user doesnt exist
                            Log.w("MainActivity", "logInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //When user details and course list is retrieved opens navigation activity
    //Navigation activity contains all application fragments
    public void openApp() {
        Intent intent = new Intent(MainActivity.this, Navigation.class);
        //Retrieving user and course information before opening the navigation activity allows instant load of the content inside fragments
        intent.putExtra("user", userInformation);
        intent.putParcelableArrayListExtra("courseList", new ArrayList<>(courseList));
        startActivity(intent);
    }

    //Gets existing user details from the database and then calls readCourses()
    private void getUserDetails() {
        //Loading animation
        LinearLayout loading = findViewById(R.id.loadingScreen);
        loading.setVisibility(View.VISIBLE);
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Creates object instance holding user data
                        userInformation = document.toObject(User.class);
                        readCourses();
                    }
                }
            }
        });
    }

    //Reads all existing courses from the database and then calls openApp()
    private void readCourses() {
        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buildListData(document);
                        }
                        //If courses were retrevied opens application home page
                        openApp();
                    } else {
                        Log.d("MainActivity", "Error loading courses", task.getException());
                    }
                });
    }

    //Builds a list of Course objects holding information about each course
    private void buildListData(QueryDocumentSnapshot document) {
        String name = document.getString("courseName");
        String duration = document.getString("courseDuration");
        String desc = document.getString("courseDesc");
        String url = document.getString("coverImageUrl");

        Course course = new Course(name, duration, desc, url);
        courseList.add(course);
    }

    //Gets user input with credentials and then calls logInUser()
    public void logInClicked(View view) {
        TextInputEditText emailValue = findViewById(R.id.emailInput);
        TextInputEditText passwordValue = findViewById(R.id.passwordInput);

        String email = emailValue.getText().toString().trim();
        String password = passwordValue.getText().toString().trim();

        //Input validation
        if (email.isEmpty()) {
            emailValue.setError("The email is required");
            emailValue.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            passwordValue.setError("The email is required");
            passwordValue.requestFocus();
            return;
        }

        //Tries to log in user with provided credentials
        logInUser(email, password);
    }
}