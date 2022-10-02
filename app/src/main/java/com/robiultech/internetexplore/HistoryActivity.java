package com.robiultech.internetexplore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private EditText searchText;
    private RecyclerView visitedPagesView;
    ImageView searchimageView;

    private List<VisitedPage> visitedPages;
    private HistorySQLite historySQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        searchText = findViewById(R.id.historySearchText);
        searchimageView = findViewById(R.id.historySearchIcon);
        visitedPagesView = findViewById(R.id.rvHistoryList);
        ImageView clearHistory = findViewById(R.id.btn_delete_history);

        historySQLite = new HistorySQLite(this);
        visitedPages = historySQLite.getAllVisitedPages();

        visitedPagesView.setAdapter(new VisitedPagesAdapter());
        visitedPagesView.setLayoutManager(new LinearLayoutManager(this));

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historySQLite.clearHistory();
                visitedPages.clear();
                visitedPagesView.getAdapter().notifyDataSetChanged();
                isHistoryEmpty();
            }
        });

        isHistoryEmpty();

    }

    private class VisitedPagesAdapter extends RecyclerView.Adapter<VisitedPagesAdapter.VisitedPageItem> {
        @Override
        public VisitedPagesAdapter.VisitedPageItem onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VisitedPagesAdapter.VisitedPageItem(LayoutInflater.from(getApplicationContext()).inflate(R.layout.history_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VisitedPagesAdapter.VisitedPageItem holder, int position) {
            holder.bind(visitedPages.get(position));
        }

        @Override
        public int getItemCount() {
            return visitedPages.size();
        }

        class VisitedPageItem extends RecyclerView.ViewHolder {
            private TextView title;
            private TextView subtitle;
            private LinearLayout linearLayout;

            VisitedPageItem(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.row_history_title);
                subtitle = itemView.findViewById(R.id.row_history_subtitle);
                linearLayout = itemView.findViewById(R.id.linearLayout);

                itemView.findViewById(R.id.row_history_menu).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final PopupMenu popup = new PopupMenu(HistoryActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.history_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.history_delete:
                                        historySQLite.deleteFromHistory(visitedPages.get(getAdapterPosition()).link);
                                        visitedPages.remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                        Toast.makeText(HistoryActivity.this, "Deleted URL", Toast.LENGTH_SHORT).show();
                                        isHistoryEmpty();
                                        break;
                                    case R.id.history_copy:
                                        Log.d("TAG", "onMenuItemClick: " + visitedPages.get(getAdapterPosition()).link);
                                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        clipboardManager.setPrimaryClip(ClipData.newPlainText("Copied URL", visitedPages.get(getAdapterPosition()).link));
                                        Toast.makeText(HistoryActivity.this, "Copied URL", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        break;
                                }
                                return true;
                            }
                        });
                        popup.show();
                    }
                });

                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String url = visitedPages.get(getAdapterPosition()).link;
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        intent.putExtra("urls", url);
                        startActivity(intent);
                        finishAffinity();
                    }
                });

                searchimageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(HistoryActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        String keyword = searchText.getText().toString().trim();
                        historySQLite.getVisitedPagesByKeyword(keyword);
                    }
                });

            }

            void bind(VisitedPage page) {
                title.setText(page.title);
                subtitle.setText(page.link);
            }
        }
    }

    private void isHistoryEmpty() {
        if (visitedPages.isEmpty()) {
            findViewById(R.id.llNoHistory).setVisibility(View.VISIBLE);
            findViewById(R.id.llShowHistory).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.llNoHistory).setVisibility(View.INVISIBLE);
            findViewById(R.id.llShowHistory).setVisibility(View.VISIBLE);
        }
    }
}
