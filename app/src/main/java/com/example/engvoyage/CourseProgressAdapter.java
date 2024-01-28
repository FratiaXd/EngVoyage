package com.example.engvoyage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CourseProgressAdapter extends RecyclerView.Adapter<CourseProgressAdapter.CourseProgressViewHolder> {

    private List<Course> courseList;
    public CourseProgressAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseProgressAdapter.CourseProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_progress, parent, false);
        return new CourseProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseProgressAdapter.CourseProgressViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseProgressViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt, durTxt;
        public CourseProgressViewHolder(View view) {
            super(view);
            nameTxt = itemView.findViewById(R.id.courseNameProgress);
            durTxt = itemView.findViewById(R.id.courseDurProgress);
        }

        public void bind(Course course) {
            nameTxt.setText(course.courseName);
            durTxt.setText(course.courseDuration);
        }
    }
}
