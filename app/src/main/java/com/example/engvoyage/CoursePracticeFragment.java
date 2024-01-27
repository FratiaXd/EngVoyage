package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CoursePracticeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private Lesson lessonInfo;
    private Course courseInfo;
    private UserCourses userCoursesInfo;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_practice, container, false);
        setPracticeInfo(view);
        openNext(view);
        return view;
    }

    public void setPracticeInfo(View view) {
        TextView name = (TextView) view.findViewById(R.id.courseTitle1);
        TextView number = (TextView) view.findViewById(R.id.coursePracticeNumber);
        TextView task = (TextView) view.findViewById(R.id.practiceTask);
        Button answ1 = (Button) view.findViewById(R.id.practiceAnswer1);
        Button answ2 = (Button) view.findViewById(R.id.practiceAnswer2);
        name.setText(courseInfo.getCourseName());
        number.setText(courseInfo.getCourseDuration());
        task.setText(lessonInfo.getPracticeTask());
        answ1.setText(lessonInfo.getPracticeTrue());
        answ2.setText(lessonInfo.getPracticeFalse());
    }

    public void openNext(View view) {
        Button answ1 = (Button) view.findViewById(R.id.practiceAnswer1);
        answ1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = CourseMaterialFragment.newInstance(userCoursesInfo, courseInfo);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_course_material");
                transaction.commit();
            }
        });
    }
}