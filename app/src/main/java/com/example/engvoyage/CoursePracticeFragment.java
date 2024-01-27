package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CoursePracticeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private String mParam1;
    private String mParam2;
    private String mParam3;
    private String mParam4;
    private String mParam5;

    private Lesson lessonInfo;

    public CoursePracticeFragment() {
        // Required empty public constructor
    }

    public static CoursePracticeFragment newInstance(Lesson lesson) {
        CoursePracticeFragment fragment = new CoursePracticeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, lesson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lessonInfo = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_practice, container, false);
        setPracticeInfo(view);
        return view;
    }

    public void setPracticeInfo(View view) {
        TextView name = (TextView) view.findViewById(R.id.courseTitle1);
        name.setText(lessonInfo.getPracticeTask());
    }
}