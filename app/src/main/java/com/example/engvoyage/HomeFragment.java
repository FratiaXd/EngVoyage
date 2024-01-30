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

/**
 * A simple {@link Fragment} subclass.
 * Use the {} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements CourseProgressAdapter.ItemClickListener{
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference docRefUser;
    private CoursePreviewAdapter coursePreviewAdapter;
    private List<Course> courseListPreview;
    private List<UserCourses> courseListProgress;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewProgress;
    private CourseProgressAdapter courseProgressAdapter;

    public HomeFragment() {
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
        courseListPreview = new ArrayList<>();
        courseListProgress = new ArrayList<>();
        coursePreviewAdapter = new CoursePreviewAdapter(courseListPreview);
        courseProgressAdapter = new CourseProgressAdapter(courseListProgress, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initRecyclerView(view);
        initRecyclerProgressView(view);
        openAllCourses(view);
        greetUser(view);
        logout(view);
        readCourses();
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

    private void readCourses() {
        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buildListData(document);
                        }
                        coursePreviewAdapter.notifyDataSetChanged();
                    } else {
                        Log.d("HomeFragment", "Error", task.getException());
                    }
                });
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
                        msg.setVisibility(View.INVISIBLE);
                    } else {
                        Log.d("HomeFragment", "Error", task.getException());
                    }
                });
        if (courseListProgress.isEmpty()) {
            msg.setVisibility(View.VISIBLE);
        }
    }

    private void buildListData(QueryDocumentSnapshot document) {
        String name = document.getString("courseName");
        String duration = document.getString("courseDuration");
        String desc = document.getString("courseDesc");

        Course course = new Course(name, duration, desc);
        courseListPreview.add(course);
    }

    private void buildListProgressData(QueryDocumentSnapshot document) {
        String name = document.getString("courseName");
        String progress = document.getString("courseProgress");
        String duration = document.getString("courseDuration");

        if (!progress.equals(duration)) {
            UserCourses userCourses = new UserCourses(name, progress, duration);
            courseListProgress.add(userCourses);
        }
    }

    public void openAllCourses(View view) {
        TextView showCourses = (TextView) view.findViewById(R.id.showall);
        showCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, new CourseListFragment());
                fr.commit();
            }
        });
    }

    private void greetUser(View view) {
        TextView greeting = (TextView) view.findViewById(R.id.greeting);
        docRefUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        User user = document.toObject(User.class);
                        greeting.setText("Hi " + user.getName() + "!");
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
        Course selectedCourse = findSelectedCourseInfo(userCourses.getCourseName());
        Fragment fragment = CourseMaterialFragment.newInstance(userCourses, selectedCourse);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
        transaction.commit();
    }

    public Course findSelectedCourseInfo(String courseName) {
        for (Course course : courseListPreview) {
            if (course.getCourseName().equals(courseName)) {
                return course;
            }
        }
        return null;
    }
}