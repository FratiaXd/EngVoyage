package com.example.engvoyage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private List<Course> courseList;
    private ItemClickListener clickListener;

    public CourseAdapter(List<Course> courseList, ItemClickListener clickListener) {
        this.courseList = courseList;
        this.clickListener = clickListener;
    }
    @NonNull
    @Override
    public CourseAdapter.CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);

        holder.itemView.setOnClickListener(v -> {
            clickListener.onItemClick(course);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {

        TextView nameTxt, durTxt;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.courseName);
            durTxt = itemView.findViewById(R.id.courseDur);
        }

        public void bind(Course course) {
            nameTxt.setText(course.courseName);
            durTxt.setText(course.courseDuration + " lessons");
        }
    }

    public interface ItemClickListener {
        public void onItemClick(Course course);
    }
}

