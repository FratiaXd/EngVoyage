package com.example.engvoyage;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VocabularyFragment extends Fragment implements WordAdapter.ItemClickListener{

    private static final String ARG_PARAM1 = "wordList";
    private List<Word> receivedWordList;
    private RecyclerView recyclerView;
    private WordAdapter wordAdapter;

    public VocabularyFragment() {
        // Required empty public constructor
    }

    //New instance receives user learned words
    public static VocabularyFragment newInstance(List<Word> wordList) {
        VocabularyFragment fragment = new VocabularyFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM1, new ArrayList<>(wordList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Initializes arguments
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
        wordAdapter = new WordAdapter(receivedWordList, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vocabulary, container, false);
        displayEmptyText(view);
        returnToBuilder(view);
        initRecyclerView(view);
        return view;
    }

    //Initializes recyclerview with user learned words
    private void initRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewWords);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(wordAdapter);
    }

    //Displays message is there are no learned words yet
    public void displayEmptyText(View view) {
        TextView empty = (TextView) view.findViewById(R.id.noWords);
        if (receivedWordList.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.INVISIBLE);
        }
    }

    //Closes vocabulary fragment and opens builder fragment
    private void returnToBuilder(View view) {
        ImageButton back = (ImageButton) view.findViewById(R.id.goBackToBuilder1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getParentFragmentManager().beginTransaction();
                fr.replace(R.id.frame_layout, new BuilderFragment());
                fr.commit();
            }
        });
    }

    //Opens word info fragment
    @Override
    public void onItemClick(Word word) {
        //Passes the selected word object to the word info fragment
        Fragment fragment = WordInfoFragment.newInstance(word);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment, "fragment_word_info");
        transaction.addToBackStack(null);
        transaction.commit();
    }
}