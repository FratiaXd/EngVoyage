package com.example.engvoyage;

public class Word {
    private String word;
    private String pronunciation;
    private String meaningShort;
    private String meaningFull;
    private String usage;

    public Word() {
    }

    public Word(String word, String pronunciation, String meaningShort, String meaningFull, String usage) {
        this.word = word;
        this.pronunciation = pronunciation;
        this.meaningShort = meaningShort;
        this.meaningFull = meaningFull;
        this.usage = usage;
    }

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
}
