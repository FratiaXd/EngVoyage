package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FlashcardFragment extends Fragment {

    private static final String ARG_PARAM1 = "wordList";

    private List<Word> receivedWordList;
    private TextView meaning;
    private Button nextFlashcard;
    private Button reveal;
    private TextView word;
    private TextView remember;

    public FlashcardFragment() {
        // Required empty public constructor
    }

    public static FlashcardFragment newInstance(List<Word> wordList) {
        FlashcardFragment fragment = new FlashcardFragment();
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
                Log.d("VocabularyFragment", "Received Word List Size: " + receivedWordList.size());
            } else {
                Log.e("VocabularyFragment", "Received Word List is null");
            }
        } else {
            Log.e("VocabularyFragment", "Arguments are null");
        }
        Collections.shuffle(receivedWordList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flashcard, container, false);
        remember = (TextView) view.findViewById(R.id.remember);
        returnToBuilder(view);
        setFlashcard(view);
        onRevealClicked(view);
        openNextFlashcard();
        return view;
    }

    public void setFlashcard(View view) {
        word = (TextView) view.findViewById(R.id.wordFlashcard);
        meaning = (TextView) view.findViewById(R.id.wordFlashcardMeaning);

        word.setText(receivedWordList.get(0).getWord());
        meaning.setText(receivedWordList.get(0).getMeaningShort());
    }

    public void onRevealClicked(View view) {
        reveal = (Button) view.findViewById(R.id.revealFlashcard);
        nextFlashcard = (Button) view.findViewById(R.id.nextFlashcard);
        reveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meaning.setVisibility(View.VISIBLE);
                reveal.setVisibility(View.INVISIBLE);
                nextFlashcard.setVisibility(View.VISIBLE);
                removeUsedWord();
            }
        });
    }

    public void removeUsedWord() {
        receivedWordList.remove(0);
    }

    public void openNextFlashcard() {
        nextFlashcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainingCompleted();
            }
        });
    }

    public void trainingCompleted() {
        if (receivedWordList.isEmpty()) {
            word.setText("You completed all available flashcards!");
            remember.setVisibility(View.INVISIBLE);
            meaning.setVisibility(View.INVISIBLE);
            reveal.setVisibility(View.INVISIBLE);
            nextFlashcard.setVisibility(View.INVISIBLE);
        } else {
            Fragment fragment = FlashcardFragment.newInstance(receivedWordList);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, fragment, "fragment_flashcard");
            transaction.commit();
        }
    }

    private void returnToBuilder(View view) {
        ImageButton back = (ImageButton) view.findViewById(R.id.goBackToBuilder2);
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