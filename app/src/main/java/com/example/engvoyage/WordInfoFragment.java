package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

public class WordInfoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private Word wordInfo;
    private ImageButton play;
    private TextToSpeech t1;
    public WordInfoFragment() {
        // Required empty public constructor
    }

    //New instance receives user selected word
    public static WordInfoFragment newInstance(Word word) {
        WordInfoFragment fragment = new WordInfoFragment();
        //Initializes arguments
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
        //Plays word pronunciation
        t1 = new TextToSpeech(this.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_word_info, container, false);
        play = (ImageButton) view.findViewById(R.id.playText1);
        setWordInfo(view);
        closeInfo(view);
        //Plays word pronunciation
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String wordCurrent = wordInfo.getWord();
                t1.speak(wordCurrent, TextToSpeech.QUEUE_FLUSH, null, "word");
            }
        });
        return view;
    }

    //Displays received word object information
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

    //Closes word info fragment and opens builder fragment
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