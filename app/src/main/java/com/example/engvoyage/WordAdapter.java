package com.example.engvoyage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder>{

    private List<Word> wordList;
    private ItemClickListener clickListener;
    public WordAdapter(List<Word> wordList, WordAdapter.ItemClickListener clickListener) {
        this.wordList = wordList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public WordAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.word, parent, false);
        return new WordAdapter.WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordAdapter.WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.bind(word);

        holder.itemView.setOnClickListener(v -> {
            clickListener.onItemClick(word);
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {

        TextView wordTxt;
        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordTxt = itemView.findViewById(R.id.wordPreview);
        }

        public void bind(Word word) {
            wordTxt.setText(word.getWord());
        }
    }

    public interface ItemClickListener {
        public void onItemClick(Word word);
    }
}
