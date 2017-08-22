package com.example.kwy2868.practice.view;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kwy2868.practice.R;
import com.example.kwy2868.practice.model.Music;

import java.util.List;

public class MusicAdapter extends ArrayAdapter<Music> {

    private LayoutInflater layoutInflater;
    private List<Music> data;
    private int resource;

    private boolean isPrefer = false;

    public MusicAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Music> objects) {
        super(context, resource, objects);
        this.layoutInflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        this.resource = resource;
        this.data = objects;
    }

    public MusicAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Music> objects, boolean isPrefer) {
        super(context, resource, objects);
        this.layoutInflater = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        this.resource = resource;
        this.data = objects;
        this.isPrefer = isPrefer;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(resource, null);
        }

        ((TextView) convertView.findViewById(R.id.music_title_text)).setText(data.get(position).name);

        if (isPrefer) {
            ((TextView) convertView.findViewById(R.id.point_text)).setVisibility(View.VISIBLE);
            ((TextView) convertView.findViewById(R.id.point_text)).setText(Integer.toString(data.get(position).point));
        } else {
            ((TextView) convertView.findViewById(R.id.point_text)).setVisibility(View.GONE);
        }

        return convertView;
    }
}
