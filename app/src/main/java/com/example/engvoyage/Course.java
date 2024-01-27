package com.example.engvoyage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Course implements Parcelable {
    public String courseName;
    public String courseDuration;
    public String courseDesc;

    public Course() {}

    public Course(String name, String duration, String desc) {
        this.courseName = name;
        this.courseDuration = duration;
        this.courseDesc = desc;
    }

    protected Course(Parcel in) {
        courseName = in.readString();
        courseDuration = in.readString();
        courseDesc = in.readString();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getCourseName() {
        return courseName;
    }

    public String getCourseDuration() {
        return courseDuration;
    }

    public String getCourseDesc() {
        return courseDesc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(courseName);
        dest.writeString(courseDuration);
        dest.writeString(courseDesc);
    }
}
