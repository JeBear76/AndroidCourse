package com.jebear76.notekeeper;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static com.jebear76.notekeeper.NoteKeeperDatabaseContract.*;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>{

    private final Context _context;
    private final LayoutInflater _layoutInflater;
    private Cursor _cursor;
    private int _course_id_index;
    private int _note_title_index;
    private int _note_id_index;

    public NoteRecyclerAdapter(Context context, Cursor cursor) {
        _context = context;
        _cursor = cursor;
        _layoutInflater = LayoutInflater.from(_context);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if(_cursor == null)
            return;
        _course_id_index = _cursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        _note_title_index = _cursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        _note_id_index = _cursor.getColumnIndex(NoteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor){
        if(_cursor!= null)
        _cursor.close();

        _cursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = _layoutInflater.inflate(R.layout.item_note_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        if(_cursor.moveToPosition(position)){
            viewHolder._textCourse.setText(_cursor.getString(_course_id_index));
            viewHolder._textTitle.setText(_cursor.getString(_note_title_index));
            viewHolder._currentID = _cursor.getInt(_note_id_index);
        }
    }

    @Override
    public int getItemCount() {
        if (_cursor != null)
            return _cursor.getCount();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView _textTitle;
        public final TextView _textCourse;
        public int _currentID;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _textCourse = itemView.findViewById(R.id.text_course);
            _textTitle = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(_context,NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, _currentID);
                    _context.startActivity(intent);
                }
            });
        }

    }
}
