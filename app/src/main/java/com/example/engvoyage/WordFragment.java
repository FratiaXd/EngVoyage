package com.example.engvoyage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WordFragment extends Fragment {

    private static final String ARG_PARAM1 = "wordList";

    private List<Word> receivedWordList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference docRefUser;
    private Button saveBtn;
    private Button nextWord;

    public WordFragment() {
        // Required empty public constructor
    }

    //New instance receives a word list of selected difficulty
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
        //Initializes arguments and firebase elements
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        docRefUser = db.collection("users").document(uid);
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
        onSaveClick(view);
        returnToBuilder(view);
        openNextWord(view);
        return view;
    }

    //Displays first word information from the list
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

    //Calls addWordToUserWords() when user clicks save button
    public void onSaveClick(View view) {
        saveBtn = (Button) view.findViewById(R.id.saveWord);
        nextWord = (Button) view.findViewById(R.id.proceed);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Word addWord = receivedWordList.get(0);
                addWordToUserWords(addWord);
            }
        });
    }

    //Updates user saved words in the database
    public void addWordToUserWords(Word newWord) {
        //Saves new learned word in the database
        docRefUser.collection("userWords")
                .document(newWord.getWord())
                .set(newWord)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("WordFragment", "Added: " + newWord.getWord());
                        //Notifies user
                        Toast.makeText(getActivity(),"Word saved!",Toast.LENGTH_SHORT).show();
                        //Removes learned word from the pool of words
                        removeLearnedWord();
                        saveBtn.setVisibility(View.INVISIBLE);
                        nextWord.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("WordFragment", "Could not add word", e);
                        Toast.makeText(getActivity(),"Could not save word. Try again!",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Deletes learned word from the pool of all words
    public void removeLearnedWord() {
        receivedWordList.remove(0);
    }

    //Opens new fragment with information about the new word
    public void openNextWord(View view) {
        nextWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If there are still not learned words in the words pool
                if (!receivedWordList.isEmpty()) {
                    //Opens word fragment for the following word
                    Fragment fragment = WordFragment.newInstance(receivedWordList);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_word");
                    transaction.commit();
                } else {
                    //If user learned all available words
                    //Displays message that there are no more words to learn
                    LinearLayout completed = (LinearLayout) view.findViewById(R.id.wordsLearned);
                    completed.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.INVISIBLE);
                    nextWord.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    //Closes this fragment and opens builder fragment
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