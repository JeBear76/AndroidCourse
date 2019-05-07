package com.jebear76.notekeeper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jebear76.notekeeper.NoteKeeperDatabaseContract.CourseInfoEntry;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder>{

    private final Context _context;
    private final LayoutInflater _layoutInflater;
    private Cursor _courseInfoCursor;

    public CourseRecyclerAdapter(Context context, Cursor courseInfoCursor) {
        _context = context;
        _courseInfoCursor = courseInfoCursor;
        _layoutInflater = LayoutInflater.from(_context);
    }

    public void changeCursor(Cursor courseInfoCursor){
        if(_courseInfoCursor != null)
            _courseInfoCursor.close();
        _courseInfoCursor = courseInfoCursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = _layoutInflater.inflate(R.layout.item_course_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        _courseInfoCursor.moveToPosition(position);
        viewHolder._textCourse.setText(_courseInfoCursor.getString(_courseInfoCursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE)));
    }

    @Override
    public int getItemCount() {
        return _courseInfoCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView _textCourse;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _textCourse = itemView.findViewById(R.id.text_course);
        }

    }
}
