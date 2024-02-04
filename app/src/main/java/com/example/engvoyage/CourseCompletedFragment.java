package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CourseCompletedFragment extends Fragment {

    public CourseCompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Course completed fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_completed, container, false);
        completeCourse(view);
        return view;
    }

    //Closes this fragment and takes user to the home fragment
    public void completeCourse(View view) {
        Button completeBtn = (Button) view.findViewById(R.id.finishCourse);
        String fragmentTagToRemoveUpTo = "HomeFragmentTag";
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack(fragmentTagToRemoveUpTo, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
    }
}