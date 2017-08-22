package com.example.kwy2868.practice.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class DialogUtil {
    public static void showOKDialog(Context context, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setMessage(message);
        alert.show();
    }
}
