package com.example.kwy2868.practice.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.kwy2868.practice.R;

import java.util.ArrayList;

/**
 * Created by kwy2868 on 2017-03-23.
 */
public class MenuAdapter extends BaseAdapter {
    public interface ButtonClickListener {
        void onButtonClick(int poisition);
    }
    ButtonClickListener buttonClickListener;
    ArrayList<String> items;
    Activity activity;
    Button button;

    public MenuAdapter(){

    }

    public MenuAdapter(Activity activity, ArrayList<String> items){
        this.activity = activity;
        this.items = items;
    }

    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(activity).inflate(R.layout.row, null);
        }
        button = (Button)convertView.findViewById(R.id.button);
        // 이거 안하면 리스트뷰에서 클릭이 안됨.
        button.setFocusable(false);
        button.setText(items.get(position));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonClickListener.onButtonClick(position);
            }
        });
        return convertView;
    }
}
