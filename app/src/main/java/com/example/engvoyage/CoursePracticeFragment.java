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
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class CoursePracticeFragment extends Fragment {

    private FirebaseFirestore db;
    private DocumentReference docRefUserCourse;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private Lesson lessonInfo;
    private Course courseInfo;
    private UserCourses userCoursesInfo;

    private Button answ1;
    private Button answ2;
    private Button nextBtn;
    private TextView result;

    public CoursePracticeFragment() {
        // Required empty public constructor
    }

    public static CoursePracticeFragment newInstance(Lesson lesson, Course course, UserCourses userCourse) {
        CoursePracticeFragment fragment = new CoursePracticeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, lesson);
        args.putParcelable(ARG_PARAM2, course);
        args.putParcelable(ARG_PARAM3, userCourse);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonInfo = getArguments().getParcelable(ARG_PARAM1);
            courseInfo = getArguments().getParcelable(ARG_PARAM2);
            userCoursesInfo = getArguments().getParcelable(ARG_PARAM3);
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
        View view = inflater.inflate(R.layout.fragment_course_practice, container, false);
        setPracticeInfo(view);
        openNext(view);
        receiveAnswer(view);
        return view;
    }

    public void setPracticeInfo(View view) {
        TextView name = (TextView) view.findViewById(R.id.courseTitle1);
        TextView number = (TextView) view.findViewById(R.id.coursePracticeNumber);
        TextView task = (TextView) view.findViewById(R.id.practiceTask);
        answ1 = (Button) view.findViewById(R.id.practiceAnswer1);
        answ2 = (Button) view.findViewById(R.id.practiceAnswer2);

        String showProgress = "Lesson " + userCoursesInfo.getCourseProgress() + "/"
                + courseInfo.getCourseDuration() + " - practice";

        name.setText(courseInfo.getCourseName());
        number.setText(showProgress);
        task.setText(lessonInfo.getPracticeTask());
        randomizeAnswers();
    }

    public void openNext(View view) {
        Button nextBtn = (Button) view.findViewById(R.id.nextLesson);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (courseInfo.getCourseDuration().equals(userCoursesInfo.getCourseProgress())) {
                    Fragment fragment = CourseCompletedFragment.newInstance("","");
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_course_completed");
                    transaction.commit();
                } else {
                    updateUserProgress();
                    Fragment fragment = CourseMaterialFragment.newInstance(userCoursesInfo, courseInfo);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                    transaction.commit();
                }
            }
        });
    }

    public void randomizeAnswers() {
        Random rand = new Random();
        int val = rand.nextInt(2);
        if (val == 1) {
            answ1.setText(lessonInfo.getPracticeTrue());
            answ2.setText(lessonInfo.getPracticeFalse());
        } else {
            answ1.setText(lessonInfo.getPracticeFalse());
            answ2.setText(lessonInfo.getPracticeTrue());
        }
    }

    public void receiveAnswer(View view) {
        result = (TextView) view.findViewById(R.id.result);
        nextBtn = (Button) view.findViewById(R.id.nextLesson);
        answ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePageOnAnswer(checkAnswer(answ1.getText().toString()));
            }
        });

        answ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePageOnAnswer(checkAnswer(answ2.getText().toString()));
            }
        });
    }

    public boolean checkAnswer(String answer) {
        if (answer == lessonInfo.getPracticeTrue()) {
            return true;
        } else {
            return false;
        }
    }

    public void updatePageOnAnswer(Boolean isCorrect) {
        if (isCorrect) {
            result.setText("Correct!");
            nextBtn.setVisibility(View.VISIBLE);
        } else {
            result.setText("Incorrect! Try again!");
        }
        result.setVisibility(View.VISIBLE);

        if (courseInfo.getCourseDuration().equals(userCoursesInfo.getCourseProgress())) {
            nextBtn.setText("COMPLETE COURSE");
        }
    }

    public void updateUserProgress() {
        Integer updProgressInt = Integer.parseInt(userCoursesInfo.getCourseProgress()) + 1;;
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
}