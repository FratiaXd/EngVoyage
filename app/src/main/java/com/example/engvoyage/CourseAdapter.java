package com.example.engvoyage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import org.w3c.dom.Text;

import java.util.List;

//Adapter to display a complete course list
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
        ShapeableImageView coursePic;
        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.courseName);
            durTxt = itemView.findViewById(R.id.courseDur);
            coursePic = itemView.findViewById(R.id.coursePic2);
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

    public interface ItemClickListener {
        public void onItemClick(Course course);
    }
}

