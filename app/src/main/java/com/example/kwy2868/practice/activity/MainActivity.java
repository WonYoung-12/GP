package com.example.kwy2868.practice.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kwy2868.practice.R;
import com.example.kwy2868.practice.model.User;
import com.example.kwy2868.practice.view.MenuAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    public static final int MY_PERMISSION_REQUEST_STORAGE = 1;
    ArrayList<String> items;
    MenuAdapter adapter;
    ListView listView;

    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkPermission();
        setUser();
    }

    private void setUser() {
        user = ((User) getIntent().getSerializableExtra("user"));
        if(!TextUtils.isEmpty(user.thumbnailImagePath)) {
            Picasso.with(this)
                    .load(user.thumbnailImagePath)
                    .into(((ImageView) findViewById(R.id.user_image)));
        }
        ((TextView) findViewById(R.id.user_name_text)).setText(user.nickname);
    }

    void init(){
        listView = (ListView)findViewById(R.id.listview);
        items =  new ArrayList<>();
        // 버튼 리스트들 생성.
        items.add("뇌파 측정");
        items.add("음악 선호도");
        items.add("종료");

        // 어댑터 객체가 데이터를 관리.
        adapter = new MenuAdapter(this, items);
        adapter.setButtonClickListener(new MenuAdapter.ButtonClickListener() {
            @Override
            public void onButtonClick(int position) {
                Intent intent;
                switch (position) {
                    // 뇌파 측정
                    case 0:
                        intent = new Intent(MainActivity.this, CheckEmotionActivity.class);
                        startActivity(intent);
                        break;
//                    case 2:
//                        intent = new Intent(MainActivity.this, MusicListActivity.class);
//                        startActivity(intent);
//                        break;
                    case 1:
                        intent = new Intent(MainActivity.this, CheckEmotionForPreferActivity.class);
                        startActivity(intent);
                        break;
                    // 종료
                    case 2:
                        finish();
                        System.out.println("종료");
                        break;
                }
            }
        });
        listView.setAdapter(adapter);
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.BLUETOOTH)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_STORAGE);

                // MY_PERMISSION_REQUEST_STORAGE is an
                // app-defined int constant

            } else {
                // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
}
