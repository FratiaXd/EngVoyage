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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CourseMaterialFragment extends Fragment {

    private static final String ARG_USER_COURSE = "userCourse";
    private static final String ARG_COURSE = "course";
    private static final String ARG_LESSON = "lesson";

    public UserCourses userCourseInfo;
    public Course currentCourse;
    public Lesson currentLesson;

    public CourseMaterialFragment() {
        // Required empty public constructor
    }

    public static CourseMaterialFragment newInstance(UserCourses userCourse, Course course, Lesson lesson) {
        CourseMaterialFragment fragment = new CourseMaterialFragment();
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
        String currentLesson1 = "lesson" + userCourseInfo.getCourseProgress();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_material, container, false);
        displayCourseMaterial(view);
        goToPractice(view);
        closeMaterial(view);
        return view;
    }

    public void displayCourseMaterial(View view) {
        TextView courseTitle = (TextView) view.findViewById(R.id.courseTitle);
        TextView courseNumber = (TextView) view.findViewById(R.id.courseNumber);
        TextView courseMaterial = (TextView) view.findViewById(R.id.courseMaterial);
        String showProgress = "Lesson " + userCourseInfo.getCourseProgress() + "/" + currentCourse.getCourseDuration();
        courseTitle.setText(userCourseInfo.getCourseName());
        courseNumber.setText(showProgress);
        courseMaterial.setText(currentLesson.getMaterial());
    }

    public void goToPractice(View view) {
        Button practiceBtn = (Button) view.findViewById(R.id.openPractice);
        practiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = CoursePracticeFragment.newInstance(currentLesson, currentCourse, userCourseInfo);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_course_practice");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    public void closeMaterial(View view) {
        ImageButton close = (ImageButton) view.findViewById(R.id.closeMaterial);
        String fragmentTagToRemoveUpTo = "HomeFragmentTag";
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack(fragmentTagToRemoveUpTo, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }
}