package com.example.demoprojectmusic.Controler;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.demoprojectmusic.R;

import java.util.List;

public class PodcastAdapter extends BaseAdapter {

    Context context;
    List<String> listPodcastAdapter;

    public PodcastAdapter(Context context, List<String> listPodcastAdapter) {
        this.context = context;
        this.listPodcastAdapter = listPodcastAdapter;
    }
    @Override
    public int getCount() {
        Log.d(TAG, "List size: " + listPodcastAdapter.size());
        return listPodcastAdapter.size();
    }

    @Override
    public Object getItem(int position) {
        return listPodcastAdapter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_list_library, null);
        }

        // Get the TextView in your custom layout
        TextView playlistNameTextView = view.findViewById(R.id.tv_namePlay);

        // Set the playlist name for the current item
        playlistNameTextView.setText(listPodcastAdapter.get(position));

        return view;
    }

}
