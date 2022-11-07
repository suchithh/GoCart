package com.Go.GoCart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MyProgressDialog {
    public CountDownTimer countdown;
    private AppCompatActivity activity;
    private AlertDialog dialog;
    public boolean a = true;
    int resultcode = 2;

    public MyProgressDialog(AppCompatActivity currActivity) {
        activity = currActivity;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_my_progress, null);
        final Button cancel = v.findViewById(R.id.negative);
        cancel.setOnClickListener(view -> {
            a = false;
            dialog.dismiss();
            activity.startActivityForResult(new Intent(activity, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY), resultcode);
            android.os.Process.killProcess(android.os.Process.myPid());
            activity.finish();
        });
        final TextView time = v.findViewById(R.id.time);
        builder.setView(v);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener((arg0, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                cancel.performClick();
            }
            return true;
        });
        activity.runOnUiThread(this::run);
        final Handler handler = new Handler();
        final int[] seconds = {0};
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (a) {
                    time.setText("Time taken: " + seconds[0] + " seconds");
                    seconds[0]++;
                    handler.postDelayed(this, 1000);
                    if (seconds[0] == 39) {
                        dialog.dismiss();
                        Toast.makeText(activity, "Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        countdown = new CountDownTimer(40000, 1000) {

            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                if (a) {
                    dialog.dismiss();
                    resultcode = 3;
                    cancel.performClick();
                    activity.finish();
                }
            }
        };
        countdown.start();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    private void run() {
        dialog.show();
    }
}
