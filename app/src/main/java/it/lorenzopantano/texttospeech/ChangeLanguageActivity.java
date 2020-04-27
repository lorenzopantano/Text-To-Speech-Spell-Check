package it.lorenzopantano.texttospeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChangeLanguageActivity extends AppCompatActivity {

    private RecyclerView langRecycler;
    private RecyclerView.Adapter langAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<String> languages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        setTitle("Choose a language");

        //Prendi la lista delle lingue
        Intent intent = getIntent();
        languages = intent.getStringArrayListExtra("avLanguages");

        //Recycler View Setup
        langRecycler = findViewById(R.id.rvLang);
        layoutManager = new LinearLayoutManager(this);
        langRecycler.setLayoutManager(layoutManager);
        langAdapter = new LangAdapter(languages);
        langRecycler.setAdapter(langAdapter);
    }


    //Adapter per la recyclerView
    public class LangAdapter extends RecyclerView.Adapter<LangAdapter.ViewHolder> {

        private ArrayList<String> languages;

        public LangAdapter(ArrayList<String> avLanguages) {
            languages = avLanguages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_recylerview, parent, false);
            ViewHolder viewHolder = new ViewHolder(constraintLayout);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvLang.setText(languages.get(position));
        }

        @Override
        public int getItemCount() {
            return languages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            TextView tvLang;
            ImageView ivFlag;

            public ViewHolder(@NonNull ConstraintLayout constraintLayout) {
                super(constraintLayout);
                tvLang = constraintLayout.findViewById(R.id.tvLang);
                ivFlag = constraintLayout.findViewById(R.id.ivFlag);
                tvLang.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

            }
        }
    }
}
