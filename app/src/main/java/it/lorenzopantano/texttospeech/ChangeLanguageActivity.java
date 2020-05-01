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
import java.util.Locale;

public class ChangeLanguageActivity extends AppCompatActivity {

    private RecyclerView langRecycler;
    private RecyclerView.Adapter langAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Locale> languages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        setTitle("Choose a language");

        //Prendi la lista delle lingue
        Bundle extras = getIntent().getExtras();
        languages = (ArrayList<Locale>) extras.get("avLanguages");

        //Recycler View Setup
        langRecycler = findViewById(R.id.rvLang);
        layoutManager = new LinearLayoutManager(this);
        langRecycler.setLayoutManager(layoutManager);
        langAdapter = new LangAdapter(languages);
        langRecycler.setAdapter(langAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent selectedLang = new Intent();
        selectedLang.putExtra("selectedLang", -1);
        setResult(RESULT_CANCELED, selectedLang);
        finish();
    }


    //Adapter per la recyclerView
    public class LangAdapter extends RecyclerView.Adapter<LangAdapter.ViewHolder> implements View.OnClickListener {

        private ArrayList<Locale> languages;

        public LangAdapter(ArrayList<Locale> avLanguages) {
            languages = avLanguages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ConstraintLayout constraintLayout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.languages_recylerview, parent, false);
            ViewHolder viewHolder = new ViewHolder(constraintLayout);
            constraintLayout.setOnClickListener(this);
            return viewHolder;
        }

        @Override
        public void onClick(View v) {
            //TODO: implementa il cambio lingua
            Intent selectedLang = new Intent();
            int itemPosition = langRecycler.getChildLayoutPosition(v);
            selectedLang.putExtra("selectedLang", itemPosition);
            setResult(RESULT_OK, selectedLang);
            finish();
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvLangRecy.setText(languages.get(position).getDisplayLanguage().substring(0,1).toUpperCase() + languages.get(position).getDisplayLanguage().substring(1));
        }

        @Override
        public int getItemCount() {
            return languages.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvLangRecy;

            public ViewHolder(@NonNull ConstraintLayout constraintLayout) {
                super(constraintLayout);
                tvLangRecy = constraintLayout.findViewById(R.id.tvLangRecy);
            }

        }
    }
}
