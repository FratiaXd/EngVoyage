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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

    private Lesson newLesson;

    private Button answ1;
    private Button answ2;
    private Button nextBtn;
    private TextView result;

    public CoursePracticeFragment() {
        // Required empty public constructor
    }

    //New instance receives course and lesson details
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
        //Initializes arguments
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
        //Retrieves information for the following lesson
        getLesson(userCoursesInfo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_practice, container, false);
        setPracticeInfo(view);
        openNext(view);
        receiveAnswer(view);
        closePractice(view);
        return view;
    }

    //Displays received lesson information to the user
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
        //Randomizes the order of answers
        randomizeAnswers();
    }

    //Takes user to the next fragment
    public void openNext(View view) {
        Button nextBtn = (Button) view.findViewById(R.id.nextLesson);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If user progress is equal to the total course duration
                //Course is completed
                if (courseInfo.getCourseDuration().equals(userCoursesInfo.getCourseProgress())) {
                    //Updates progress
                    updateUserProgress();
                    //Opens course completed fragment
                    Fragment fragment = new CourseCompletedFragment();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_course_completed");
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    //If user progress is less than total course duration
                    //Updates progress
                    updateUserProgress();
                    //Opens next material fragment for the following lesson
                    Fragment fragment = CourseMaterialFragment.newInstance(userCoursesInfo, courseInfo, newLesson);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });
    }

    //Randomizes the order of correct and incorrect answers
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

    //Handles answer submission
    public void receiveAnswer(View view) {
        result = (TextView) view.findViewById(R.id.result);
        nextBtn = (Button) view.findViewById(R.id.nextLesson);
        answ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Displays response
                updatePageOnAnswer(checkAnswer(answ1.getText().toString()));
            }
        });

        answ2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Displays response
                updatePageOnAnswer(checkAnswer(answ2.getText().toString()));
            }
        });
    }

    //Check if the selected answer is correct
    public boolean checkAnswer(String answer) {
        if (answer == lessonInfo.getPracticeTrue()) {
            return true;
        } else {
            return false;
        }
    }

    //Updates page according to the answer
    public void updatePageOnAnswer(Boolean isCorrect) {
        if (isCorrect) {
            //If correct answer
            //Display button to go to the next fragment
            result.setText("Correct!");
            nextBtn.setVisibility(View.VISIBLE);
        } else {
            //If incorrect prompts to try again
            result.setText("Incorrect! Try again!");
        }
        result.setVisibility(View.VISIBLE);

        //Updates button if there are no further lessons
        if (courseInfo.getCourseDuration().equals(userCoursesInfo.getCourseProgress())) {
            nextBtn.setText("COMPLETE COURSE");
        }
    }

    //Updates user progress in the database
    public void updateUserProgress() {
        //Increases progress by 1
        Integer updProgressInt = Integer.parseInt(userCoursesInfo.getCourseProgress()) + 1;
        String updProgress = updProgressInt.toString();
        docRefUserCourse.update("courseProgress", updProgress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("CoursePracticeFragment", "User progress updated successfully");
                        //userCoursesInfo.setCourseProgress(updProgress);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("CoursePracticeFragment", "Could not update user progress", e);
                    }
                });
        //Updates user progress inside the object
        userCoursesInfo.setCourseProgress(updProgress);
    }

    //Closes fragment and opens home fragment
    public void closePractice(View view) {
        ImageButton close = (ImageButton) view.findViewById(R.id.closePractice);
        String fragmentTagToRemoveUpTo = "HomeFragmentTag";
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack(fragmentTagToRemoveUpTo, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }

    //Retrieves information from the database for the next lesson
    public void getLesson(UserCourses userCourseInfo) {
        //Increases progress to find the next lesson
        Integer updProgressInt = Integer.parseInt(userCoursesInfo.getCourseProgress()) + 1;
        String updProgress = "lesson" + updProgressInt.toString();
        db = FirebaseFirestore.getInstance();
        DocumentReference docRefCourse = db.collection("courses")
                .document(userCourseInfo.getCourseName())
                .collection("lessons")
                .document(updProgress);

        docRefCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Creates new object holding the following lesson details
                        newLesson = document.toObject(Lesson.class);
                    }
                }
            }
        });
    }
}