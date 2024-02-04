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
import com.google.android.gms.tasks.TaskCompletionSource;
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
    private Lesson currentLesson;

    public HomeFragment() {
        // Required empty public constructor
    }
    //New instance receives user details and course list
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
        //Initializes arguments and firebase elements
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

    //Initializes recyclerview holding course previews
    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewHorizontal);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(coursePreviewAdapter);
    }

    //Initializes recyclerview holding user course progress
    private void initRecyclerProgressView(View view) {
        recyclerViewProgress = view.findViewById(R.id.recyclerViewProgress);
        recyclerViewProgress.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProgress.setAdapter(courseProgressAdapter);
    }

    //Reads any user courses progress stored in the database
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
                        //If user havent started any courses yet display message
                        if (courseListProgress.isEmpty()) {
                            msg.setVisibility(View.VISIBLE);
                        } else {
                            msg.setVisibility(View.INVISIBLE);
                            TextView count = (TextView) view.findViewById(R.id.courseCount);
                            count.setText("Total: " + courseListProgress.size());
                        }
                    } else {
                        Log.d("HomeFragment", "Error", task.getException());
                    }
                });
    }

    //Builds list holding user course objects with user progress data
    private void buildListProgressData(QueryDocumentSnapshot document) {
        String name = document.getString("courseName");
        String progress = document.getString("courseProgress");
        String duration = document.getString("courseDuration");
        int progressInt = Integer.parseInt(progress);
        int durationInt = Integer.parseInt(duration);
        UserCourses userCourses = new UserCourses(name, progress, duration);

        //Divides courses that are in progress and that are already completed by user
        if (progressInt <= durationInt) {
            courseListProgress.add(userCourses);
            allUserCourses.add(userCourses);
        } else {
            allUserCourses.add(userCourses);
        }
    }

    //When show all button clicked opens course list fragment
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

    //Displays greeting message
    private void greetUser(View view) {
        TextView greeting = (TextView) view.findViewById(R.id.greeting);
        greeting.setText("Hi " + userCurrent.getName() + "!");
    }

    //Resumes clicked course in progress
    @Override
    public void onItemClick(UserCourses userCourses) {
        //Retrieves last saved lesson information
        Task<Void> getLessonRestartTask = retrieveLesson(userCourses);

        getLessonRestartTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                fragmentTag = "HomeFragmentTag";
                //Gets course information for the selected course by name
                Course selectedCourse = findSelectedCourseInfo(userCourses.getCourseName());
                //Opens course material fragment and passes all required data for it
                Fragment fragment = CourseMaterialFragment.newInstance(userCourses, selectedCourse, currentLesson);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                transaction.addToBackStack(fragmentTag);
                transaction.commit();
            }
        });
    }

    //Depending on the selected user course returns matching course and its information by name
    public Course findSelectedCourseInfo(String courseName) {
        for (Course course : availableCourses) {
            if (course.getCourseName().equals(courseName)) {
                return course;
            }
        }
        return null;
    }

    //Retrieves lesson information from the database for the selected course
    public Task<Void> retrieveLesson(UserCourses userCourseInfo) {
        //Depending on the user progress finds the last saved lesson
        String lessonToResume = "lesson" + userCourseInfo.getCourseProgress();
        DocumentReference docRefCourse = db.collection("courses")
                .document(userCourseInfo.getCourseName())
                .collection("lessons")
                .document(lessonToResume);

        //Awaits for completion
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        docRefCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Creates lesson object for the selected course
                        currentLesson = document.toObject(Lesson.class);
                        taskCompletionSource.setResult(null); // Complete the Task
                    } else {
                        taskCompletionSource.setException(new Exception("Document does not exist"));
                    }
                } else {
                    taskCompletionSource.setException(task.getException());
                }
            }
        });

        return taskCompletionSource.getTask();
    }
}