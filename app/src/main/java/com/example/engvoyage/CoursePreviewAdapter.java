package com.example.engvoyage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

//Adapter to display course preview on the home fragment
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
        ShapeableImageView coursePic;
        public CoursePreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.courseName1);
            durTxt = itemView.findViewById(R.id.courseDur1);
            coursePic = itemView.findViewById(R.id.coursePic1);
        }

        public void bind(Course course) {
            nameTxt.setText(course.courseName);
            durTxt.setText(course.courseDuration + " lessons");

            Glide.with(itemView.getContext())
                    .load(course.coverImageUrl)
                    .placeholder(R.drawable.loading_img)
                    .error(R.drawable.error)
                    .into(coursePic);
        }
    }
}