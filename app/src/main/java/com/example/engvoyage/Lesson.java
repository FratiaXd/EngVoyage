package com.example.engvoyage;

public class Lesson {
    private String material;
    private String practiceTask;
    private String practiceTrue;
    private String practiceFalse;

    public Lesson() {}

    public Lesson(String material, String practiceTask, String practiceTrue, String practiceFalse) {
        this.material = material;
        this.practiceTask = practiceTask;
        this.practiceTrue = practiceTrue;
        this.practiceFalse = practiceFalse;
    }

    public String getMaterial() {
        return material;
    }

    public String getPracticeTask() {
        return practiceTask;
    }

    public String getPracticeTrue() {
        return practiceTrue;
    }

    public String getPracticeFalse() {
        return practiceFalse;
    }
}
