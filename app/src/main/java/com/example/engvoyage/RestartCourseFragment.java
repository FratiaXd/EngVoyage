package com.example.engvoyage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Course courseInfo;
    private UserCourses userCoursesInfo;

    public RestartCourseFragment() {
        // Required empty public constructor
    }

    public static RestartCourseFragment newInstance(Course course, UserCourses userCourse) {
        RestartCourseFragment fragment = new RestartCourseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, course);
        args.putParcelable(ARG_PARAM2, userCourse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseInfo = getArguments().getParcelable(ARG_PARAM1);
            userCoursesInfo = getArguments().getParcelable(ARG_PARAM2);
        }
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        db = FirebaseFirestore.getInstance();
        docRefUserCourse = db.collection("users")
                .document(uid)
                .collection("userCourses")
                .document(courseInfo.getCourseName());
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
                Fragment fragment = CourseMaterialFragment.newInstance(userCoursesInfo, courseInfo);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
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
        userCoursesInfo.setCourseProgress(updProgress);
    }

    public void closeRestart(View view) {
        ImageButton close = (ImageButton) view.findViewById(R.id.closeRestart);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, new CourseListFragment());
                fr.commit();
            }
        });
    }
}