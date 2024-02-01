package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WordFragment extends Fragment {

    private static final String ARG_PARAM1 = "wordList";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private List<Word> receivedWordList;

    public WordFragment() {
        // Required empty public constructor
    }

    public static WordFragment newInstance(List<Word> wordList) {
        WordFragment fragment = new WordFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, new ArrayList<>(wordList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            receivedWordList = args.getParcelableArrayList(ARG_PARAM1);
            if (receivedWordList != null) {
                Log.d("WordFragment", "Received Word List Size: " + receivedWordList.size());
            } else {
                Log.e("WordFragment", "Received Word List is null");
            }
        } else {
            Log.e("WordFragment", "Arguments are null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word, container, false);
        setWordInfo(view);
        returnToBuilder(view);
        return view;
    }

    public void setWordInfo(View view) {
        TextView wordTxt = (TextView) view.findViewById(R.id.word);
        TextView shortTxt = (TextView) view.findViewById(R.id.wordMeaningShort);
        TextView pronunciationTxt = (TextView) view.findViewById(R.id.pronunciation);
        TextView fullTxt = (TextView) view.findViewById(R.id.meaningFull);
        TextView usageTxt = (TextView) view.findViewById(R.id.usage);
        wordTxt.setText(receivedWordList.get(0).getWord());
        shortTxt.setText(receivedWordList.get(0).getMeaningShort());
        pronunciationTxt.setText(receivedWordList.get(0).getPronunciation());
        fullTxt.setText(receivedWordList.get(0).getMeaningFull());
        usageTxt.setText(receivedWordList.get(0).getUsage());
    }

    private void returnToBuilder(View view) {
        ImageButton back = (ImageButton) view.findViewById(R.id.goBackToBuilder);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, new BuilderFragment());
                fr.commit();
            }
        });
    }
}