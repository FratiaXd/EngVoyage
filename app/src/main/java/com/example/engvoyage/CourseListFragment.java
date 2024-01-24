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
    private FirebaseFirestore db;
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
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        initRecyclerView(view);
        returnHome(view);
        readCourses();

        return view;
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(courseAdapter);
    }

    private void readCourses() {
        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buildListData(document);
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("CourseListFragment", "Error", task.getException());
                    }
                });
    }

    private void buildListData(QueryDocumentSnapshot document) {
        String name = document.getString("courseName");
        String duration = document.getString("courseDuration");
        String desc = document.getString("courseDesc");

        Course course = new Course(name, duration, desc);
        courseList.add(course);
    }

    private void returnHome(View view) {
        ImageButton back = (ImageButton) view.findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, new HomeFragment());
                fr.commit();
            }
        });
    }
}