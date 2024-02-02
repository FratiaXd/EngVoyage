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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private DocumentReference docRefUser;
    private static final String ARG_USER_COURSE = "userCourse";
    private static final String ARG_COURSE = "course";
    private static final String ARG_LESSON = "lesson";
    public UserCourses userCourseInfo;
    public Course currentCourse;
    public Lesson currentLesson;

    public static CourseDetailFragment newInstance(UserCourses userCourse, Course course, Lesson lesson) {
        CourseDetailFragment fragment = new CourseDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER_COURSE, userCourse);
        args.putParcelable(ARG_COURSE, course);
        args.putParcelable(ARG_LESSON, lesson);
        fragment.setArguments(args);
        return fragment;
    }

    public CourseDetailFragment() {
        // Required empty public constructor
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
        db = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        docRefUser = db.collection("users").document(uid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_detail, container, false);

        TextView detailsName = view.findViewById(R.id.detailsCourseName);
        TextView detailsDur = view.findViewById(R.id.detailsCourseDur);
        TextView detailsDesc = view.findViewById(R.id.detailsCourseDesc);
        detailsName.setText(currentCourse.getCourseName());
        detailsDur.setText(currentCourse.getCourseDuration());
        detailsDesc.setText(currentCourse.getCourseDesc());

        enrollUser(view);
        returnToList(view);

        return view;
    }

    public void enrollUser(View view) {
        Button enrollBtn = (Button) view.findViewById(R.id.enrollUser);
        enrollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                docRefUser.collection("userCourses").document(currentCourse.getCourseName())
                        .set(userCourseInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("CourseDetailFragment", "User enrolled");
                                Fragment fragment = CourseMaterialFragment.newInstance(userCourseInfo, currentCourse, currentLesson);
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("CourseDetailFragment", "Enrollment error", e);
                            }
                        });
            }
        });
    }

    private void returnToList(View view) {
        ImageButton back = (ImageButton) view.findViewById(R.id.goBackToList);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}