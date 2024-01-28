package com.example.engvoyage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class UserCourses implements Parcelable {
    private String courseName;
    private String courseProgress;
    private Course courseInfo;

    protected UserCourses(Parcel in) {
        courseName = in.readString();
        courseProgress = in.readString();
    }

    public static final Creator<UserCourses> CREATOR = new Creator<UserCourses>() {
        @Override
        public UserCourses createFromParcel(Parcel in) {
            return new UserCourses(in);
        }

        @Override
        public UserCourses[] newArray(int size) {
            return new UserCourses[size];
        }
    };

    public String getCourseName() {
        return courseName;
    }

    public String getCourseProgress() {
        return courseProgress;
    }

    public Course getCourseInfo() {
        return courseInfo;
    }

    public void setCourseProgress(String progress) {
        courseProgress = progress;
    }

    public void setCourseInfo(Course course) {
        courseInfo = course;
    }

    public UserCourses(String courseName, String courseProgress) {
        this.courseName = courseName;
        this.courseProgress = courseProgress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(courseName);
        dest.writeString(courseProgress);
    }
}