package com.example.engvoyage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

//Word class holds available words
public class Word implements Parcelable {
    private String word;
    private String pronunciation;
    private String meaningShort;
    private String meaningFull;
    private String usage;
    private String difficulty;

    //Default empty constructor
    public Word() {
    }

    public Word(String word, String pronunciation, String meaningShort, String meaningFull, String usage, String difficulty) {
        this.word = word;
        this.pronunciation = pronunciation;
        this.meaningShort = meaningShort;
        this.meaningFull = meaningFull;
        this.usage = usage;
        this.difficulty = difficulty;
    }

    protected Word(Parcel in) {
        word = in.readString();
        pronunciation = in.readString();
        meaningShort = in.readString();
        meaningFull = in.readString();
        usage = in.readString();
        difficulty = in.readString();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    public String getWord() {
        return word;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getMeaningShort() {
        return meaningShort;
    }

    public String getMeaningFull() {
        return meaningFull;
    }

    public String getUsage() {
        return usage;
    }

    public String getDifficulty() {
        return difficulty;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(word);
        dest.writeString(pronunciation);
        dest.writeString(meaningShort);
        dest.writeString(meaningFull);
        dest.writeString(usage);
        dest.writeString(difficulty);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Word other = (Word) obj;

        return word != null && word.equals(other.word);
    }
}
