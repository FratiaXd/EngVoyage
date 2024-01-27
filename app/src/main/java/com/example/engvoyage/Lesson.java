package com.example.engvoyage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Lesson implements Parcelable {
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

    protected Lesson(Parcel in) {
        material = in.readString();
        practiceTask = in.readString();
        practiceTrue = in.readString();
        practiceFalse = in.readString();
    }

    public static final Creator<Lesson> CREATOR = new Creator<Lesson>() {
        @Override
        public Lesson createFromParcel(Parcel in) {
            return new Lesson(in);
        }

        @Override
        public Lesson[] newArray(int size) {
            return new Lesson[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(material);
        dest.writeString(practiceTask);
        dest.writeString(practiceTrue);
        dest.writeString(practiceFalse);
    }
}
