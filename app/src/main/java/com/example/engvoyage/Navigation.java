package com.example.engvoyage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.engvoyage.databinding.ActivityMainBinding;
import com.example.engvoyage.databinding.ActivityNavigationBinding;

import java.util.ArrayList;
import java.util.List;

public class Navigation extends AppCompatActivity implements EditProfileFragment.OnProfileUpdatedListener{

    private User receivedUser;
    private List<Course> receivedCourses;
    ActivityNavigationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        receivedCourses = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            receivedUser = (User) intent.getParcelableExtra("user");
            receivedCourses = intent.getParcelableArrayListExtra("courseList");
        }

        setContentView(binding.getRoot());
        replaceFragment(HomeFragment.newInstance(receivedUser, receivedCourses));

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id == R.id.home) {
                clearBackStack();
                replaceFragment(HomeFragment.newInstance(receivedUser, receivedCourses));
            } else if (id == R.id.vocBuilder) {
                clearBackStack();
                replaceFragment(new BuilderFragment());
            } else if (id == R.id.profile) {
                clearBackStack();
                replaceFragment(ProfileFragment.newInstance(receivedUser));
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override
    public void onProfileUpdated(User updatedUser) {
        // Update the receivedUser when the profile is updated
        receivedUser = updatedUser;
    }
}