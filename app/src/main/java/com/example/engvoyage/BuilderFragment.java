package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BuilderFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private List<Word> wordList;

    public BuilderFragment() {
        // Required empty public constructor
    }

    public static BuilderFragment newInstance(String param1, String param2) {
        BuilderFragment fragment = new BuilderFragment();
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
        db = FirebaseFirestore.getInstance();
        wordList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_builder, container, false);
        readWords();
        openEasyWords(view);
        //remove user learned words
        return view;
    }

    public void openEasyWords(View view) {
        Button easyBtn = (Button) view.findViewById(R.id.easyLevel);
        easyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = WordFragment.newInstance(wordList);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_word");
                transaction.commit();
            }
        });
    }

    public void openIntermediateWords(View view) {
        Button intermediateBtn = (Button) view.findViewById(R.id.intermediateLevel);
    }

    public void openAdvancedWords(View view) {
        Button advancedBtn = (Button) view.findViewById(R.id.advancedLevel);
    }

    public void readWords() {
        db.collection("words")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("BuilderFragment", "Success");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buildWordList(document);
                        }
                    } else {
                        Log.d("BuilderFragment", "Error", task.getException());
                    }
                });
    }

    private void buildWordList(QueryDocumentSnapshot document) {
        String word = document.getString("word");
        String pronun = document.getString("pronunciation");
        String meanSh = document.getString("meaningShort");
        String meanFl = document.getString("meaningFull");
        String usage = document.getString("usage");
        String diff = document.getString("difficulty");

        Word wordInfo = new Word(word, pronun, meanSh, meanFl, usage, diff);
        wordList.add(wordInfo);
    }

    public void createBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("wordList", new ArrayList<>(wordList));
    }
}