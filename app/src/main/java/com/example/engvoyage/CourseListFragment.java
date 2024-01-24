package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CourseListFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference docRef;

    public View view;
    public ImageButton back;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private RecyclerView recyclerView;

    public CourseListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_course_list, container, false);
        back = (ImageButton) view.findViewById(R.id.goBack);
        recyclerView = view.findViewById(R.id.recyclerView); // Replace with your RecyclerView ID
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(courseAdapter);

        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("courseName");
                            String duration = document.getString("courseDuration");
                            String desc = document.getString("courseDesc");

                            Course course = new Course(name, duration, desc);
                            courseList.add(course);
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("CourseListFragment", "Error", task.getException());
                    }
                });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, new HomeFragment());
                fr.commit();
            }
        });
        return view;
    }
}