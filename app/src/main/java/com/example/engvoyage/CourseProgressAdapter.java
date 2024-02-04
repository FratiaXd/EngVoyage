package com.example.engvoyage;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

//Adapter to display user enrolled courses and progress
public class CourseProgressAdapter extends RecyclerView.Adapter<CourseProgressAdapter.CourseProgressViewHolder> {

    private List<UserCourses> userCoursesList;
    private ItemClickListener clickListener;
    public CourseProgressAdapter(List<UserCourses> userCoursesList, ItemClickListener clickListener) {
        this.userCoursesList = userCoursesList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CourseProgressAdapter.CourseProgressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_progress, parent, false);
        return new CourseProgressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseProgressAdapter.CourseProgressViewHolder holder, int position) {
        UserCourses userCourses = userCoursesList.get(position);
        holder.bind(userCourses);

        holder.itemView.setOnClickListener(v -> {
            clickListener.onItemClick(userCourses);
        });
    }

    @Override
    public int getItemCount() {
        return userCoursesList.size();
    }

    public static class CourseProgressViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt, durTxt;
        ProgressBar progressBar;
        int currentProgress;
        public CourseProgressViewHolder(View view) {
            super(view);
            nameTxt = itemView.findViewById(R.id.courseNameProgress);
            durTxt = itemView.findViewById(R.id.courseDurProgress);
            progressBar = itemView.findViewById(R.id.progressBar);
        }

        public void bind(UserCourses userCourses) {
            nameTxt.setText(userCourses.getCourseName());
            int progressInt = Integer.parseInt(userCourses.getCourseProgress());
            int durationInt = Integer.parseInt(userCourses.getCourseDuration());
            if (progressInt <= durationInt) {
                String dur = userCourses.getCourseProgress() + "/" + userCourses.getCourseDuration();
                durTxt.setText(dur);
            } else {
                durTxt.setText("Completed");
            }
            progressBar.setProgress(Integer.parseInt(userCourses.getCourseProgress()));
            progressBar.setMax(Integer.parseInt(userCourses.getCourseDuration()) + 1);
        }
    }

    public interface ItemClickListener {
        public void onItemClick(UserCourses userCourses);
    }
}
