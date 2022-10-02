package com.robiultech.internetexplore;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;

public class DownloadWithPauseResmueNew extends AppCompatActivity {

    Button buttonone,buttonCancelOne;
    TextView textViewProgressOne,textViewfilenameset;
    ProgressBar progressBarOne;
    int downloadIdOne;
    private static String dirPath;
    String urlsss="",filename="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_with_pause_resmue_new);
        dirPath = Utils.getRootDirPath(getApplicationContext());
        urlsss = getIntent().getStringExtra("urlss");
        filename = getIntent().getStringExtra("filenames");
        init();
        textViewfilenameset.setText(filename);
        onClickListenerOne();
    }

    private void init() {
        buttonone = findViewById(R.id.buttonOne);

        buttonCancelOne = findViewById(R.id.buttonCancel);

        textViewProgressOne = findViewById(R.id.textviewProgressone);
        textViewfilenameset = findViewById(R.id.textviewFilenameset);

        progressBarOne = findViewById(R.id.progressbarone);


    }
    private void onClickListenerOne() {
        buttonone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Status.RUNNING == PRDownloader.getStatus(downloadIdOne)) {
                    PRDownloader.pause(downloadIdOne);
                    return;
                }
                buttonone.setEnabled(false);
                progressBarOne.setIndeterminate(true);
                progressBarOne.getIndeterminateDrawable().setColorFilter(
                        Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);


                if (Status.PAUSED == PRDownloader.getStatus(downloadIdOne)) {
                    PRDownloader.resume(downloadIdOne);
                    return;
                }
                downloadIdOne = PRDownloader.download(urlsss,dirPath,""+filename)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {

                                progressBarOne.setIndeterminate(false);
                                buttonone.setEnabled(true);
                                buttonone.setText("pause");
                                buttonCancelOne.setEnabled(true);

                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {

                                buttonone.setText("Resume");

                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {

                                buttonone.setText("start");
                                buttonCancelOne.setEnabled(false);
                                progressBarOne.setProgress(0);
                                textViewProgressOne.setText("");
                                downloadIdOne = 0;
                                progressBarOne.setIndeterminate(false);

                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {

                                long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                progressBarOne.setProgress((int) progressPercent);
                                textViewProgressOne.setText(Utils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                                progressBarOne.setIndeterminate(false);
                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {

                                buttonone.setEnabled(false);
                                buttonCancelOne.setEnabled(false);
                                buttonone.setText("completed");

                            }

                            @Override
                            public void onError(Error error) {
                                buttonone.setText("start");
                                Toast.makeText(getApplicationContext(), getString(R.string.some_Error_Occur) + " " + "1", Toast.LENGTH_SHORT).show();
                                textViewProgressOne.setText("");
                                progressBarOne.setProgress(0);
                                downloadIdOne = 0;
                                buttonCancelOne.setEnabled(false);
                                progressBarOne.setIndeterminate(false);
                                buttonone.setEnabled(true);

                            }

                        });

            }
        });
        buttonCancelOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PRDownloader.cancel(downloadIdOne);
            }
        });
    }
}