package com.example.engvoyage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CourseProgressAdapter.ItemClickListener{
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference docRefUser;
    private CoursePreviewAdapter coursePreviewAdapter;
    private List<UserCourses> courseListProgress;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewProgress;
    private CourseProgressAdapter courseProgressAdapter;

    private static final String ARG_USER = "user";
    private static final String ARG_COURSES = "courseList";
    private User userCurrent;
    private List<Course> availableCourses;
    private List<UserCourses> allUserCourses;
    private String fragmentTag;

    public HomeFragment() {
        // Required empty public constructor
    }
    public static HomeFragment newInstance(User currentuser, List<Course> coursesList) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, currentuser);
        args.putParcelableArrayList(ARG_COURSES, new ArrayList<>(coursesList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userCurrent = getArguments().getParcelable(ARG_USER);
            availableCourses = getArguments().getParcelableArrayList(ARG_COURSES);
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        docRefUser = db.collection("users").document(uid);
        courseListProgress = new ArrayList<>();
        allUserCourses = new ArrayList<>();
        coursePreviewAdapter = new CoursePreviewAdapter(availableCourses);
        courseProgressAdapter = new CourseProgressAdapter(courseListProgress, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        courseListProgress.clear();
        allUserCourses.clear();
        initRecyclerView(view);
        initRecyclerProgressView(view);
        openAllCourses(view);
        greetUser(view);
        readUserCourses(view);
        return view;
    }

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewHorizontal);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(coursePreviewAdapter);
    }

    private void initRecyclerProgressView(View view) {
        recyclerViewProgress = view.findViewById(R.id.recyclerViewProgress);
        recyclerViewProgress.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProgress.setAdapter(courseProgressAdapter);
    }

    private void readUserCourses(View view) {
        TextView msg = (TextView) view.findViewById(R.id.noCourses);
        docRefUser.collection("userCourses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buildListProgressData(document);
                        }
                        courseProgressAdapter.notifyDataSetChanged();
                        if (courseListProgress.isEmpty()) {
                            msg.setVisibility(View.VISIBLE);
                        } else {
                            msg.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        Log.d("HomeFragment", "Error", task.getException());
                    }
                });
    }

    private void buildListProgressData(QueryDocumentSnapshot document) {
        String name = document.getString("courseName");
        String progress = document.getString("courseProgress");
        String duration = document.getString("courseDuration");
        int progressInt = Integer.parseInt(progress);
        int durationInt = Integer.parseInt(duration);
        UserCourses userCourses = new UserCourses(name, progress, duration);

        if (progressInt <= durationInt) {
            courseListProgress.add(userCourses);
            allUserCourses.add(userCourses);
        } else {
            allUserCourses.add(userCourses);
        }
    }

    public void openAllCourses(View view) {
        TextView showCourses = (TextView) view.findViewById(R.id.showall);
        showCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTag = "HomeFragmentTag";
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, CourseListFragment.newInstance(userCurrent, availableCourses, allUserCourses), fragmentTag);
                fr.addToBackStack(fragmentTag);
                fr.commit();
            }
        });
    }

    private void greetUser(View view) {
        TextView greeting = (TextView) view.findViewById(R.id.greeting);
        greeting.setText("Hi " + userCurrent.getName() + "!");
    }

    @Override
    public void onItemClick(UserCourses userCourses) {/*
        Course selectedCourse = findSelectedCourseInfo(userCourses.getCourseName());
        Fragment fragment = CourseMaterialFragment.newInstance(userCourses, selectedCourse);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
        transaction.commit();*/
    }

    public Course findSelectedCourseInfo(String courseName) {
        for (Course course : availableCourses) {
            if (course.getCourseName().equals(courseName)) {
                return course;
            }
        }
        return null;
    }
}