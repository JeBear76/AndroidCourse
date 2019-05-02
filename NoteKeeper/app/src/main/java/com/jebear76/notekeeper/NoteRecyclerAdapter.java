package com.jebear76.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder>{

    private final Context _context;
    private final LayoutInflater _layoutInflater;
    private List<NoteInfo> _noteInfoList;

    public NoteRecyclerAdapter(Context context, List<NoteInfo> noteInfoList) {
        _context = context;
        _noteInfoList = noteInfoList;
        _layoutInflater = LayoutInflater.from(_context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = _layoutInflater.inflate(R.layout.item_note_list, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        NoteInfo noteInfo = _noteInfoList.get(position);
        viewHolder._textCourse.setText(noteInfo.getCourse().getTitle());
        viewHolder._textTitle.setText(noteInfo.getTitle());
        viewHolder._currentPosition = position;
    }

    @Override
    public int getItemCount() {
        return _noteInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView _textTitle;
        public final TextView _textCourse;
        public int _currentPosition;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _textCourse = itemView.findViewById(R.id.text_course);
            _textTitle = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(_context,NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_POSITION, _currentPosition);
                    _context.startActivity(intent);
                }
            });
        }

    }
}
