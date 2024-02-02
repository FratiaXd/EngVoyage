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

    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(courseAdapter);
    }

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

    @Override
    public void onItemClick(Course course) {
        if (isEnrolled(course.getCourseName())) {
            int progressInt = Integer.parseInt(selectedUserCourse.getCourseProgress());
            int durationInt = Integer.parseInt(selectedUserCourse.getCourseDuration());
            UserCourses tempUserCourse = new UserCourses(selectedUserCourse.getCourseName(),
                    selectedUserCourse.getCourseProgress(),
                    selectedUserCourse.getCourseDuration());
            if (progressInt > durationInt) {
                tempUserCourse.setCourseProgress("1");
                Task<Void> getLessonRestartTask = getLesson(tempUserCourse);
                getLessonRestartTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Fragment fragment = RestartCourseFragment.newInstance(tempUserCourse, course, lesson);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, fragment, "fragment_restart_course");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            } else {
                Task<Void> getLessonTask = getLesson(selectedUserCourse);
                getLessonTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Fragment fragment = CourseMaterialFragment.newInstance(selectedUserCourse, course, lesson);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            // Handle the failure scenario if needed
                            Log.e("OnItemClick", "Failed to get lesson", task.getException());
                        }
                    }
                });
            }
        } else {
            UserCourses courseDetail = new UserCourses(course.getCourseName(), "1", course.getCourseDuration());
            Task<Void> getLessonTask = getLesson(courseDetail);
            getLessonTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
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

    public boolean isEnrolled(String selectedCourse) {
        for (UserCourses course : userCourses) {
            if (course.getCourseName().equals(selectedCourse)) {
                selectedUserCourse = course;
                return true;
            }
        }
        return false;
    }

    public Task<Void> getLesson(UserCourses userCourseInfo) {
        String currentLesson = "lesson" + userCourseInfo.getCourseProgress();
        DocumentReference docRefCourse = db.collection("courses")
                .document(userCourseInfo.getCourseName())
                .collection("lessons")
                .document(currentLesson);

        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        docRefCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
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