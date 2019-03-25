package com.moodify.www.livejukebox;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SongListAdapter extends
        RecyclerView.Adapter<SongListAdapter.SongViewHolder> {
    private Context context;
    private static SongRecyclerViewClickListener listener;
    private int selected_position = -1;
    private final LinkedList<Song> m_song_list;
    private final HashMap<Long, Artist> m_artist_list;
    private LayoutInflater m_inflater;

    public SongListAdapter(Context context,
                           LinkedList<Song> song_list,
                           HashMap<Long, Artist> artist_list,
                           SongRecyclerViewClickListener listener){
        m_inflater = LayoutInflater.from(context);
        this.m_song_list = song_list;
        this.m_artist_list = artist_list;
        this.context = context;
        this.listener = listener;
    }
    @NonNull
    @Override
    public SongListAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item_view = m_inflater.inflate(R.layout.song_list_item,
                parent,false);
        return new SongViewHolder(item_view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SongListAdapter.SongViewHolder holder, final int position) {
        final int cur_pos = position;
        Song current_song = m_song_list.get(position);
        Artist current_artist = m_artist_list.get(current_song.getArtistId());
        holder.tv_song_title.setText(current_song.getTitle());
        holder.tv_song_artist.setText(current_artist.toString());
        holder.tv_song_original_artist.setText(current_song.getOriginalArtist());
        if(selected_position == position){
            holder.layout.setBackgroundResource(R.drawable.clicked_song_border);
        }
        else{
            holder.layout.setBackgroundResource(R.drawable.unclicked_song_border);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_position = cur_pos;
                    notifyDataSetChanged();
                    listener.songListClicked(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return m_song_list.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        final TextView tv_song_title;
        final TextView tv_song_artist;
        final TextView tv_song_original_artist;
        final LinearLayout layout;
        final SongListAdapter mAdapter;

        public SongViewHolder(View item_view, SongListAdapter adapter){
            super(item_view);
            layout = item_view.findViewById(R.id.layout_song_list_item);
            tv_song_title = item_view.findViewById(R.id.tv_song_title);
            tv_song_artist = item_view.findViewById(R.id.tv_song_artist);
            tv_song_original_artist = item_view.findViewById(R.id.tv_song_original_artist);
            this.mAdapter = adapter;
        }


    }
}
