package com.example.hellowold;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private TextView txtDateTimeFormat, txtCount;
    private EditText edtDate;
    private Button btnFormatTime, btnFormatDate, btnFormatDT, btnCheck;
    private ProgressBar progressBar,pbCheck;

    Calendar calendar = Calendar.getInstance();

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDateTimeFormat = findViewById(R.id.txtDateTimeFormat);
        edtDate = findViewById(R.id.edtDate);
        txtCount = findViewById(R.id.txtCount);
        btnCheck = findViewById(R.id.btnCheck);
        btnFormatTime = findViewById(R.id.btnFormatTime);
        btnFormatDate = findViewById(R.id.btnFormatDate);
        btnFormatDT = findViewById(R.id.btnFormatDT);
        progressBar = findViewById(R.id.progressBar);
        pbCheck = findViewById(R.id.pbCheck);

        final String time = "22:12-31/03/2020";
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfD = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDT = new SimpleDateFormat("HH:mm-dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfH = new SimpleDateFormat("HH:mm");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

        progressBar.setMax(10000);
        runProgressBar();

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                Date date;
                int S = 0;
                try {
                    int x = 0, y = 0;
                    date = sdfDT.parse(edtDate.getText().toString());
                    if (date != null) {
                        x = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    }
                    date = sdfDT.parse(getInstantDateTime());
                    if (date != null) {
                        y = countNumberOfDateTime(Integer.parseInt(sdfYear.format(date)), Integer.parseInt(sdfMonth.format(date)), Integer.parseInt(sdfDay.format(date)));
                    }
                    S = y-x;
                    txtCount.setText("" + S);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                pbCheck.setMax(20);
                pbCheck.setProgress(pbCheck.getMax() - S);
            }
        });

        btnFormatTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                try {
                    date = sdfH.parse(time);
                    assert date != null;
                    txtDateTimeFormat.setText(sdfH.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnFormatDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                try {
                    date = sdfDT.parse(time);
                    assert date != null;
                    txtDateTimeFormat.setText(sdfD.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnFormatDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date date;
                try {
                    date = sdfDT.parse(time);
                    assert date != null;
                    txtDateTimeFormat.setText(sdfDT.format(date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private String getInstantDateTime() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat("HH:ss-dd/MM/yyyy", Locale.US);
        return sdf.format(calendar.getTime());
    }

    int countNumberOfDateTime(int year, int month, int day) {
        if (month < 3) {
            year--;
            month += 12;
        }
        return 365 * year + year / 4 - year / 100 + year / 400 + (153 * month - 457) / 5 + day - 306;
    }

    private void runProgressBar() {
        final Timer t = new Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                count++;
                progressBar.setProgress(progressBar.getMax() - count);
                if (count == 10000) {
                    t.cancel();
                }
            }
        };
        t.schedule(tt, 0, 10);

    }
}
