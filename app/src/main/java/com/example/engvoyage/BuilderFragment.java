package com.example.engvoyage;

import android.os.Bundle;

import androidx.annotation.BinderThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BuilderFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db;
    private List<Word> wordListEasy;
    private List<Word> wordListIntermediate;
    private List<Word> wordListAdvanced;
    private List<Word> userWordList;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference docRefUser;

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
        //Initializes arguments and firebase elements
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();
        docRefUser = db.collection("users").document(uid);
        userWordList = new ArrayList<>();
        wordListEasy = new ArrayList<>();
        wordListIntermediate = new ArrayList<>();
        wordListAdvanced = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_builder, container, false);
        //Reads all words from the database
        readWords();
        openEasyWords(view);
        openAdvancedWords(view);
        openIntermediateWords(view);
        openVocabulary(view);
        openFlashcards(view);
        return view;
    }

    //Opens easy words fragment
    public void openEasyWords(View view) {
        Button easyBtn = (Button) view.findViewById(R.id.easyLevel);
        easyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If there are not learned words in the list
                if (!wordListEasy.isEmpty()) {
                    //Passes a list of easy not learned words to the word fragment
                    Fragment fragment = WordFragment.newInstance(wordListEasy);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_word");
                    transaction.commit();
                } else {
                    //If user already learned all easy words
                    Toast.makeText(getActivity(),"You learned all available words. Wait for new updates!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Opens intermediate words fragment
    public void openIntermediateWords(View view) {
        Button intermediateBtn = (Button) view.findViewById(R.id.intermediateLevel);

        intermediateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If there are not learned words in the list
                if (!wordListIntermediate.isEmpty()) {
                    //Passes a list of intermediate not learned words to the word fragment
                    Fragment fragment = WordFragment.newInstance(wordListIntermediate);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_word");
                    transaction.commit();
                } else {
                    //If user already learned all intermediate words
                    Toast.makeText(getActivity(),"You learned all available words. Wait for new updates!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Opens advanced words fragment
    public void openAdvancedWords(View view) {
        Button advancedBtn = (Button) view.findViewById(R.id.advancedLevel);

        advancedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If there are not learned words in the list
                if (!wordListAdvanced.isEmpty()) {
                    //Passes a list of advanced not learned words to the word fragment
                    Fragment fragment = WordFragment.newInstance(wordListAdvanced);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_word");
                    transaction.commit();
                } else {
                    //If user already learned all advanced words
                    Toast.makeText(getActivity(),"You learned all available words. Wait for new updates!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Opens vocabulary fragment
    public void openVocabulary(View view) {
        Button vocabBtn = (Button) view.findViewById(R.id.vocabulary);
        vocabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Passes user learned words list
                Fragment fragment = VocabularyFragment.newInstance(userWordList);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment, "fragment_word");
                transaction.commit();
            }
        });
    }

    //Opens flashcards fragment
    public void openFlashcards(View view) {
        Button flashBtn = (Button) view.findViewById(R.id.builderPractice);
        flashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If user has learned at least one word
                if (!userWordList.isEmpty()) {
                    //Opens flashcards fragment
                    Fragment fragment = FlashcardFragment.newInstance(userWordList);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, fragment, "fragment_flashcard");
                    transaction.commit();
                } else {
                    //If there are no learned words yet
                    //Notifies user
                    Toast.makeText(getActivity(),"You have not learned any words yet!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Reads all words stored in the database
    public void readWords() {
        db.collection("words")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("BuilderFragment", "Success");
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            buildWordList(document);
                        }
                        readLearnedWords();
                    } else {
                        Log.d("BuilderFragment", "Error", task.getException());
                    }
                });
    }

    //Builds three word lists depending on their difficulty
    private void buildWordList(QueryDocumentSnapshot document) {
        String word = document.getString("word");
        String pronun = document.getString("pronunciation");
        String meanSh = document.getString("meaningShort");
        String meanFl = document.getString("meaningFull");
        String usage = document.getString("usage");
        String diff = document.getString("difficulty");
        Word wordInfo = new Word(word, pronun, meanSh, meanFl, usage, diff);

        if (diff.equals("Easy")) {
            wordListEasy.add(wordInfo);
        } else if (diff.equals("Intermediate")) {
            wordListIntermediate.add(wordInfo);
        } else if (diff.equals("Advanced")) {
            wordListAdvanced.add(wordInfo);
        }
    }

    //Identifies words that user learned already
    public void readLearnedWords() {
        docRefUser.collection("userWords")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Build a list ofer user learned words
                                Word userWord = document.toObject(Word.class);
                                userWordList.add(userWord);
                            }
                            //Removes learned words
                            removeLearnedWords();
                        } else {
                            Log.d("BuilderFragment", "Error reading user words", task.getException());
                        }
                    }
                });
    }

    //Removes words that user already learned from the pool of available words
    public void removeLearnedWords() {
        wordListEasy.removeAll(userWordList);
        wordListIntermediate.removeAll(userWordList);
        wordListAdvanced.removeAll(userWordList);
    }
}