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

    public String getCourseName() {
        return courseName;
    }

    public String getCourseDuration() {
        return courseDuration;
    }

    public String getCourseDesc() {
        return courseDesc;
    }
}
