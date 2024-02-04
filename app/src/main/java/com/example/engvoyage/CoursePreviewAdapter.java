package com.example.engvoyage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class CoursePreviewAdapter extends RecyclerView.Adapter<CoursePreviewAdapter.CoursePreviewViewHolder> {
    private List<Course> courseList;

    public CoursePreviewAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }
    @NonNull
    @Override
    public CoursePreviewAdapter.CoursePreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_preview, parent, false);
        return new CoursePreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoursePreviewAdapter.CoursePreviewViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CoursePreviewViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, durTxt;
        public CoursePreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.courseName1);
            durTxt = itemView.findViewById(R.id.courseDur1);
        }

        public void bind(Course course) {
            nameTxt.setText(course.courseName);
            durTxt.setText(course.courseDuration + " lessons");
        }
    }
}