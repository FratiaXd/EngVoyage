package com.example.engvoyage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements CourseProgressAdapter.ItemClickListener{

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference docRefUser;
    private User user;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private List<UserCourses> courseListProgress;
    private RecyclerView recyclerView;
    private CourseProgressAdapter courseProgressAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        docRefUser = db.collection("users").document(uid);
        courseListProgress = new ArrayList<>();
        courseProgressAdapter = new CourseProgressAdapter(courseListProgress, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        getUserDetails(view);
        updateUserDetails(view);
        initRecyclerView(view);
        readUserCourses(view);
        logout(view);
        return view;
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewCompleted);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(courseProgressAdapter);
    }

    private void readUserCourses(View view) {
        TextView msg = (TextView) view.findViewById(R.id.noCoursesTxt);
        docRefUser.collection("userCourses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buildListProgressData(document);
                        }
                        courseProgressAdapter.notifyDataSetChanged();
                        msg.setVisibility(View.INVISIBLE);
                    } else {
                        Log.d("HomeFragment", "Error", task.getException());
                    }
                });
        if (courseListProgress.isEmpty()) {
            msg.setVisibility(View.VISIBLE);
        }
    }

    private void buildListProgressData(QueryDocumentSnapshot document) {
        String name = document.getString("courseName");
        String progress = document.getString("courseProgress");
        String duration = document.getString("courseDuration");
        int progressInt = Integer.parseInt(progress);
        int durationInt = Integer.parseInt(duration);

        if (progressInt >= durationInt) {
            UserCourses userCourses = new UserCourses(name, progress, duration);
            courseListProgress.add(userCourses);
        }
    }

    public void updateUserDetails(View view) {
        Button updateDetails = (Button) view.findViewById(R.id.editProfile);
        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = EditProfileFragment.newInstance(user);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_edit_profile");
                transaction.commit();
            }
        });
    }

    private void getUserDetails(View view) {
        TextView username = (TextView) view.findViewById(R.id.username);
        TextView email = (TextView) view.findViewById(R.id.email);
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.toObject(User.class);
                        username.setText(user.getName() + " " + user.getSurname());
                        email.setText(user.getEmail());
                    }
                }
            }
        });
    }

    private void logout(View view) {
        Button logoutBtn = (Button) view.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(UserCourses userCourses) {

    }
}