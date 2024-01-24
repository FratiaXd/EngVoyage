package com.example.engvoyage;

public class Course {
    public String courseName;
    public String courseDuration;
    public String courseDesc;

    public Course() {}

    public Course(String name, String duration, String desc) {
        this.courseName = name;
        this.courseDuration = duration;
        this.courseDesc = desc;
    }
}
