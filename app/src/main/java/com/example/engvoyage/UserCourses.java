package com.example.engvoyage;

public class UserCourses {
    private String courseName;
    private String courseProgress;

    public String getCourseName() {
        return courseName;
    }

    public String getCourseProgress() {
        return courseProgress;
    }

    public UserCourses(String courseName, String courseProgress) {
        this.courseName = courseName;
        this.courseProgress = courseProgress;
    }
}