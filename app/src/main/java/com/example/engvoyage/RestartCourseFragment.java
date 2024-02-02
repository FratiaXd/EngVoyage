package com.example.engvoyage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestartCourseFragment extends Fragment {

    private FirebaseFirestore db;
    private DocumentReference docRefUserCourse;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String ARG_USER_COURSE = "userCourse";
    private static final String ARG_COURSE = "course";
    private static final String ARG_LESSON = "lesson";
    public UserCourses userCourseInfo;
    public Course currentCourse;
    public Lesson currentLesson;

    private Course courseInfo;
    private UserCourses userCoursesInfo;

    public RestartCourseFragment() {
        // Required empty public constructor
    }

    public static RestartCourseFragment newInstance(UserCourses userCourse, Course course, Lesson lesson) {
        RestartCourseFragment fragment = new RestartCourseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER_COURSE, userCourse);
        args.putParcelable(ARG_COURSE, course);
        args.putParcelable(ARG_LESSON, lesson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userCourseInfo = getArguments().getParcelable(ARG_USER_COURSE);
            currentCourse = getArguments().getParcelable(ARG_COURSE);
            currentLesson = getArguments().getParcelable(ARG_LESSON);
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        db = FirebaseFirestore.getInstance();
        docRefUserCourse = db.collection("users")
                .document(uid)
                .collection("userCourses")
                .document(currentCourse.getCourseName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_restart_course, container, false);
        onClickRestart(view);
        closeRestart(view);
        return view;
    }

    public void onClickRestart(View view) {
        Button restartBtn = (Button) view.findViewById(R.id.startAgain);
        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartCourse();
                Fragment fragment = CourseMaterialFragment.newInstance(userCourseInfo, currentCourse, currentLesson);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    public void restartCourse() {
        Integer updProgressInt = 1;
        String updProgress = updProgressInt.toString();
        docRefUserCourse.update("courseProgress", updProgress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("CoursePracticeFragment", "User progress updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("CoursePracticeFragment", "Could not update user progress", e);
                    }
                });
        userCourseInfo.setCourseProgress(updProgress);
    }

    public void closeRestart(View view) {
        ImageButton close = (ImageButton) view.findViewById(R.id.closeRestart);
        String fragmentTagToRemoveUpTo = "HomeFragmentTag";
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack(fragmentTagToRemoveUpTo, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }
}