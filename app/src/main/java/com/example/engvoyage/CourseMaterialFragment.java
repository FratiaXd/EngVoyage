package com.example.engvoyage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class CourseMaterialFragment extends Fragment {

    private FirebaseFirestore db;
    private DocumentReference docRefCourse;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public CourseMaterialFragment() {
        // Required empty public constructor
    }

    public static CourseMaterialFragment newInstance(String param1, String param2) {
        CourseMaterialFragment fragment = new CourseMaterialFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        String currentLesson = "lesson" + mParam2;
        db = FirebaseFirestore.getInstance();
        docRefCourse = db.collection("courses")
                .document(mParam1)
                .collection("lessons")
                .document(currentLesson);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_material, container, false);
        displayCourseMaterial(view);
        return view;
    }

    public void displayCourseMaterial(View view) {
        TextView courseTitle = (TextView) view.findViewById(R.id.courseTitle);
        TextView courseNumber = (TextView) view.findViewById(R.id.courseNumber);
        TextView courseMaterial = (TextView) view.findViewById(R.id.courseMaterial);
        docRefCourse.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Lesson lesson = document.toObject(Lesson.class);
                        courseTitle.setText(mParam1);
                        courseNumber.setText(mParam2);
                        courseMaterial.setText(lesson.getMaterial());
                    }
                }
            }
        });
    }

    public void goToPractice(View view) {
        Button practiceBtn = (Button) view.findViewById(R.id.openPractice);
        practiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}