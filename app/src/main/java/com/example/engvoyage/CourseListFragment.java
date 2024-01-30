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

public class CourseListFragment extends Fragment implements CourseAdapter.ItemClickListener{
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference docRefUser;
    private FirebaseFirestore db;
    private CourseAdapter courseAdapter;
    private UserCourses selectedUserCourse;
    private List<Course> courseList;
    private List<UserCourses> userCoursesList;
    private RecyclerView recyclerView;

    public CourseListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        docRefUser = db.collection("users").document(uid);
        userCoursesList = new ArrayList<>();
        courseList = new ArrayList<>();
        courseAdapter = new CourseAdapter(courseList, this);
        getUserEnrolledCourses();
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

    public void getUserEnrolledCourses() {
        docRefUser.collection("userCourses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buildCourseListData(document);
                        }
                    } else {
                        Log.d("CourseListFragment", "Error", task.getException());
                    }
                });
    }

    private void buildCourseListData(QueryDocumentSnapshot document) {
        String name = document.getString("courseName");
        String progress = document.getString("courseProgress");
        String duration = document.getString("courseDuration");

        UserCourses userCourses = new UserCourses(name, progress, duration);
        userCoursesList.add(userCourses);
    }

    @Override
    public void onItemClick(Course course) {
        if (isEnrolled(course.getCourseName())) {
            int progressInt = Integer.parseInt(selectedUserCourse.getCourseProgress());
            int durationInt = Integer.parseInt(selectedUserCourse.getCourseDuration());
            if (progressInt > durationInt) {
                Fragment fragment = RestartCourseFragment.newInstance(course, selectedUserCourse);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_restart_course");
                transaction.commit();
            } else {
                Fragment fragment = CourseMaterialFragment.newInstance(selectedUserCourse, course);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                transaction.commit();
            }
        } else {
            Fragment fragment = CourseDetailFragment.newInstance(course);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, fragment, "fragment_course_detail");
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public boolean isEnrolled(String selectedCourse) {
        for (UserCourses course : userCoursesList) {
            if (course.getCourseName().equals(selectedCourse)) {
                selectedUserCourse = course;
                return true;
            }
        }
        return false;
    }
}