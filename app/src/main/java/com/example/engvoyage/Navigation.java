package com.example.engvoyage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.engvoyage.databinding.ActivityMainBinding;
import com.example.engvoyage.databinding.ActivityNavigationBinding;

public class Navigation extends AppCompatActivity {

    private User receivedUser;
    ActivityNavigationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            receivedUser = (User) intent.getParcelableExtra("user");
        }

        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if (id == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.vocBuilder) {
                replaceFragment(new BuilderFragment());
            } else if (id == R.id.profile) {
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
}