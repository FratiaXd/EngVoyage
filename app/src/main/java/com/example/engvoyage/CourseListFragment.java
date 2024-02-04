package com.example.engvoyage;

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
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class CourseListFragment extends Fragment implements CourseAdapter.ItemClickListener{
    private FirebaseFirestore db;
    private CourseAdapter courseAdapter;
    private UserCourses selectedUserCourse;
    private RecyclerView recyclerView;

    private static final String ARG_USER = "user";
    private static final String ARG_COURSES = "courseList";
    private static final String ARG_USER_COURSES = "userCourseList";
    private User userCurrent;
    private List<Course> availableCourses;
    private List<UserCourses> userCourses;
    private Lesson lesson;

    public CourseListFragment() {
        // Required empty public constructor
    }

    //New instance receives user details
    //Lists containing all courses and the courses user enrolled to
    public static CourseListFragment newInstance(User currentuser, List<Course> coursesList, List<UserCourses> coursesUser) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, currentuser);
        args.putParcelableArrayList(ARG_COURSES, new ArrayList<>(coursesList));
        args.putParcelableArrayList(ARG_USER_COURSES, new ArrayList<>(coursesUser));
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
            userCourses = getArguments().getParcelableArrayList(ARG_USER_COURSES);
        }
        db = FirebaseFirestore.getInstance();
        courseAdapter = new CourseAdapter(availableCourses, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        initRecyclerView(view);
        returnHome(view);

        return view;
    }

    //Initializes recyclerview with a list of available courses
    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(courseAdapter);
    }

    //Goes back to the home fragment
    private void returnHome(View view) {
        ImageButton back = (ImageButton) view.findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, HomeFragment.newInstance(userCurrent, availableCourses));
                fr.commit();
            }
        });
    }

    //Takes user to other fragment depending on the selected course progress
    @Override
    public void onItemClick(Course course) {
        //If user is enrolled to the selected course
        if (isEnrolled(course.getCourseName())) {
            //Gets user progress and overall course duration for that course
            int progressInt = Integer.parseInt(selectedUserCourse.getCourseProgress());
            int durationInt = Integer.parseInt(selectedUserCourse.getCourseDuration());
            //Temp object
            UserCourses tempUserCourse = new UserCourses(selectedUserCourse.getCourseName(),
                    selectedUserCourse.getCourseProgress(),
                    selectedUserCourse.getCourseDuration());
            //If user progress for this course is more than duration this course was completed
            if (progressInt > durationInt) {
                //Resets temp progress to 1
                tempUserCourse.setCourseProgress("1");
                //Retrieves lesson1 for temp object
                Task<Void> getLessonRestartTask = getLesson(tempUserCourse);
                getLessonRestartTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Opens restart course fragment offering user to restart previously completed course
                        //Passes first lesson for the selected course if the user decides to restart
                        Fragment fragment = RestartCourseFragment.newInstance(tempUserCourse, course, lesson);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, fragment, "fragment_restart_course");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            } else {
                //If the user is enrolled to the selected course but his progress is less than total course duration
                //Retrieves lesson according to the user progress
                Task<Void> getLessonTask = getLesson(selectedUserCourse);
                getLessonTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Opens course material with retrieved lesson information
                            Fragment fragment = CourseMaterialFragment.newInstance(selectedUserCourse, course, lesson);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            // Handles the failure scenario if needed
                            Log.e("OnItemClick", "Failed to get lesson", task.getException());
                        }
                    }
                });
            }
        } else {
            //If user is not enrolled to the selected course
            //Creates new user course object
            UserCourses courseDetail = new UserCourses(course.getCourseName(), "1", course.getCourseDuration());
            //Retrieves the first lesson for the selected course
            Task<Void> getLessonTask = getLesson(courseDetail);
            getLessonTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //Opens enrollment and course detail page
                        Fragment fragment = CourseDetailFragment.newInstance(courseDetail, course, lesson);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, fragment, "fragment_course_detail");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else {
                        // Handle the failure scenario if needed
                        Log.e("OnItemClick", "Failed to get lesson", task.getException());
                    }
                }
            });
        }
    }

    //Checks if user is already enrolled to the selected course
    public boolean isEnrolled(String selectedCourse) {
        for (UserCourses course : userCourses) {
            //If same course exists in user courses list returns true
            if (course.getCourseName().equals(selectedCourse)) {
                selectedUserCourse = course;
                return true;
            }
        }
        return false;
    }

    //Retrieves lesson information from the database for the selected course
    public Task<Void> getLesson(UserCourses userCourseInfo) {
        //Depending on the user progress finds the last saved lesson
        String currentLesson = "lesson" + userCourseInfo.getCourseProgress();
        DocumentReference docRefCourse = db.collection("courses")
                .document(userCourseInfo.getCourseName())
                .collection("lessons")
                .document(currentLesson);

        //Awaits for completion
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        docRefCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Creates lesson object for the selected course
                        lesson = document.toObject(Lesson.class);
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