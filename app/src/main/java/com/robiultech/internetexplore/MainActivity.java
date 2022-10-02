package com.robiultech.internetexplore;

import static com.robiultech.internetexplore.iConstants.SEARCH_ENGINE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.webkit.WebSettingsCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    ImageButton btnRefresh, btnbookmarks;
    ImageView btnMic, searchBtn;
    Button btnfortab;
    ProgressBar progressBar;
    BottomNavigationView bottomNavigationView;
    EditText edittexturl;
    WebView webview;
    LinearLayout layNonet;
    public static HistorySQLite db;
    MyDbHandlerBook dbHandlerBook;
    boolean dark = false;
    private int desktopmodevalue = 0;
    public boolean multiple_files = true;
    private static String file_type = "*/*";
    private String cam_file_data = null;
    private ValueCallback<Uri> file_data;
    private ValueCallback<Uri[]> file_path;
    private final static int file_req_code = 1;
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;

    public Boolean WebviewLoaded = false;
    private Boolean SearchHasFocus = false;
    private float m_downX;


    @SuppressLint({"ClickableViewAccessibility", "SetJavaScriptEnabled"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // record audio permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        webview = findViewById(R.id.webView);
        layNonet = findViewById(R.id.layNonet);
        btnMic = findViewById(R.id.btnmic);
        btnRefresh = findViewById(R.id.btnrefresh);
        edittexturl = findViewById(R.id.edttexturl);
        edittexturl.setSelectAllOnFocus(true);

        searchBtn = findViewById(R.id.searchbtn);
        btnfortab = findViewById(R.id.btnTextViewForTab);
        btnbookmarks = findViewById(R.id.btnAddtobookmarks);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigationView = findViewById(R.id.mBottomNavigation);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
       // final MediaPlayer mp = MediaPlayer.create(this,R.raw.start_audio);
        db= new HistorySQLite(this);
        dbHandlerBook = new MyDbHandlerBook(this, null, null, 1);


        progressBar.setMax(100);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setDisplayZoomControls(false);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setAllowFileAccess(true);
        webview.loadUrl("http://www.bpsc.gov.bd/");

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                WebviewLoaded = true;
                webview.loadUrl("javascript:(function() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\");');}}})()");
                Log.e("WEBVIEWFIN", url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                progressBar.setVisibility(View.GONE);

            }
        });
        webview.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UpdateUi();
                    }
                }, 1000);

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                    }
                    break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                    }
                    break;
                }
                return false;
            }
        });

        progressBar.setProgress(0);
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                VisitedPage vp = new VisitedPage();
                vp.title = title;
                vp.link = view.getUrl();
                db.addPageToHistory(vp);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100)
                    progressBar.setVisibility(View.INVISIBLE);

                else
                    progressBar.setVisibility(View.VISIBLE);
                super.onProgressChanged(view, newProgress);

            }

            /*-- handling input[type="file"] requests for android API 21+ --*/
            @SuppressLint("QueryPermissionsNeeded")
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {

                if (file_permission() && Build.VERSION.SDK_INT >= 21) {
                    file_path = filePathCallback;
                    Intent takePictureIntent = null;
                    Intent takeVideoIntent = null;

                    boolean includeVideo = false;
                    boolean includePhoto = false;

                    /*-- checking the accept parameter to determine which intent(s) to include --*/

                    paramCheck:
                    for (String acceptTypes : fileChooserParams.getAcceptTypes()) {
                        String[] splitTypes = acceptTypes.split(", ?+");
                        /*-- although it's an array, it still seems to be the whole value; split it out into chunks so that we can detect multiple values --*/
                        for (String acceptType : splitTypes) {
                            switch (acceptType) {
                                case "*/*":
                                    includePhoto = true;
                                    includeVideo = true;
                                    break paramCheck;
                                case "image/*":
                                    includePhoto = true;
                                    break;
                                case "video/*":
                                    includeVideo = true;
                                    break;
                            }
                        }
                    }

                    if (fileChooserParams.getAcceptTypes().length == 0) {

                        /*-- no `accept` parameter was specified, allow both photo and video --*/

                        includePhoto = true;
                        includeVideo = true;
                    }

                    if (includePhoto) {
                        takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = create_image();
                                takePictureIntent.putExtra("PhotoPath", cam_file_data);
                            } catch (IOException ex) {
                                // Log.e(TAG, "Image file creation failed", ex);
                                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (photoFile != null) {
                                cam_file_data = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            } else {
                                cam_file_data = null;
                                takePictureIntent = null;
                            }
                        }
                    }

                    if (includeVideo) {
                        takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        if (takeVideoIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
                            File videoFile = null;
                            try {
                                videoFile = create_video();
                            } catch (IOException ex) {
                                //Log.e(TAG, "Video file creation failed", ex);
                                Toast.makeText(MainActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (videoFile != null) {
                                cam_file_data = "file:" + videoFile.getAbsolutePath();
                                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoFile));
                            } else {
                                cam_file_data = null;
                                takeVideoIntent = null;
                            }
                        }
                    }

                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType(file_type);
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }

                    Intent[] intentArray;
                    if (takePictureIntent != null && takeVideoIntent != null) {
                        intentArray = new Intent[]{takePictureIntent, takeVideoIntent};
                    } else if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else if (takeVideoIntent != null) {
                        intentArray = new Intent[]{takeVideoIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, file_req_code);
                    return true;
                } else {
                    return false;
                }
            }


        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.back:

                        webview.goBack();

                        break;

                    case R.id.forward:
                        if (webview.canGoForward()) {
                            webview.goForward();
                        }
                        break;

                    case R.id.stop:
                        webview.stopLoading();
                        break;

                    case R.id.refbtn:
                        layNonet.setVisibility(View.GONE);
                        webview.setVisibility(View.VISIBLE);
                        webview.reload();
                        break;

                    case R.id.browserhome:
                        layNonet.setVisibility(View.GONE);
                        webview.setVisibility(View.VISIBLE);
                        webview.loadUrl("https://google.com");
                        break;
                }
                return true;
            }
        });

        btnfortab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "btnfortab", Toast.LENGTH_SHORT).show();
            }
        });
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittexturl.selectAll();
                edittexturl.setText("");
                //use for open keypad when clear all text from edittext using this button
                InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                im.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                edittexturl.requestFocus();
            }
        });




        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = edittexturl.getText().toString().trim();
                //  iUtils.ShowToast(MainActivity.this,url);
                edittexturl.clearFocus();
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(edittexturl.getWindowToken(), 0);
                if (iUtils.checkURL(url)) {
                    //   iUtils.ShowToast(MainActivity.this,url);
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    webview.loadUrl(url);
                    WebviewLoaded = false;
                    edittexturl.setText(webview.getUrl());
                } else {
                    String Searchurl = String.format(SEARCH_ENGINE, url);
                    webview.loadUrl(Searchurl);
                    WebviewLoaded = false;
                    edittexturl.setText(webview.getUrl());
                }
            }
        });
        edittexturl.setOnFocusChangeListener(focusListener);




        /*
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isNetworkAvailable(MainActivity.this)){
                    webview.setVisibility(View.GONE);
                    layNonet.setVisibility(View.VISIBLE);
                }else{
                    webview.setVisibility(View.VISIBLE);
                    layNonet.setVisibility(View.GONE);
                }
                if (!edittexturl.getText().toString().isEmpty()) {
                    String address = edittexturl.getText().toString();
                    if (address.contains("http") || address.contains("https")) {
                        if (address.contains(".com") || address.contains(".net") || address.contains(".in")) {
                            webview.loadUrl(address);
                        } else {

                            webview.loadUrl("https://www.google.com/search?q=" + address.replace("http", "").replace("https", ""));
                        }
                    } else {


                        if (address.contains(".com") || address.contains(".net") || address.contains(".in")) {
                            webview.loadUrl("http://" + address);
                        } else {
                            webview.loadUrl("https://www.google.com/search?q=" + address.replace("http", "").replace("https", ""));

                        }


                       // webview.loadUrl("https://www.google.com/search?q=" + address.replace("http", "").replace("https", ""));

                    }
                }
            }
        });
        */


        btnMic.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch (View view, MotionEvent motionEvent){
        Toast.makeText(MainActivity.this, "Speak something", Toast.LENGTH_SHORT).show();
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            speechRecognizer.stopListening();
        }
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            btnMic.setImageResource(R.drawable.ic_mic_none_black_24dp);
            speechRecognizer.startListening(speechRecognizerIntent);
        }
        return false;
    }
    });
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
        @Override
        public void onReadyForSpeech (Bundle bundle){

        }

        @Override
        public void onBeginningOfSpeech () {
            edittexturl.setText("");
            edittexturl.setHint("Speak something...");
        }

        @Override
        public void onRmsChanged ( float v){

        }

        @Override
        public void onBufferReceived ( byte[] bytes){

        }

        @Override
        public void onEndOfSpeech () {

        }

        @Override
        public void onError ( int i){

        }

        @Override
        public void onResults (Bundle bundle){
            btnMic.setImageResource(R.drawable.ic_baseline_mic_off_24);
            ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            edittexturl.setText(data.get(0));
        }

        @Override
        public void onPartialResults (Bundle bundle){

        }

        @Override
        public void onEvent ( int i, Bundle bundle){

        }
    });

        /*
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, final String userAgent, String contentDisposition, String mimetype, long contentLength) {
                //Checking runtime permission for devices above Marshmallow.
                saveData();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.v("TAG", "Permission is granted");
                        downloadDialog(url, userAgent, contentDisposition, mimetype);
                        saveData();
                    } else {

                        Log.v("TAG", "Permission is revoked");
                        //requesting permissions.
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                    }
                } else {
                    //Code for devices below API 23 or Marshmallow
                    Log.v("TAG", "Permission is granted");
                    downloadDialog(url, userAgent, contentDisposition, mimetype);

                }
            }
        });

      */
        webview.setDownloadListener(new DownloadListener() {
        @Override
        public void onDownloadStart (String url, String userAgent, String contentDisposition, String
        mimetype,long contentLength){

            //Checking runtime permission for devices above Marshmallow.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    downloadDialog(url, userAgent, contentDisposition, mimetype);

                } else {


                    //requesting permissions.
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                }
            } else {
                //Code for devices below API 23 or Marshmallow

                downloadDialog(url, userAgent, contentDisposition, mimetype);

            }

        }
    });
        btnbookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressedNew();
                Toast.makeText(MainActivity.this, "Page added in Bookmark", Toast.LENGTH_SHORT).show();
            }
        });
        if(getIntent().getStringExtra("urls") != null) {
            webview.loadUrl(getIntent().getStringExtra("urls"));
        }

    //------ open any website------------//
    Uri uri = getIntent().getData();
        if(uri !=null)

    {
        String path = uri.toString();
        webview.loadUrl(path);
    }
    //------ end any website------------//

        if(!isNetworkAvailable(MainActivity .this))
    {
        webview.setVisibility(View.GONE);
        layNonet.setVisibility(View.VISIBLE);
    }else

    {
        webview.setVisibility(View.VISIBLE);
        layNonet.setVisibility(View.GONE);
    }




}//---- oncreate end


    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    //---- network check
    public static boolean isNetworkAvailable(Context context) {
        return ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo() != null;
    }

    private void onBackPressedNew() {
        Websites web = new Websites(webview.getUrl());
        dbHandlerBook.addUrl(web);
    }
    private void setDesktopmode(WebView webview, boolean enabled) {
        String newUserAgent = webview.getSettings().getUserAgentString();
        if (enabled) {
            try {
                String ua = webview.getSettings().getUserAgentString();
                String androidDoString = webview.getSettings().getUserAgentString()
                        .substring(ua.indexOf("("), ua.indexOf(")") + 1);
                newUserAgent = webview.getSettings().getUserAgentString()
                        .replace(androidDoString, "x11; Linux x86_64");

            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {
            newUserAgent = null;
        }
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setDisplayZoomControls(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setUserAgentString(newUserAgent);
        webview.getSettings().setUseWideViewPort(enabled);
        webview.getSettings().setLoadWithOverviewMode(enabled);
        webview.reload();

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutwo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.refresh:
                webview.reload();
                Toast.makeText(MainActivity.this, "history Done", Toast.LENGTH_SHORT).show();
                break;

            case R.id.history:
                Intent his = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(his);
                break;

            case R.id.setting:
                Intent intent = new Intent(MainActivity.this, BrowserSettings.class);
                startActivity(intent);
                break;

            case R.id.favouritestar:
                Intent book = new Intent(MainActivity.this, Bookmarks.class);
                startActivity(book);
                break;

            case R.id.download:
                Intent download = new Intent(MainActivity.this, DownloadWithPauseResmueNew.class);
                startActivity(download);
                break;

            case R.id.desktopMode:

                if (desktopmodevalue == 0) {
                   setDesktopmode(webview, true);
                    desktopmodevalue = 1;
                } else {
                    setDesktopmode(webview, false);
                    desktopmodevalue = 0;
                }
                break;

            case R.id.ScanqrCode:
                Intent i = new Intent(MainActivity.this, ScanQrCode.class);
                startActivity(i);
                break;

            case R.id.DarkOrLight:
                if (dark) {
                    WebSettingsCompat.setForceDark(webview.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    dark = true;
                } else {
                    WebSettingsCompat.setForceDark(webview.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    dark = true;
                }
                break;

            case R.id.ExitWebBrowser:
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.ic_round_exit)
                        .setTitle("Exit")
                        .setMessage("Are You Want to Exit This App")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).setNeutralButton("Help", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "Pres Yes to exit this app", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
                break;

            case R.id.aboutdeveloper:
                Intent about = new Intent(MainActivity.this, AboutDeveloper.class);
                startActivity(about);
                break;
        }
        return true;
    }

    /* \\ alternative download function
    private void downloader(String url, String userAgent, String contentDisposition, String mimetype) {

        //getting filename from url.
        final String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        //alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //title of alertdialog
        builder.setTitle("Downloading");
        //message of alertdialog
        builder.setMessage("We are trying to download: " + ' ' + filename);
        //if Yes button clicks.

        builder.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DownloadManager.Request created with url.

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                //cookie
                String cookie = CookieManager.getInstance().getCookie(url);
                //Add cookie and User-Agent to request
                request.addRequestHeader("Cookie", cookie);
                request.addRequestHeader("User-Agent", userAgent);
                //file scanned by MediaScannar
                request.allowScanningByMediaScanner();
                //Download is visible and its progress, after completion too.
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                //DownloadManager created
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                //Saving files in Download folder
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                //download enqued
                downloadManager.enqueue(request);


                BroadcastReceiver onComplete = new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        Toast.makeText(getApplicationContext(), "Download Complete ", Toast.LENGTH_SHORT).show();
                    }
                };

                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

            }
        });
        builder.setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel the dialog if Cancel clicks
                dialog.cancel();
                webview.goBack();
            }

        });
        //alertdialog shows.
        builder.show();
    }
*/

    	public void downloadDialog(final String url, final String userAgent, String contentDisposition, String mimetype) {
        //getting filename from url.
        final String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
        //alertdialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View vvr = getLayoutInflater().inflate(R.layout.custom_dialog_for_download,null);
        Button buttonYes = vvr.findViewById(R.id.button_yes);
        Button buttonNo = vvr.findViewById(R.id.button_no);
        EditText EditTextFileName = vvr.findViewById(R.id.editTextFileName);
        EditText EditTextFileUrl = vvr.findViewById(R.id.editTextFile_url);

        builder.setView(vvr);
        final  AlertDialog alertDialogs = builder.create();
        alertDialogs.setCancelable(false);

        EditTextFileName.setText(filename);
        EditTextFileUrl.setText(url);
        final String ur= EditTextFileUrl.getText().toString();
        final String fi = EditTextFileName.getText().toString();

        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intentUrls = new Intent(view.getContext(),DownloadWithPauseResmueNew.class);
                intentUrls.putExtra("urlss",ur);
                intentUrls.putExtra("filenames",fi);

                startActivity(intentUrls);

                alertDialogs.dismiss();

            }
        });
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialogs.dismiss();

            }
        });

        //title of alertdialog
        builder.setTitle(R.string.download_title);
        //message of alertdialog
        builder.setMessage(getString(R.string.download_file) + ' ' + filename);



        //if Yes button clicks.

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DownloadManager.Request created with url.
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                //cookie
                String cookie = CookieManager.getInstance().getCookie(url);
                //Add cookie and User-Agent to request
                request.addRequestHeader("Cookie", cookie);
                request.addRequestHeader("User-Agent", userAgent);
                //file scanned by MediaScannar
                request.allowScanningByMediaScanner();
                //Download is visible and its progress, after completion too.
                String urls = url;
                String title = filename;


                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                //DownloadManager created
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                //Saving files in Download folder
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);
                //download enqued
                downloadManager.enqueue(request);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancel the dialog if Cancel clicks

                String urls = url;
                String title = filename;
                dialog.cancel();
                webview.goBack();

            }

        });
        alertDialogs.show();

    }

    private View.OnFocusChangeListener focusListener = new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                SearchHasFocus = true;
            } else {
                SearchHasFocus = false;
            }
        }
    };
    private void UpdateUi() {
        String WebUrl = webview.getUrl();
        String SearchUrl = edittexturl.getText().toString();
        //  iUtils.ShowToast(MainActivity.this,WebUrl);
        if (!SearchHasFocus) {
            // if (!WebUrl.equals(SearchUrl)) {
            if (WebviewLoaded) {
                edittexturl.setText(WebUrl);

            }

        }

    }

