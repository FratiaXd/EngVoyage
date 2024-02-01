package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class WordInfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private Word wordInfo;

    public WordInfoFragment() {
        // Required empty public constructor
    }

    public static WordInfoFragment newInstance(Word word) {
        WordInfoFragment fragment = new WordInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, word);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            wordInfo = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_info, container, false);
        setWordInfo(view);
        closeInfo(view);
        return view;
    }

    public void setWordInfo(View view) {
        TextView wordTxt = (TextView) view.findViewById(R.id.wordInfo);
        TextView shortTxt = (TextView) view.findViewById(R.id.wordMeaningShortInfo);
        TextView pronunciationTxt = (TextView) view.findViewById(R.id.pronunciationInfo);
        TextView fullTxt = (TextView) view.findViewById(R.id.meaningFullInfo);
        TextView usageTxt = (TextView) view.findViewById(R.id.usageInfo);
        wordTxt.setText(wordInfo.getWord());
        shortTxt.setText(wordInfo.getMeaningShort());
        pronunciationTxt.setText(wordInfo.getPronunciation());
        fullTxt.setText(wordInfo.getMeaningFull());
        usageTxt.setText(wordInfo.getUsage());
    }

    public void closeInfo(View view) {
        ImageButton goBack1 = (ImageButton) view.findViewById(R.id.goBackToWords);
        Button goBack2 = (Button) view.findViewById(R.id.backToWords);

        goBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        goBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
    }
}