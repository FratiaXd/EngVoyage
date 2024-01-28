package com.example.engvoyage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        public CourseProgressViewHolder(View view) {
            super(view);
            nameTxt = itemView.findViewById(R.id.courseNameProgress);
            durTxt = itemView.findViewById(R.id.courseDurProgress);
        }

        public void bind(UserCourses userCourses) {
            nameTxt.setText(userCourses.getCourseName());
            String dur = userCourses.getCourseProgress() +
                    "/" + userCourses.getCourseInfo().getCourseDuration();
            durTxt.setText(dur);
        }
    }

    public interface ItemClickListener {
        public void onItemClick(UserCourses userCourses);
    }
}