/*
            Now we will write a function
      "onActivityResult"  for selecting the file from the Camera or SD card in android webview app.
            This function will be outside of "onCreate" method. See the  Below Code: */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;

            /*-- if file request cancelled; exited camera. we need to send null value to make future attempts workable --*/
            if (resultCode == Activity.RESULT_CANCELED) {
                file_path.onReceiveValue(null);
                return ;
            }

            /*-- continue if response is positive --*/
            if(resultCode== Activity.RESULT_OK){
                if(null == file_path){
                    return;
                }
                ClipData clipData;
                String stringData;

                try {
                    clipData = intent.getClipData();
                    stringData = intent.getDataString();
                }catch (Exception e){
                    clipData = null;
                    stringData = null;
                }
                if (clipData == null && stringData == null && cam_file_data != null) {
                    results = new Uri[]{Uri.parse(cam_file_data)};
                }else{
                    if (clipData != null) {
                        final int numSelectedFiles = clipData.getItemCount();
                        results = new Uri[numSelectedFiles];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            results[i] = clipData.getItemAt(i).getUri();
                        }
                    } else {
                        try {
                            Bitmap cam_photo = (Bitmap) intent.getExtras().get("data");
                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                            cam_photo.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            stringData = MediaStore.Images.Media.insertImage(this.getContentResolver(), cam_photo, null, null);
                        }catch (Exception ignored){}

                        results = new Uri[]{Uri.parse(stringData)};
                    }
                }
            }

            file_path.onReceiveValue(results);
            file_path = null;
        }else{
            if(requestCode == file_req_code){
                if(null == file_data) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                file_data.onReceiveValue(result);
                file_data = null;
            }
        }
    }
    // Now we will write function called "onConfigurationChanged"
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }


    //"file_permission" for checking and asking for required file permissions
    public boolean file_permission(){
        if(Build.VERSION.SDK_INT >=23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
            return false;
        }else{
            return true;
        }
    }

    // create_image
    private File create_image() throws IOException{
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName,".jpg",storageDir);
    }
    //Now we will write function for video file
    private File create_video() throws IOException {
        @SuppressLint("SimpleDateFormat") String file_name    = new SimpleDateFormat("yyyy_mm_ss").format(new Date());
        String new_name     = "file_"+file_name+"_";
        File sd_directory   = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(new_name, ".3gp", sd_directory);
    }
    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to Exit ?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.super.onBackPressed();


                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                }
            });
           /*
            alternative
            builder.show()
            */
            AlertDialog dialog = builder.create();
            dialog.show();

        }

    }


}