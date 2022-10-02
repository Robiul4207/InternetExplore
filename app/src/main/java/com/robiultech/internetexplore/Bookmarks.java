package com.robiultech.internetexplore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class Bookmarks extends AppCompatActivity {
    MyDbHandlerBook dbHandlerBook = new MyDbHandlerBook(this, null, null, 1);
    WebView mywebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        /*
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Bookmarks"); */

        final List<String> books = dbHandlerBook.databaseToString();
        if (books.size() > 0) {
            ArrayAdapter<String> myadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, books);
            ListView mylist = findViewById(R.id.listViewBookmark);
            mylist.setAdapter(myadapter);
            mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String url = books.get(position);
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtra("urls", url);
                    startActivity(intent);
                    finishAffinity();
                }
            });
            mylist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                    String url=books.get(position);
                    Toast.makeText(Bookmarks.this, "item Selected : "+url +" deleted", Toast.LENGTH_LONG).show();
                    dbHandlerBook.deleteUrl(url);
                    finish();
                    return false;
                }
            });
        }

    }
}