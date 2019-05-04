package com.jebear76.notekeeper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder>{

    private final Context _context;
    private final LayoutInflater _layoutInflater;
    private List<CourseInfo> _courseInfoList;

    public CourseRecyclerAdapter(Context context, List<CourseInfo> courseInfoList) {
        _context = context;
        _courseInfoList = courseInfoList;
        _layoutInflater = LayoutInflater.from(_context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = _layoutInflater.inflate(R.layout.item_course_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CourseInfo courseInfo = _courseInfoList.get(position);
        viewHolder._textCourse.setText(courseInfo.getTitle());
    }

    @Override
    public int getItemCount() {
        return _courseInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView _textCourse;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _textCourse = itemView.findViewById(R.id.text_course);
        }

    }
}
